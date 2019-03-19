package org.ext.constantutil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public final class ConstantUtil {
	private static String resourceName = "constants.properties";
	private static Logger LOGGER = Logger.getLogger(ConstantUtil.class.getName());
	private static Properties prop;
	private static HashMap<String, String> propertiesMap;
	private static ConstantUtil instance = null;
	
	private ConstantUtil() throws IOException {
		// TODO Auto-generated constructor stub
		LOGGER.info("Init constants");
		prop = new Properties();
		try {
			prop.load(ConstantUtil.class.getClassLoader().getResourceAsStream(resourceName));
			propertiesMap = new HashMap<>();
			for (String key : prop.stringPropertyNames()) {
				String value = prop.getProperty(key);
				propertiesMap.put(key, value);
			}
		} catch (IOException e) {
			LOGGER.severe("Failed to load constants file");
			throw e;
		}
	}
	
	public static void init() throws IOException {
		if (instance == null) {
			instance = new ConstantUtil();
		}
	}

	public static HashMap<String, String> getConstantsMap() {
		return propertiesMap;
	}

}

