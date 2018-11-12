package net.pms.dlna.virtual;

import net.pms.Messages;

public class MediaLibrary extends VirtualFolder {
	private MediaLibraryFolder allFolder;

	public MediaLibraryFolder getAllFolder() {
		return allFolder;
	}
	private MediaLibraryFolder albumFolder;
	private MediaLibraryFolder artistFolder;
	private MediaLibraryFolder genreFolder;
	private MediaLibraryFolder playlistFolder;

	public MediaLibraryFolder getAlbumFolder() {
		return albumFolder;
	}

	public MediaLibrary() {
		super(Messages.getString("PMS.2"), null);
		init();
	}

	private void init() {
		VirtualFolder vfAudio = new VirtualFolder(Messages.getString("PMS.1"), null);
		allFolder = new MediaLibraryFolder(Messages.getString("PMS.11"), "SELECT F.* FROM FILES F WHERE F.TYPE = 1 AND F.GENRE NOT IN ('Speech', 'Genre_101') ORDER BY UPPER_TITLE ASC", MediaLibraryFolder.FILES);
		vfAudio.addChild(allFolder);
		allFolder = new MediaLibraryFolder(Messages.getString("PMS.43"), "SELECT F.* FROM FILES F WHERE F.TYPE = 1 AND F.GENRE IN ('Speech', 'Genre_101') ORDER BY UPPER_TITLE ASC", MediaLibraryFolder.FILES);
		vfAudio.addChild(allFolder);
		playlistFolder = new MediaLibraryFolder(Messages.getString("PMS.9"), "SELECT * FROM FILES F WHERE F.TYPE = 16 ORDER BY F.FILENAME ASC", MediaLibraryFolder.PLAYLISTS);
		vfAudio.addChild(playlistFolder);
		artistFolder = new MediaLibraryFolder(Messages.getString("PMS.13"), new String[]{"SELECT NAME FROM ARTISTS ORDER BY NAME ASC", 
			"SELECT * FROM FILES F, AUDIOTRACKS A where F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ARTIST LIKE '%${0}%' ORDER BY UPPER_TITLE"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(artistFolder);
		albumFolder = new MediaLibraryFolder(Messages.getString("PMS.16"), new String[]{"SELECT DISTINCT A.UPPER_ALBUM FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 ORDER BY A.UPPER_ALBUM ASC", 
			"SELECT * FROM FILES F, AUDIOTRACKS A where F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ALBUM = '${0}' ORDER BY UPPER_TITLE"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(albumFolder);
		genreFolder = new MediaLibraryFolder(Messages.getString("PMS.19"), new String[]{"SELECT DISTINCT F.UPPER_NAME FROM GENRES F WHERE F.TYPE = 1 ORDER BY F.UPPER_NAME ASC", 
			"SELECT * FROM FILES F WHERE F.TYPE = 1 AND F.UPPER_GENRE LIKE '%${0}%' ORDER BY UPPER_TITLE"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(genreFolder);
		MediaLibraryFolder mlf6 = new MediaLibraryFolder(Messages.getString("PMS.22"), new String[]{
				"SELECT DISTINCT A.UPPER_ARTIST FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 ORDER BY A.UPPER_ARTIST ASC",
				"SELECT DISTINCT A.UPPER_ALBUM FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ARTIST = '${0}' ORDER BY A.UPPER_ALBUM ASC",
				"SELECT * FROM FILES F, AUDIOTRACKS A where F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ARTIST = '${1}' AND A.UPPER_ALBUM = '${0}' ORDER BY A.TRACK ASC, UPPER_TITLE ASC"},
				new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(mlf6);
		MediaLibraryFolder mlf7 = new MediaLibraryFolder(Messages.getString("PMS.26"), new String[]{
				"SELECT DISTINCT F.UPPER_NAME FROM GENRES F WHERE F.TYPE = 1 ORDER BY F.UPPER_NAME ASC",
				"SELECT DISTINCT A.UPPER_ARTIST FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 AND F.UPPER_GENRE = '${0}' ORDER BY A.UPPER_ARTIST ASC",
				"SELECT DISTINCT A.UPPER_ALBUM FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 AND F.UPPER_GENRE = '${1}' AND A.UPPER_ARTIST = '${0}' ORDER BY A.UPPER_ALBUM ASC",
				"SELECT * FROM FILES F, AUDIOTRACKS A where F.ID = A.FILEID AND F.TYPE = 1 AND F.UPPER_GENRE = '${2}' AND A.UPPER_ARTIST = '${1}' AND A.UPPER_ALBUM = '${0}' ORDER BY A.TRACK ASC, F.FILENAME ASC"}, 
				new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.TEXTS, MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(mlf7);
		MediaLibraryFolder mlfAudioDate = new MediaLibraryFolder(Messages.getString("PMS.12"), new String[]{
			"SELECT YEAR Y FROM FILES WHERE TYPE = 1 GROUP BY Y ORDER BY Y", 
			"SELECT * FROM FILES WHERE TYPE = 1 AND YEAR = '${0}' ORDER BY FILENAME ASC"},
			new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(mlfAudioDate);

		MediaLibraryFolder mlf8 = new MediaLibraryFolder(Messages.getString("PMS.28"), new String[]{
				"SELECT ID FROM REGEXP_RULES ORDER BY ORDR ASC",
				"SELECT DISTINCT A.UPPER_ARTIST FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ARTIST REGEXP (SELECT RULE FROM REGEXP_RULES WHERE ID = '${0}') ORDER BY A.UPPER_ARTIST ASC",
				"SELECT DISTINCT A.UPPER_ALBUM FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ARTIST = '${0}' ORDER BY A.UPPER_ALBUM ASC",
				"SELECT * FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 AND A.UPPER_ARTIST = '${1}' AND A.UPPER_ALBUM = '${0}' ORDER BY UPPER_TITLE"},
				new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.TEXTS, MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfAudio.addChild(mlf8);
		addChild(vfAudio);

		VirtualFolder vfImage = new VirtualFolder(Messages.getString("PMS.31"), null);
		MediaLibraryFolder mlfPhoto01 = new MediaLibraryFolder(Messages.getString("PMS.32"), "SELECT * FROM FILES WHERE TYPE = 2 ORDER BY FILENAME ASC", MediaLibraryFolder.FILES);
		vfImage.addChild(mlfPhoto01);
		MediaLibraryFolder mlfPhoto02 = new MediaLibraryFolder(Messages.getString("PMS.12"), new String[]{
			"SELECT FORMATDATETIME(MODIFIED, 'MMM yyyy') AS MONTH, YEAR(MODIFIED) AS Y, MONTH(MODIFIED) AS M FROM FILES WHERE TYPE = 2 GROUP BY MONTH, Y, M ORDER BY Y, M", 
			"SELECT * FROM FILES WHERE TYPE = 2 AND FORMATDATETIME(MODIFIED, 'MMM yyyy') = '${0}' ORDER BY FILENAME ASC"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfImage.addChild(mlfPhoto02);
		MediaLibraryFolder mlfPhoto03 = new MediaLibraryFolder(Messages.getString("PMS.21"), new String[]{"SELECT MODEL FROM FILES WHERE TYPE = 2 AND MODEL IS NOT NULL ORDER BY MODEL ASC", 
			"SELECT * FROM FILES WHERE TYPE = 2 AND MODEL = '${0}' ORDER BY FILENAME ASC"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfImage.addChild(mlfPhoto03);
		MediaLibraryFolder mlfPhoto04 = new MediaLibraryFolder(Messages.getString("PMS.25"), new String[]{"SELECT ISO FROM FILES WHERE TYPE = 2 AND ISO > 0 ORDER BY ISO ASC", 
			"SELECT * FROM FILES WHERE TYPE = 2 AND ISO = '${0}' ORDER BY FILENAME ASC"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfImage.addChild(mlfPhoto04);
		addChild(vfImage);

		VirtualFolder vfVideo = new VirtualFolder(Messages.getString("PMS.34"), null);
		MediaLibraryFolder mlfVideo01 = new MediaLibraryFolder(Messages.getString("PMS.35"), "SELECT * FROM FILES WHERE TYPE = 4 ORDER BY FILENAME ASC", MediaLibraryFolder.FILES);
		vfVideo.addChild(mlfVideo01);
		MediaLibraryFolder mlfVideo02 = new MediaLibraryFolder(Messages.getString("PMS.12"), new String[]{
			"SELECT YEAR Y FROM FILES WHERE TYPE = 4 GROUP BY Y ORDER BY Y", 
			"SELECT * FROM FILES WHERE TYPE = 4 AND YEAR = '${0}' ORDER BY FILENAME ASC"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
		vfVideo.addChild(mlfVideo02);
	    genreFolder = new MediaLibraryFolder(Messages.getString("PMS.19"), new String[]{"SELECT DISTINCT F.UPPER_NAME FROM GENRES F WHERE F.TYPE = 4 ORDER BY F.UPPER_NAME ASC", 
            "SELECT * FROM FILES F WHERE F.TYPE = 4 AND F.UPPER_GENRE LIKE '%${0}%' ORDER BY UPPER_TITLE"}, new int[]{MediaLibraryFolder.TEXTS, MediaLibraryFolder.FILES});
	    vfVideo.addChild(genreFolder);
		MediaLibraryFolder mlfVideo03 = new MediaLibraryFolder(Messages.getString("PMS.36"), "SELECT * FROM FILES WHERE TYPE = 4 AND (WIDTH >= 1200 OR HEIGHT >= 700) ORDER BY FILENAME ASC", MediaLibraryFolder.FILES);
		vfVideo.addChild(mlfVideo03);
		MediaLibraryFolder mlfVideo04 = new MediaLibraryFolder(Messages.getString("PMS.39"), "SELECT * FROM FILES WHERE TYPE = 4 AND (WIDTH < 1200 AND HEIGHT < 700) ORDER BY FILENAME ASC", MediaLibraryFolder.FILES);
		vfVideo.addChild(mlfVideo04);
		MediaLibraryFolder mlfVideo05 = new MediaLibraryFolder(Messages.getString("PMS.40"), "SELECT * FROM FILES WHERE TYPE = 32 ORDER BY FILENAME ASC", MediaLibraryFolder.ISOS);
		vfVideo.addChild(mlfVideo05);
		addChild(vfVideo);
	}

	public MediaLibraryFolder getArtistFolder() {
		return artistFolder;
	}

	public MediaLibraryFolder getGenreFolder() {
		return genreFolder;
	}

	public MediaLibraryFolder getPlaylistFolder() {
		return playlistFolder;
	}
}
