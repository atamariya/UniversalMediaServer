package net.pms.util;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.pms.PMS;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class FileDb {
	private Map<String, Object> db;
	private int minCnt;
	private String sep;
	private File file;
	private DbHandler handler;
	private boolean autoSync;
	private boolean overwrite;

	public FileDb(DbHandler h) {
		this(PMS.getConfiguration().getDataFile(h.name()), h);
	}

	public FileDb(String f, DbHandler h) {
		if (StringUtils.isEmpty(f)) {
			f = "UMS.db";
		}
		file = new File(f);
		handler = h;
		minCnt = 2;
		sep = ",";
		autoSync = true;
		overwrite = false;
		db = new HashMap<>();
	}

	public void setSep(String s) {
		sep = s;
	}

	public void setMinCnt(int c) {
		minCnt = c;
	}

	public void setAutoSync(boolean b) {
		autoSync = b;
	}

	public void setOverwrite(boolean b) {
		overwrite = b;
	}

	public Set<String> keys() {
		return db.keySet();
	}

	public void init() {
		if (!file.exists()) {
			return;
		}
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine.length < minCnt || nextLine[0].startsWith("#")) {
					continue;
				}
				db.put(nextLine[0], handler.create(nextLine));
			}
		} catch (Exception e) {
		}
	}

	public void addNoSync(String key, Object obj) {
		if (!overwrite) {
			if (get(key) != null) {
				return;
			}
		}
		db.put(key, obj);
	}

	public void removeNoSync(String key) {
		db.remove(key);
	}

	public void add(String key, Object obj) {
		addNoSync(key, obj);
		if (autoSync) {
			sync();
		}
	}

	public void remove(String key) {
		db.remove(key);
		if (autoSync) {
			sync();
		}
	}

	public Object get(String key) {
		return db.get(key);
	}

	public void sync() {
		try (FileOutputStream out = new FileOutputStream(file)) {
			// Write a dummy line to make sure the file exists
			Date now = new Date();
			String data = "#########################\n#### Db file generated " + now.toString() + "\n" +
					"#### Edit with care\n#########################\n";
			out.write(data.getBytes(), 0, data.length());
			for (String key : db.keySet()) {
				Object obj = db.get(key);
				if (obj == null) {
					data = key;
					for (int i = 1; i < minCnt; i++) {
						data += sep;
					}
					data += "\n";
				} else {
					String[] data1 = handler.format(obj);

					// Make sure values containing commas are wrapped with quotation marks
					data1[1] = StringEscapeUtils.escapeCsv(data1[1]);
					data1[2] = StringEscapeUtils.escapeCsv(data1[2]);

					data = key + sep + StringUtils.join(data1, sep) + "\n";
				}
				out.write(data.getBytes(), 0, data.length());
			}
			out.flush();
		} catch (IOException e) {
		}
	}

	public static String safeGetArg(String[] args, int i) {
		return (i >= args.length ? "" : args[i]);
	}
}
