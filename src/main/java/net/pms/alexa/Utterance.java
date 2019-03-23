package net.pms.alexa;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utterance {
	private static final Logger LOGGER = LoggerFactory.getLogger(Utterance.class);
	
	public static final String DEFAULT = "default";
	public static final String START = "welcome";
	public static final String END = "end";
	
	private static boolean initialised;
	private static ResourceBundle bundle;

	private static void init() {
		if (!initialised) {
			try {
				bundle = ResourceBundle.getBundle("resources.AlexaSpeech");
				initialised = true;
			} catch (Exception e) {
				LOGGER.error("Failed to load properties: {}", e.getMessage());
			}
		}
	}
	
	public static String get(String key) {
		init();
		String val = "Response not configured";
		if (bundle != null) {
			String[] values = getStringArray(key);
			int i = (int) Math.round((values.length * Math.random()));
			if (values.length > 0) {
				i = Math.min(i, values.length - 1);
				val = values[i];
			}
		}
		return val;
	}
	
	private static String[] getStringArray(String keyPrefix) {
	      String[] result;
	      Enumeration<String> keys = bundle.getKeys();
	      List<String> temp = new ArrayList<String>();

	      // get the keys and add them in a temporary ArrayList
	      for (Enumeration<String> e = keys; keys.hasMoreElements();) {
	         String key = e.nextElement();
	         
	         if (key.startsWith(keyPrefix)) {
	            temp.add(key);
	         }
	      }

	      // create a string array based on the size of temporary ArrayList
	      result = new String[temp.size()];

	      // store the bundle Strings in the StringArray
	      for (int i = 0; i < temp.size(); i++) {
	         result[i] = bundle.getString(temp.get(i));
	      }

	      return result;
	   }

}
