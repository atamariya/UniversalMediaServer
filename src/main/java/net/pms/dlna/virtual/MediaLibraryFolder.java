package net.pms.dlna.virtual;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pms.PMS;
import net.pms.dlna.DLNAMediaDatabase;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.DVDISOFile;
import net.pms.dlna.PlaylistFolder;
import net.pms.dlna.RealFile;

public class MediaLibraryFolder extends VirtualFolder {
	public static final int FILES = 0;
	public static final int TEXTS = 1;
	public static final int PLAYLISTS = 2;
	public static final int ISOS = 3;
	public static final Pattern PATTERN_SQL = Pattern.compile("SELECT (.*?) FROM (.*)", Pattern.CASE_INSENSITIVE);

	private String sqls[];
	private int expectedOutputs[];
	private transient DLNAMediaDatabase database;
	private int childLength = -1;
	private int maxChild = -1;
	private int start, count = -1;

	public MediaLibraryFolder(String name, String sql, int expectedOutput) {
		this(name, new String[]{sql}, new int[]{expectedOutput});
	}

	public MediaLibraryFolder(String name, String sql[], int expectedOutput[]) {
		super(name, null);
		this.sqls = sql;
		this.expectedOutputs = expectedOutput;
		this.database = PMS.get().getDatabase();
	}

	@Override
	public void doRefreshChildren() {
		if (!isDiscovered() && sqls.length > 0) {
			String sql = sqls[0];
			int expectedOutput = expectedOutputs[0];
			if (sql != null) {
				getChildren().clear();
				sql = transformSQL(sql);
				setDiscovered(true);

				if (getMaxChild() == -1) {
					String count = sql;
					int end = count.indexOf("ORDER BY");
					count = count.substring(0, end);

					Matcher matcher = PATTERN_SQL.matcher(count);
					matcher.find();
					if (count.indexOf("*") != -1)
						count = matcher.replaceFirst("SELECT COUNT($1) FROM $2");
					else
						count = String.format("SELECT COUNT(*) FROM (%s)", count);

					sql = String.format("%s offset %d limit %d", sql, getStart(), getCount());

					List<String> children = database.getStrings(count);
					if (children != null) {
						childLength = Integer.parseInt(children.get(0));
					}
				} else {
					int count = getStart()  + getCount();
					if (count > getMaxChild()) {
						if (getStart() > 0)
							count = getMaxChild() - getCount();
						else
							count = getMaxChild();
					} else
						count = getCount();
					sql = String.format("%s offset %d limit %d", sql, getStart(), count);
					childLength = getMaxChild();
				}

				if (expectedOutput == FILES) {
					List<File> list = database.getFiles(sql);
					if (list != null) {
//						UMSUtils.sort(list, PMS.getConfiguration().mediaLibrarySort());
						for (File f : list) {
							addChild(new RealFile(f));
						}
					}
//					discoverWithRenderer(this, sql, getStart(), getCount(), null, null);
					
					// Don't set discovered flag as we want the most updated view 
					setDiscovered(false);
				} else if (expectedOutput == PLAYLISTS) {
					List<File> list = database.getFiles(sql);
					if (list != null) {
//						UMSUtils.sort(list, PMS.getConfiguration().mediaLibrarySort());
						for (File f : list) {
							addChild(new PlaylistFolder(f));
						}
					}
				} else if (expectedOutput == ISOS) {
					List<File> list = database.getFiles(sql);
					if (list != null) {
//						UMSUtils.sort(list, PMS.getConfiguration().mediaLibrarySort());
						for (File f : list) {
							addChild(new DVDISOFile(f));
						}
					}
				} else if (expectedOutput == TEXTS) {
					List<String> list = database.getStrings(sql);
					if (list != null) {
						for (String s : list) {
							String sqls2[] = new String[sqls.length - 1];
							int expectedOutputs2[] = new int[expectedOutputs.length - 1];
							System.arraycopy(sqls, 1, sqls2, 0, sqls2.length);
							System.arraycopy(expectedOutputs, 1, expectedOutputs2, 0, expectedOutputs2.length);
							
							MediaLibraryFolder child = new MediaLibraryFolder(s, sqls2, expectedOutputs2);
							child.setStart(getStart());
							addChild(child);
						}
					}
				}
			}
		}
	}

	private String transformSQL(String sql) {

		sql = sql.replace("${0}", transformName(getName()));
		if (getParent() != null) {
			sql = sql.replace("${1}", transformName(getParent().getName()));
			if (getParent().getParent() != null) {
				sql = sql.replace("${2}", transformName(getParent().getParent().getName()));
				if (getParent().getParent().getParent() != null) {
					sql = sql.replace("${3}", transformName(getParent().getParent().getParent().getName()));
					if (getParent().getParent().getParent().getParent() != null) {
						sql = sql.replace("${4}", transformName(getParent().getParent().getParent().getParent().getName()));
					}
				}
			}
		}
		return sql;
	}

	private String transformName(String name) {
		if (name.equals(DLNAMediaDatabase.NONAME)) {
			name = "";
		}
		name = name.replace("'", "''"); // issue 448
		return name;
	}

	@Override
	public boolean isRefreshNeeded() {
		return true;
	}

//	@Override
	public void doRefreshChildren1() {
		ArrayList<File> list = null;
		ArrayList<String> strings = null;
		int expectedOutput = 0;
		if (sqls.length > 0) {
			String sql = sqls[0];
			expectedOutput = expectedOutputs[0];
			if (sql != null) {
				sql = transformSQL(sql);
				if (expectedOutput == FILES || expectedOutput == PLAYLISTS || expectedOutput == ISOS) {
					list = database.getFiles(sql);
				} else if (expectedOutput == TEXTS) {
					strings = database.getStrings(sql);
				}
			}
		}
		ArrayList<File> addedFiles = new ArrayList<>();
		ArrayList<String> addedString = new ArrayList<>();
		ArrayList<DLNAResource> removedFiles = new ArrayList<>();
		ArrayList<DLNAResource> removedString = new ArrayList<>();
		int i = 0;
		if (list != null) {
			for (File file : list) {
				boolean present = false;

				for (DLNAResource dlna : getChildren()) {
					if (i == 0 && (!(dlna instanceof VirtualFolder) || (dlna instanceof MediaLibraryFolder))) {
						removedFiles.add(dlna);
					}

					String name = dlna.getName();
					long lm = dlna.getLastModified();
					boolean videoTSHack = false;

					if (dlna instanceof DVDISOFile) {
						DVDISOFile dvdISOFile = (DVDISOFile) dlna;
						// XXX DVDISOFile has inconsistent ideas of what constitutes a VIDEO_TS folder
						videoTSHack = dvdISOFile.getFilename().equals(file.getName());
					}

					if ((file.getName().equals(name) || videoTSHack) && file.lastModified() == lm) {
						removedFiles.remove(dlna);
						present = true;
					}
				}
				i++;
				if (!present) {
					addedFiles.add(file);
				}
			}
		}
		i = 0;
		if (strings != null) {
			for (String f : strings) {
				boolean present = false;
				for (DLNAResource d : getChildren()) {
					if (i == 0 && (!(d instanceof VirtualFolder) || (d instanceof MediaLibraryFolder))) {
						removedString.add(d);
					}
					String name = d.getName();
					if (f.equals(name)) {
						removedString.remove(d);
						present = true;
					}
				}
				i++;
				if (!present) {
					addedString.add(f);
				}
			}
		}

		for (DLNAResource f : removedFiles) {
			getChildren().remove(f);
		}
		for (DLNAResource s : removedString) {
			getChildren().remove(s);
		}
		for (File f : addedFiles) {
			if (expectedOutput == FILES) {
				addChild(new RealFile(f));
			} else if (expectedOutput == PLAYLISTS) {
				addChild(new PlaylistFolder(f));
			} else if (expectedOutput == ISOS) {
				addChild(new DVDISOFile(f));
			}
		}
		for (String f : addedString) {
			if (expectedOutput == TEXTS) {
				String sqls2[] = new String[sqls.length - 1];
				int expectedOutputs2[] = new int[expectedOutputs.length - 1];
				System.arraycopy(sqls, 1, sqls2, 0, sqls2.length);
				System.arraycopy(expectedOutputs, 1, expectedOutputs2, 0, expectedOutputs2.length);
				addChild(new MediaLibraryFolder(f, sqls2, expectedOutputs2));
			}
		}

		setUpdateId(this.getIntId());
		//return removedFiles.size() != 0 || addedFiles.size() != 0 || removedString.size() != 0 || addedString.size() != 0;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof MediaLibraryFolder && getName() != null) {
			MediaLibraryFolder k = (MediaLibraryFolder) o;
			result = getName().equals(k.getName()) && this.sqls[0].equals(k.sqls[0]) 
					&& getStart() == k.getStart();
		}
		return result;
	}
	
	@Override
	public int childrenNumber() {
		if (childLength == -1)
			return super.childrenNumber();
		else
			return childLength;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		if (this.start != start) {
			this.start = start;
			setDiscovered(false);
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getExpectedOutput() {
		return expectedOutputs[0];
	}

	public int getMaxChild() {
		return maxChild;
	}

	public void setMaxChild(int maxChild) {
		this.maxChild = maxChild;
	}
}
