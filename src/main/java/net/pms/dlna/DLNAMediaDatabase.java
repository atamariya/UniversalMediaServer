/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2008  A.Brochard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.pms.dlna;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.awt.Component;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.h2.engine.Constants;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.Messages;
import net.pms.PMS;
import net.pms.configuration.FormatConfiguration;
import net.pms.configuration.PmsConfiguration;
import net.pms.formats.Format;
import net.pms.formats.v2.SubtitleType;

/**
 * This class provides methods for creating and maintaining the database where
 * media information is stored. Scanning media and interpreting the data is
 * intensive, so the database is used to cache scanned information to be reused
 * later.
 */
public class DLNAMediaDatabase implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DLNAMediaDatabase.class);
	private static final PmsConfiguration configuration = PMS.getConfiguration();

	private String url;
	private String dbDir;
	private String dbName;
	public static final String NONAME = "###";
	private Thread scanner;
	private JdbcConnectionPool cp;
	private int dbCount;

	/**
	 * The database version should be incremented when we change anything to
	 * do with the database since the last released version.
	 */
	private final String latestVersion = "7";
	
	public enum DataType { INT, DOUBLE, STRING, TIME };

	class Param {
		DataType type;
		Object value;
		public Param(DataType type, Object value) {
			this.type = type;
			this.value = value;
		}
	}
	
	// Database column sizes
	private final int SIZE_CODECV = 32;
	private final int SIZE_FRAMERATE = 32;
	private final int SIZE_ASPECTRATIO_DVDISO = 32;
	private final int SIZE_ASPECTRATIO_CONTAINER = 5;
	private final int SIZE_ASPECTRATIO_VIDEOTRACK = 5;
	private final int SIZE_AVC_LEVEL = 3;
	private final int SIZE_CONTAINER = 32;
	private final int SIZE_MATRIX_COEFFICIENTS = 16;
	private final int SIZE_MODEL = 128;
	private final int SIZE_MUXINGMODE = 32;
	private final int SIZE_FRAMERATE_MODE = 16;
	private final int SIZE_STEREOSCOPY = 255;
	private final int SIZE_LANG = 3;
	private final int SIZE_TITLE = 255;
	private final int SIZE_SAMPLEFREQ = 16;
	private final int SIZE_CODECA = 32;
	private final int SIZE_ALBUM = 255;
	private final int SIZE_ARTIST = 255;
	private final int SIZE_SONGNAME = 255;
	private final int SIZE_GENRE = 64;
	private final int SIZE_GENRE_FILE = 255;

    private Server tcpServer;
    private Server webServer;

	public DLNAMediaDatabase(String name) {
		dbName = name;
		File profileDirectory = new File(configuration.getProfileDirectory());
		dbDir = new File(profileDirectory.isDirectory() ? configuration.getProfileDirectory() : null, "database").getAbsolutePath();
		url = Constants.START_URL + "tcp://localhost/" + dbDir + File.separator + dbName;
		LOGGER.debug("Using database URL: " + url);
		LOGGER.info("Using database located at: " + dbDir);

		try {
			tcpServer = Server.createTcpServer("-tcpAllowOthers").start();
			webServer = Server.createWebServer("-webAllowOthers").start();
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException | SQLException e) {
			LOGGER.error(null, e);
		}

		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(url);
		ds.setUser("sa");
		ds.setPassword("");
		cp = JdbcConnectionPool.create(ds);
	}

	/**
	 * Gets the name of the database file
	 *
	 * @return The filename
	 */
	public String getDatabaseFilename() {
		if (dbName == null || dbDir == null) {
			return null;
		} else {
			return dbDir + File.separator + dbName;
		}
	}

	/**
	 * Gets a new connection from the connection pool if one is available. If
	 * not waits for a free slot until timeout.<br>
	 * <br>
	 * <strong>Important: Every connection must be closed after use</strong>
	 *
	 * @return the new connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return cp.getConnection();
	}

	public void init(boolean force) {
		dbCount = -1;
		String version = null;
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;

		try {
			conn = getConnection();
		} catch (SQLException se) {
			final File dbFile = new File(dbDir + File.separator + dbName + ".data.db");
			final File dbDirectory = new File(dbDir);
			if (dbFile.exists() || (se.getErrorCode() == 90048)) { // Cache is corrupt or a wrong version, so delete it
				FileUtils.deleteQuietly(dbDirectory);
				if (!dbDirectory.exists()) {
					LOGGER.info("The database has been deleted because it was corrupt or had the wrong version");
				} else {
					if (!net.pms.PMS.isHeadless()) {
						JOptionPane.showMessageDialog(
							SwingUtilities.getWindowAncestor((Component) PMS.get().getFrame()),
							String.format(Messages.getString("DLNAMediaDatabase.5"), dbDir),
							Messages.getString("Dialog.Error"),
							JOptionPane.ERROR_MESSAGE);
					}
					LOGGER.error("Damaged cache can't be deleted. Stop the program and delete the folder \"" + dbDir + "\" manually");
					PMS.get().getRootFolder(null).stopScan();
					configuration.setUseCache(false);
					return;
				}
			} else {
				LOGGER.error("Database connection error: " + se.getMessage());
				LOGGER.trace("", se);
				RootFolder rootFolder = PMS.get().getRootFolder(null);
				if (rootFolder != null) {
					rootFolder.stopScan();
				}
				configuration.setUseCache(false);
				return;
			}
		} finally {
			close(conn);
		}

		try {
			conn = getConnection();

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT count(*) FROM FILES");
			if (rs.next()) {
				dbCount = rs.getInt(1);
			}
			rs.close();
			stmt.close();

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT VALUE FROM METADATA WHERE KEY = 'VERSION'");
			if (rs.next()) {
				version = rs.getString(1);
			}
		} catch (SQLException se) {
			if (se.getErrorCode() != 42102) { // Don't log exception "Table "FILES" not found" which will be corrected in following step
				LOGGER.error(null, se);
			}
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}

		// Recreate database if it is not the latest version.
		boolean force_reinit = !latestVersion.equals(version);
		if (force || dbCount == -1 || force_reinit) {
			LOGGER.debug("Database will be (re)initialized");
			try {
				conn = getConnection();
				executeUpdate(conn, "DROP TABLE FILES");
				executeUpdate(conn, "DROP TABLE AUDIOTRACKS");
				executeUpdate(conn, "DROP TABLE SUBTRACKS");
				executeUpdate(conn, "DROP TABLE ARTISTS");
				executeUpdate(conn, "DROP TABLE REGEXP_RULES");
				// delete metadata at the last
				executeUpdate(conn, "DROP TABLE METADATA");
			} catch (SQLException se) {
				if (se.getErrorCode() != 42102) { // Don't log exception "Table "FILES" not found" which will be corrected in following step
					LOGGER.error(null, se);
				}
			}
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("CREATE TABLE FILES (");
				sb.append("  ID                      INT AUTO_INCREMENT");
				sb.append(", FILENAME                VARCHAR2(1024)   NOT NULL");
				sb.append(", MODIFIED                TIMESTAMP        NOT NULL");
				sb.append(", TYPE                    INT");
				sb.append(", THUMBPROCESSED          INT");
				sb.append(", DURATION                DOUBLE");
				sb.append(", BITRATE                 INT");
				sb.append(", WIDTH                   INT");
				sb.append(", HEIGHT                  INT");
				sb.append(", SIZE                    NUMERIC");
				sb.append(", CODECV                  VARCHAR2(").append(SIZE_CODECV).append(')');
				sb.append(", FRAMERATE               VARCHAR2(").append(SIZE_FRAMERATE).append(')');
				sb.append(", ASPECT                  VARCHAR2(").append(SIZE_ASPECTRATIO_DVDISO).append(')');
				sb.append(", ASPECTRATIOCONTAINER    VARCHAR2(").append(SIZE_ASPECTRATIO_CONTAINER).append(')');
				sb.append(", ASPECTRATIOVIDEOTRACK   VARCHAR2(").append(SIZE_ASPECTRATIO_VIDEOTRACK).append(')');
				sb.append(", REFRAMES                TINYINT");
				sb.append(", AVCLEVEL                VARCHAR2(").append(SIZE_AVC_LEVEL).append(')');
				sb.append(", BITSPERPIXEL            INT");
				sb.append(", THUMB                   BINARY");
				sb.append(", CONTAINER               VARCHAR2(").append(SIZE_CONTAINER).append(')');
				sb.append(", MODEL                   VARCHAR2(").append(SIZE_MODEL).append(')');
				sb.append(", EXPOSURE                INT");
				sb.append(", ORIENTATION             INT");
				sb.append(", ISO                     INT");
				sb.append(", MUXINGMODE              VARCHAR2(").append(SIZE_MUXINGMODE).append(')');
				sb.append(", FRAMERATEMODE           VARCHAR2(").append(SIZE_FRAMERATE_MODE).append(')');
				sb.append(", STEREOSCOPY             VARCHAR2(").append(SIZE_STEREOSCOPY).append(')');
				sb.append(", MATRIXCOEFFICIENTS      VARCHAR2(").append(SIZE_MATRIX_COEFFICIENTS).append(')');
				sb.append(", EMBEDDEDFONTEXISTS      BIT              NOT NULL");
				sb.append(", TITLE			         VARCHAR2(").append(SIZE_TITLE).append(')');
				sb.append(", UPPER_TITLE       		 VARCHAR2(").append(SIZE_TITLE).append(") AS UPPER(TITLE)");
//				sb.append(", TITLEVIDEOTRACK         VARCHAR2(").append(SIZE_TITLE).append(')');
				sb.append(", VIDEOTRACKCOUNT         INT");
				sb.append(", IMAGECOUNT              INT");
				sb.append(", BITDEPTH                INT");
                sb.append(", YEAR                    VARCHAR2(4)");
                sb.append(", GENRE                   VARCHAR2(").append(SIZE_GENRE_FILE).append(')');
                sb.append(", UPPER_GENRE             VARCHAR2(").append(SIZE_GENRE_FILE).append(") AS UPPER(GENRE)");
				sb.append(", PLAYPOS                 DOUBLE");
				sb.append(", PLAYCOUNT               INT");
				sb.append(", LASTPLAYED              TIMESTAMP");
				sb.append(", constraint PK1 primary key (FILENAME, MODIFIED))");
				executeUpdate(conn, sb.toString());
				
                sb = new StringBuilder();
                sb.append("CREATE TABLE GENRES (");
                sb.append("  ID                INT              AUTO_INCREMENT");
                sb.append(", TYPE              INT");
                sb.append(", NAME              VARCHAR2(").append(SIZE_GENRE).append(')');
                sb.append(", UPPER_NAME        VARCHAR2(").append(SIZE_GENRE).append(") AS UPPER(NAME)");
                sb.append(", constraint PKGENRES primary key (TYPE, NAME))");
                executeUpdate(conn, sb.toString());

                sb = new StringBuilder();
				sb.append("CREATE TABLE AUDIOTRACKS (");
				sb.append("  FILEID            INT              NOT NULL");
				sb.append(", ID                INT              AUTO_INCREMENT");
				sb.append(", LANG              VARCHAR2(").append(SIZE_LANG).append(')');
//				sb.append(", TITLE             VARCHAR2(").append(SIZE_TITLE).append(')');
				sb.append(", NRAUDIOCHANNELS   NUMERIC");
				sb.append(", SAMPLEFREQ        VARCHAR2(").append(SIZE_SAMPLEFREQ).append(')');
				sb.append(", CODECA            VARCHAR2(").append(SIZE_CODECA).append(')');
				sb.append(", BITSPERSAMPLE     INT");
				sb.append(", ALBUM             VARCHAR2(").append(SIZE_ALBUM).append(')');
				sb.append(", ARTIST            VARCHAR2(").append(SIZE_ARTIST).append(')');
//				sb.append(", SONGNAME          VARCHAR2(").append(SIZE_SONGNAME).append(')');
				sb.append(", UPPER_ALBUM       VARCHAR2(").append(SIZE_ALBUM).append(") AS UPPER(ALBUM)");
				sb.append(", UPPER_ARTIST      VARCHAR2(").append(SIZE_ARTIST).append(") AS UPPER(ARTIST)");
				sb.append(", TRACK             INT");
				sb.append(", DELAY             INT");
				sb.append(", MUXINGMODE        VARCHAR2(").append(SIZE_MUXINGMODE).append(')');
				sb.append(", BITRATE           INT");
				sb.append(", constraint PKAUDIO primary key (FILEID, ID)");
				sb.append(", foreign key (FILEID) REFERENCES FILES(ID) ON DELETE CASCADE)");
				executeUpdate(conn, sb.toString());
				
				sb = new StringBuilder();
				sb.append("CREATE TABLE SUBTRACKS (");
				sb.append("  FILEID   INT              NOT NULL");
				sb.append(", ID       INT              AUTO_INCREMENT");
				sb.append(", LANG     VARCHAR2(").append(SIZE_LANG).append(')');
				sb.append(", TITLE    VARCHAR2(").append(SIZE_TITLE).append(')');
				sb.append(", TYPE     INT");
				sb.append(", constraint PKSUB primary key (FILEID, ID)");
				sb.append(", foreign key (FILEID) REFERENCES FILES(ID) ON DELETE CASCADE)");
				executeUpdate(conn, sb.toString());
				
	            sb = new StringBuilder();
	            sb.append("CREATE TABLE IMDB (");
	            sb.append("  FILEID              INT NOT NULL");
	            sb.append(", IMDBID              INT");
                sb.append(", constraint PKIMDB primary key (FILEID)");
                sb.append(", foreign key (FILEID) REFERENCES FILES(ID) ON DELETE CASCADE)");
	            executeUpdate(conn, sb.toString());

				executeUpdate(conn, "CREATE TABLE ARTISTS (NAME VARCHAR2(255) PRIMARY KEY, MODIFIED TIMESTAMP NOT NULL);");
				
				executeUpdate(conn, "CREATE INDEX IDXTITLE_U on FILES (UPPER_TITLE asc);");
				executeUpdate(conn, "CREATE INDEX IDXLASTPLAYED on FILES (LASTPLAYED asc);");
				executeUpdate(conn, "CREATE INDEX IDXMODIFIED on FILES (MODIFIED asc);");
				executeUpdate(conn, "CREATE INDEX IDXARTIST on AUDIOTRACKS (ARTIST asc);");
				executeUpdate(conn, "CREATE INDEX IDXALBUM on AUDIOTRACKS (ALBUM asc);");
				executeUpdate(conn, "CREATE INDEX IDXARTIST_U on AUDIOTRACKS (UPPER_ARTIST asc);");
				executeUpdate(conn, "CREATE INDEX IDXALBUM_U on AUDIOTRACKS (UPPER_ALBUM asc);");
				executeUpdate(conn, "CREATE INDEX IDXGENRE_U on FILES (UPPER_GENRE asc);");
				executeUpdate(conn, "CREATE INDEX IDXYEAR on FILES (YEAR asc);");

				executeUpdate(conn, "CREATE TABLE REGEXP_RULES ( ID VARCHAR2(255) PRIMARY KEY, RULE VARCHAR2(255), ORDR NUMERIC);");
				executeUpdate(conn, "INSERT INTO REGEXP_RULES VALUES ( '###', '(?i)^\\W.+', 0 );");
				executeUpdate(conn, "INSERT INTO REGEXP_RULES VALUES ( '0-9', '(?i)^\\d.+', 1 );");

				executeUpdate(conn, "CREATE TABLE METADATA (KEY VARCHAR2(255) NOT NULL, VALUE VARCHAR2(255) NOT NULL)");
				executeUpdate(conn, "INSERT INTO METADATA VALUES ('VERSION', '" + latestVersion + "')");

				// Retrieve the alphabet property value and split it
				String[] chars = Messages.getString("DLNAMediaDatabase.1").split(",");

				for (int i = 0; i < chars.length; i++) {
					// Create regexp rules for characters with a sort order based on the property value
					executeUpdate(conn, "INSERT INTO REGEXP_RULES VALUES ( '" + chars[i] + "', '(?i)^" + chars[i] + ".+', " + (i + 2) + " );");
				}

				LOGGER.debug("Database initialized");
			} catch (SQLException se) {
				LOGGER.info("Error in table creation: " + se.getMessage());
			} finally {
				close(conn);
			}
		} else {
			LOGGER.debug("Database file count: " + dbCount);
			LOGGER.debug("Database version: " + latestVersion);
		}
	}

	private void executeUpdate(Connection conn, String sql) throws SQLException {
		if (conn != null) {
			try (Statement stmt = conn.createStatement()) {
				stmt.executeUpdate(sql);
			}
		}
	}

	public boolean isDataExists(String name, long modified) {
		boolean found = false;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM FILES WHERE FILENAME = ? AND MODIFIED = ?");
			stmt.setString(1, name);
			stmt.setTimestamp(2, new Timestamp(modified));
			rs = stmt.executeQuery();
			while (rs.next()) {
				found = true;
			}
		} catch (SQLException se) {
			LOGGER.error(null, se);
			return false;
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return found;
	}
	
	public List<DLNAMediaInfo> getData(String name, long modified) {
		List<Param> params = new ArrayList<>();
		params.add(new Param(DataType.STRING, name));
		params.add(new Param(DataType.TIME, new Timestamp(modified)));
		return query("SELECT * FROM FILES WHERE FILENAME = ? AND MODIFIED = ?", params);
	}
	
	public List<DLNAMediaInfo> query(String sql, List<Param> params) {
		List<DLNAMediaInfo> list = null;
		Connection conn = null;
		try {
			conn = getConnection();
			ResultSet rs = executeQuery(conn, sql, params, false);
			if (rs != null)
				list = populateMediaInfo(conn, rs);
		} catch (SQLException e) {
			LOGGER.error(null, e);
		} finally {
//			close(rs);
//			close(stmt);
			close(conn);
		}
		return list;
	}

	private ResultSet executeQuery(Connection conn, String sql, List<Param> params, boolean update) {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);

			if (params != null) {
				int i = 1;
				for (Param param : params) {
					switch (param.type) {
					case INT:
						stmt.setInt(i++, (Integer) param.value);
						break;
					case DOUBLE:
						stmt.setDouble(i++, (Double) param.value);
						break;
					case STRING:
						stmt.setString(i++, (String) param.value);
						break;
					case TIME:
						stmt.setTimestamp(i++, (Timestamp) param.value);
						break;
					default:
						break;
					}
				}
			}

			if (update)
				stmt.executeUpdate();
			else
				rs = stmt.executeQuery();
		} catch (SQLException se) {
			LOGGER.error(null, se);
			return null;
		}
		return rs;
	}

	protected List<DLNAMediaInfo> populateMediaInfo(Connection conn, ResultSet rs)
			throws SQLException {
		List<DLNAMediaInfo> list = new ArrayList<>();
		while (rs.next()) {
			DLNAMediaInfo media = new DLNAMediaInfo();
			int id = rs.getInt("ID");
			media.setFileId(rs.getString("ID"));
			media.setFileName(rs.getString("FILENAME"));
			media.setDuration(toDouble(rs, "DURATION"));
			media.setThumbready(rs.getInt("THUMBPROCESSED") == 1);
			media.setBitrate(rs.getInt("BITRATE"));
			media.setWidth(rs.getInt("WIDTH"));
			media.setHeight(rs.getInt("HEIGHT"));
			media.setSize(rs.getLong("SIZE"));
			media.setCodecV(rs.getString("CODECV"));
			media.setFrameRate(rs.getString("FRAMERATE"));
			media.setAspectRatioDvdIso(rs.getString("ASPECT"));
			media.setAspectRatioContainer(rs.getString("ASPECTRATIOCONTAINER"));
			media.setAspectRatioVideoTrack(rs.getString("ASPECTRATIOVIDEOTRACK"));
			media.setReferenceFrameCount(rs.getByte("REFRAMES"));
			media.setAvcLevel(rs.getString("AVCLEVEL"));
			media.setBitsPerPixel(rs.getInt("BITSPERPIXEL"));
			media.setThumb(rs.getBytes("THUMB"));
			media.setContainer(rs.getString("CONTAINER"));
			media.setModel(rs.getString("MODEL"));
			if (media.getModel() != null && !FormatConfiguration.JPG.equals(media.getContainer())) {
				media.setExtrasAsString(media.getModel());
			}
			media.setExposure(rs.getInt("EXPOSURE"));
			media.setOrientation(rs.getInt("ORIENTATION"));
			media.setIso(rs.getInt("ISO"));
			media.setMuxingMode(rs.getString("MUXINGMODE"));
			media.setFrameRateMode(rs.getString("FRAMERATEMODE"));
			media.setStereoscopy(rs.getString("STEREOSCOPY"));
			media.setMatrixCoefficients(rs.getString("MATRIXCOEFFICIENTS"));
			media.setEmbeddedFontExists(rs.getBoolean("EMBEDDEDFONTEXISTS"));
			media.setFileTitleFromMetadata(rs.getString("TITLE"));
//			media.setVideoTrackTitleFromMetadata(rs.getString("TITLEVIDEOTRACK"));
			media.setVideoTrackCount(rs.getInt("VIDEOTRACKCOUNT"));
			media.setImageCount(rs.getInt("IMAGECOUNT"));
			media.setVideoBitDepth(rs.getInt("BITDEPTH"));
			media.setYear(rs.getString("YEAR"));
			media.setGenre(rs.getString("GENRE"));
			media.setPlayCount(rs.getInt("PLAYCOUNT"));
			media.setPlayPosition(rs.getDouble("PLAYPOS"));
			if (rs.getTimestamp("LASTPLAYED") != null)
				media.setLastPlayed(rs.getTimestamp("LASTPLAYED").getTime());
			media.setMediaparsed(true);
			ResultSet subrs;
			try (PreparedStatement audios = conn.prepareStatement("SELECT * FROM AUDIOTRACKS WHERE FILEID = ?")) {
				audios.setInt(1, id);
				subrs = audios.executeQuery();
				while (subrs.next()) {
					DLNAMediaAudio audio = new DLNAMediaAudio();
					audio.setId(subrs.getInt("ID"));
					audio.setLang(subrs.getString("LANG"));
//					audio.setAudioTrackTitleFromMetadata(subrs.getString("TITLE"));
					audio.getAudioProperties().setNumberOfChannels(subrs.getInt("NRAUDIOCHANNELS"));
					audio.setSampleFrequency(subrs.getString("SAMPLEFREQ"));
					audio.setCodecA(subrs.getString("CODECA"));
					audio.setBitsperSample(subrs.getInt("BITSPERSAMPLE"));
					audio.setAlbum(subrs.getString("ALBUM"));
					audio.setArtist(subrs.getString("ARTIST"));
//					audio.setSongname(subrs.getString("SONGNAME"));
					audio.setTrack(subrs.getInt("TRACK"));
					audio.getAudioProperties().setAudioDelay(subrs.getInt("DELAY"));
					audio.setMuxingModeAudio(subrs.getString("MUXINGMODE"));
					audio.setBitRate(subrs.getInt("BITRATE"));
					media.getAudioTracksList().add(audio);
				}
				subrs.close();
			}
			try (PreparedStatement subs = conn.prepareStatement("SELECT * FROM SUBTRACKS WHERE FILEID = ?")) {
				subs.setInt(1, id);
				subrs = subs.executeQuery();
				while (subrs.next()) {
					DLNAMediaSubtitle sub = new DLNAMediaSubtitle();
					sub.setId(subrs.getInt("ID"));
					sub.setLang(subrs.getString("LANG"));
					sub.setSubtitlesTrackTitleFromMetadata(subrs.getString("TITLE"));
					sub.setType(SubtitleType.valueOfStableIndex(subrs.getInt("TYPE")));
					media.getSubtitleTracksList().add(sub);
				}
				subrs.close();
			}
			try (PreparedStatement subs = conn.prepareStatement("SELECT IMDBID FROM IMDB WHERE FILEID = ?")) {
                subs.setInt(1, id);
                subrs = subs.executeQuery();
                while (subrs.next()) {
                    DLNAMediaSubtitle sub = new DLNAMediaSubtitle();
                    media.setImdbId(subrs.getString("IMDBID"));
                }
                subrs.close();
            }

			list.add(media);
		}
		return list;
	}

	private Double toDouble(ResultSet rs, String column) throws SQLException {
		Object obj = rs.getObject(column);
		if (obj instanceof Double) {
			return (Double) obj;
		}
		return null;
	}

	public void insertData(String name, long modified, int type, DLNAMediaInfo media) {
		Connection conn = null;
		PreparedStatement ps = null;
		int i = 1;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(
				"INSERT INTO FILES(FILENAME, MODIFIED, TYPE, DURATION, BITRATE, WIDTH, HEIGHT, SIZE, CODECV, "+
				"FRAMERATE, ASPECT, ASPECTRATIOCONTAINER, ASPECTRATIOVIDEOTRACK, REFRAMES, AVCLEVEL, BITSPERPIXEL, "+
				"THUMB, CONTAINER, MODEL, EXPOSURE, ORIENTATION, ISO, MUXINGMODE, FRAMERATEMODE, STEREOSCOPY, "+
				"MATRIXCOEFFICIENTS, EMBEDDEDFONTEXISTS, TITLE, VIDEOTRACKCOUNT, IMAGECOUNT, BITDEPTH, YEAR, GENRE) VALUES "+
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(i++, name);
			ps.setTimestamp(i++, new Timestamp(modified));
			ps.setInt(i++, type);
			if (media != null) {
				if (media.getDuration() != null) {
					ps.setDouble(i++, media.getDuration());
				} else {
					ps.setNull(i++, Types.DOUBLE);
				}

				int databaseBitrate = 0;
				if (type != Format.IMAGE) {
					databaseBitrate = media.getBitrate();
					if (databaseBitrate == 0) {
						LOGGER.debug("Could not parse the bitrate from: " + name);
					}
				}
				ps.setInt(i++, databaseBitrate);

				ps.setInt(i++, media.getWidth());
				ps.setInt(i++, media.getHeight());
				ps.setLong(i++, media.getSize());
				ps.setString(i++, left(media.getCodecV(), SIZE_CODECV));
				ps.setString(i++, left(media.getFrameRate(), SIZE_FRAMERATE));
				ps.setString(i++, left(media.getAspectRatioDvdIso(), SIZE_ASPECTRATIO_DVDISO));
				ps.setString(i++, left(media.getAspectRatioContainer(), SIZE_ASPECTRATIO_CONTAINER));
				ps.setString(i++, left(media.getAspectRatioVideoTrack(), SIZE_ASPECTRATIO_VIDEOTRACK));
				ps.setByte(i++, media.getReferenceFrameCount());
				ps.setString(i++, left(media.getAvcLevel(), SIZE_AVC_LEVEL));
				ps.setInt(i++, media.getBitsPerPixel());
				ps.setBytes(i++, media.getThumb());
				ps.setString(i++, left(media.getContainer(), SIZE_CONTAINER));
				if (media.getExtras() != null) {
					ps.setString(i++, left(media.getExtrasAsString(), SIZE_MODEL));
				} else {
					ps.setString(i++, left(media.getModel(), SIZE_MODEL));
				}
				ps.setInt(i++, media.getExposure());
				ps.setInt(i++, media.getOrientation());
				ps.setInt(i++, media.getIso());
				ps.setString(i++, left(media.getMuxingModeAudio(), SIZE_MUXINGMODE));
				ps.setString(i++, left(media.getFrameRateMode(), SIZE_FRAMERATE_MODE));
				ps.setString(i++, left(media.getStereoscopy(), SIZE_STEREOSCOPY));
				ps.setString(i++, left(media.getMatrixCoefficients(), SIZE_MATRIX_COEFFICIENTS));
				ps.setBoolean(i++, media.isEmbeddedFontExists());
				ps.setString(i++, left(trimToEmpty(media.getFileTitleFromMetadata()), SIZE_TITLE));
//				ps.setString(i++, left(media.getVideoTrackTitleFromMetadata(), SIZE_TITLE));
				ps.setInt(i++, media.getVideoTrackCount());
				ps.setInt(i++, media.getImageCount());
				ps.setInt(i++, media.getVideoBitDepth());
                ps.setString(i++, getYearAsString(media));
                
                String genres = left(trimToEmpty(media.getGenre()), SIZE_GENRE_FILE);
                ps.setString(i++, genres);
                updateGenres(conn, genres, type);
			} else {
				ps.setString(i++, null);
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setLong(i++, 0);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setByte(i++, (byte) -1);
				ps.setString(i++, null);
				ps.setInt(i++, 0);
				ps.setBytes(i++, null);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setString(i++, null);
				ps.setBoolean(i++, false);
				ps.setString(i++, null);
//				ps.setString(i++, null);
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setString(i++, "");
				ps.setString(i++, "");
			}
			ps.executeUpdate();
			int id;
			try (ResultSet rs = ps.getGeneratedKeys()) {
				id = -1;
				while (rs.next()) {
					id = rs.getInt(1);
				}
			}
			
			if (id >= PMS.getGlobalRepo().getIndex()) {
				scanLibrary();
			}
			
			if (media != null && id > -1) {
				PreparedStatement insert = null;
				if (media.getAudioTracksList().size() > 0) {
					insert = conn.prepareStatement("INSERT INTO AUDIOTRACKS (FILEID,LANG,NRAUDIOCHANNELS,SAMPLEFREQ,CODECA,BITSPERSAMPLE,ALBUM,ARTIST,TRACK,DELAY,MUXINGMODE,BITRATE) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					for (DLNAMediaAudio audio : media.getAudioTracksList()) {
						i = 1;
						insert.clearParameters();
						insert.setInt(i++, id);
//						insert.setInt(2, audio.getId());
						insert.setString(i++, left(audio.getLang(), SIZE_LANG));
//						insert.setString(i++, left(audio.getAudioTrackTitleFromMetadata(), SIZE_TITLE));
						insert.setInt(i++, audio.getAudioProperties().getNumberOfChannels());
						insert.setString(i++, left(audio.getSampleFrequency(), SIZE_SAMPLEFREQ));
						insert.setString(i++, left(audio.getCodecA(), SIZE_CODECA));
						insert.setInt(i++, audio.getBitsperSample());
						insert.setString(i++, left(trimToEmpty(audio.getAlbum()), SIZE_ALBUM));
						
						String artists = left(trimToEmpty(audio.getArtist()), SIZE_ARTIST);
						insert.setString(i++, artists);
						updateArtists(conn, artists);
						
//						insert.setString(10, left(trimToEmpty(audio.getSongname()), SIZE_SONGNAME));
						insert.setInt(i++, audio.getTrack());
						insert.setInt(i++, audio.getAudioProperties().getAudioDelay());
						insert.setString(i++, left(trimToEmpty(audio.getMuxingModeAudio()), SIZE_MUXINGMODE));
						insert.setInt(i++, audio.getBitRate());

						try {
							insert.executeUpdate();
						} catch (SQLException e) {
							if (e.getErrorCode() == 23505) {
								LOGGER.debug("A duplicate key error occurred while trying to store the following file's audio information in the database: " + name);
							} else {
								LOGGER.debug("An error occurred while trying to store the following file's audio information in the database: " + name);
							}
							LOGGER.debug("The error given by jdbc was: " + e);
						}
					}
				}
                close(insert);

				insertSubtitles(media, conn, id);
				
                insertImdb(media, conn, id);
			}
		} catch (SQLException se) {
			if (se.getErrorCode() == 23505) {
				LOGGER.debug("Duplicate key while inserting this entry: " + name + " into the database: " + se.getMessage());
			} else {
				LOGGER.error(null, se);
			}
		} finally {
			close(ps);
			close(conn);
		}
	}

    private void insertImdb(DLNAMediaInfo media, Connection conn, int id) throws SQLException {
        PreparedStatement insert = conn.prepareStatement("MERGE INTO IMDB (FILEID, IMDBID) VALUES (?, ?)");
        insert.clearParameters();
        int i = 1;
        insert.setInt(i++, id);
        insert.setString(i++, media.getImdbId());
        insert.executeUpdate();
        close(insert);
    }

	/**
	 * Have to keep default as "" for year view in media library
	 * @param media
	 * @return
	 */
    private String getYearAsString(DLNAMediaInfo media) {
        return media.getYear() > 0 ? String.valueOf(media.getYear()) : "";
    }

    public void insertSubtitles(DLNAMediaInfo media, int fileId) {
        Connection conn = null;
        try {
            conn = getConnection();
            insertSubtitles(media, conn, fileId);
        } catch (SQLException e) {
            LOGGER.error(null, e);
        } finally {
            close(conn);
        }
    }
	
    private void insertSubtitles(DLNAMediaInfo media, Connection conn, int fileId) throws SQLException {
        if (media.getSubtitleTracksList().size() > 0) {
            PreparedStatement insert = conn.prepareStatement("INSERT INTO SUBTRACKS (FILEID,LANG,TITLE,TYPE) VALUES (?, ?, ?, ?)");
        	for (DLNAMediaSubtitle sub : media.getSubtitleTracksList()) {
        		if (sub.getExternalFile() == null) { // no save of external subtitles
        			insert.clearParameters();
        			insert.setInt(1, fileId);
//							insert.setInt(2, sub.getId());
        			insert.setString(2, left(sub.getLang(), SIZE_LANG));
        			String title = sub.getSubtitlesTrackTitleFromMetadata();
                    insert.setString(3, left(title, SIZE_TITLE));
        			insert.setInt(4, sub.getType().getStableIndex());
        			try {
        				insert.executeUpdate();
        			} catch (SQLException e) {
        				if (e.getErrorCode() == 23505) {
        					LOGGER.debug("A duplicate key error occurred while trying to store the following file's subtitle information in the database: " + title);
        				} else {
        					LOGGER.debug("An error occurred while trying to store the following file's subtitle information in the database: " + title);
        				}
        				LOGGER.debug("The error given by jdbc was: " + e);
        			}
        		}
        	}
            close(insert);
        }
    }

	private void updateArtists(Connection conn, String artists) throws SQLException {
		String[] names = artists.split("[,&/]");
		List<Param> params = new ArrayList<>();
		Timestamp modified = new Timestamp((new Date()).getTime());

		for (String n : names) {
			params.add(new Param(DataType.STRING, n.trim().toUpperCase()));
			params.add(new Param(DataType.TIME, modified));
			executeQuery(conn, "MERGE INTO ARTISTS (NAME, MODIFIED) VALUES (?, ?)", params, true);
			params.clear();
		}
	}
	
    private void updateGenres(Connection conn, String genres, int type) throws SQLException {
        String[] names = genres.split(",");
        List<Param> params = new ArrayList<>();

        for (String n : names) {
            params.add(new Param(DataType.STRING, n.trim()));
            params.add(new Param(DataType.INT, type));
            executeQuery(conn, "MERGE INTO GENRES (NAME, TYPE) VALUES (?, ?)", params, true);
            params.clear();
        }
    }

	public void updateStatistics(DLNAResource res, double playPosition) {
		String sql = "UPDATE FILES SET PLAYPOS = ?, PLAYCOUNT = ?, LASTPLAYED = ? WHERE FILENAME = ?";
		List<Param> params = new ArrayList<>();
		int count = res.getMedia().getPlayCount();
		long lastPlayed = (new Date()).getTime();

		params.add(new Param(DataType.DOUBLE, playPosition));
		params.add(new Param(DataType.INT, count));
		params.add(new Param(DataType.TIME, new Timestamp(lastPlayed)));
		params.add(new Param(DataType.STRING, res.getSystemName()));

		Connection conn = null;
		try {
			conn = getConnection();
			executeQuery(conn, sql, params, true);
		} catch (SQLException e) {
			LOGGER.error(null, e);
		} finally {
			close(conn);
		}
		res.getMedia().setPlayCount(count);
		res.getMedia().setPlayPosition(playPosition);
	}

	/**
	 * Update IMDB id, title, year, thumbnail and genre.
	 * 
	 * @param name file name
	 * @param modified
	 * @param type
	 * @param media
	 */
    public void updateMetadata(String name, long modified, int type, DLNAMediaInfo media) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "UPDATE FILES SET THUMB = ?, ";
            if (Format.VIDEO == type) {
                sql += "TITLE = ?, YEAR = ?, GENRE = ?, ";
            }
            sql += "THUMBPROCESSED = 1 WHERE ID = ? AND MODIFIED = ?";
            ps = conn.prepareStatement(sql);

            int i = 1;
            if (media != null) {
                ps.setBytes(i++, media.getThumb());
            } else {
                ps.setNull(i++, Types.BINARY);
            }
            if (Format.VIDEO == type) {
                ps.setString(i++, media.getFileTitleFromMetadata());
                ps.setString(i++, getYearAsString(media));
                ps.setString(i++, media.getGenre());
            }
            ps.setString(i++, name);
            ps.setTimestamp(i++, new Timestamp(modified));
            ps.executeUpdate();

            insertImdb(media, conn, Integer.valueOf(name));
        } catch (SQLException se) {
            if (se.getErrorCode() == 23001) {
                LOGGER.debug(
                        "Duplicate key while inserting this entry: " + name + " into the database: " + se.getMessage());
            } else {
                LOGGER.error(null, se);
            }
        } finally {
            close(ps);
            close(conn);
        }
    }

	public ArrayList<String> getStrings(String sql) {
		ArrayList<String> list = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String str = rs.getString(1);
				if (isBlank(str)) {
					if (!list.contains(NONAME)) {
						list.add(NONAME);
					}
				} else if (!list.contains(str)) {
					list.add(str);
				}
			}
		} catch (SQLException se) {
			LOGGER.error(null, se);
			return null;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return list;
	}

	/**
	 * Cleanup records which don't match name. Used to cleanup shares after they have been removed from config.
	 * @param name Share name in the config
	 */
	public void cleanup(String name) {
		List<Param> params = new ArrayList<>();
		params.add(new Param(DataType.STRING, name));
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "DELETE FROM FILES WHERE FILENAME REGEXP ?";
			executeQuery(conn, sql, params, true);
		} catch (SQLException e) {
			LOGGER.error(null, e);
		} finally {
			close(conn);
		}
	}

	/**
	 * Cleanup stale file records.
	 */
	public void cleanup() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) FROM FILES");
			rs = ps.executeQuery();
			dbCount = 0;

			if (rs.next()) {
				dbCount = rs.getInt(1);
			}

			rs.close();
			ps.close();
			PMS.get().getFrame().setStatusLine(Messages.getString("DLNAMediaDatabase.2") + " 0%");
			int i = 0;
			int oldpercent = 0;

			if (dbCount > 0) {
				ps = conn.prepareStatement("SELECT FILENAME, MODIFIED, ID FROM FILES", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
				rs = ps.executeQuery();
				while (rs.next()) {
					String filename = rs.getString("FILENAME");
					long modified = rs.getTimestamp("MODIFIED").getTime();
					File file = new File(filename);
					if (!file.exists() || file.lastModified() != modified) 
					{
						rs.deleteRow();
					}
					i++;
					int newpercent = i * 100 / dbCount;
					if (newpercent > oldpercent) {
						PMS.get().getFrame().setStatusLine(Messages.getString("DLNAMediaDatabase.2") + newpercent + "%");
						oldpercent = newpercent;
					}
				}
			}
			
			String cleanArtists = "DELETE FROM ARTISTS G WHERE NAME NOT IN (SELECT G.NAME FROM AUDIOTRACKS A WHERE A.UPPER_ARTIST LIKE CONCAT('%', G.NAME, '%'))";
			String cleanGenres = "DELETE FROM GENRES G WHERE UPPER_NAME NOT IN (SELECT G.UPPER_NAME FROM FILES F WHERE F.UPPER_GENRE LIKE CONCAT('%', G.UPPER_NAME, '%'))";
			executeUpdate(conn, cleanArtists);
			executeUpdate(conn, cleanGenres);
		} catch (SQLException se) {
			LOGGER.error(null, se);
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
	}

	public ArrayList<File> getFiles(String sql) {
		ArrayList<File> list = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean clean = true;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql.toLowerCase().startsWith("select") ? sql : ("SELECT FILENAME, MODIFIED FROM FILES WHERE " + sql));
			rs = ps.executeQuery();
			while (rs.next()) {
				String filename = rs.getString("FILENAME");
				long modified = rs.getTimestamp("MODIFIED").getTime();
				File file = new File(filename);
				if (file.lastModified() == 0 || (file.exists() && file.lastModified() == modified)) {
					list.add(file);
				} else {
					clean = false;
				}
			}

			if (!clean)
				cleanup();
		} catch (SQLException se) {
			LOGGER.error(null, se);
			return null;
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
		return list;
	}

	private void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			LOGGER.error("error during closing:" + e.getMessage(), e);
		}
	}

	private void close(Statement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			LOGGER.error("error during closing:" + e.getMessage(), e);
		}
	}

	private void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.error("error during closing:" + e.getMessage(), e);
		}
	}

	public boolean isScanLibraryRunning() {
		return scanner != null && scanner.isAlive();
	}

	public void scanLibrary() {
		if (isScanLibraryRunning()) {
			LOGGER.info(Messages.getString("NetworkTab.70"));
		} else {
			scanner = new Thread(this, "Library Scanner");
			scanner.start();
		}
	}

	public void stopScanLibrary() {
		if (isScanLibraryRunning()) {
			PMS.get().getRootFolder(null).stopScan();
		}
	}

	@Override
	public void run() {
		try {
			PMS.get().refreshLibrary(true);
//			PMS.get().getRootFolder(null).scan();
		} catch (Exception e) {
			LOGGER.error("Unhandled exception during library scan: {}", e.getMessage());
			LOGGER.trace("", e);
		}
	}
	
	public void deleteFile(String filename) {
		String sql = "DELETE FROM FILES WHERE FILENAME = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, filename);
			ps.executeUpdate();
		} catch (SQLException se) {
			LOGGER.error(null, se);
		} finally {
			close(ps);
			close(conn);
		}
	
	}

	public void shutdown() {
	    cp.dispose();
	    tcpServer.stop();
	    webServer.stop();
	}
}
