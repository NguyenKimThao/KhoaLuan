package org.ext.constantutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class ConstantParseUtil {
	private static String resourceName = "constantParse.properties";
	private static Logger LOGGER = Logger.getLogger(ConstantParseUtil.class.getName());
	private static Properties prop;
	private static List<String> propertiesList;
	private static ConstantParseUtil instance = null;
	
	private ConstantParseUtil() throws IOException {
		// TODO Auto-generated constructor stub
		LOGGER.info("Init constants");
		prop = new Properties();
		try {
			prop.load(ConstantParseUtil.class.getClassLoader().getResourceAsStream(resourceName));
			propertiesList = new ArrayList<>();
			for (String key : prop.stringPropertyNames()) {
				String value = prop.getProperty(key);
				propertiesList.add(ConstantUtil.getConstantsMap().get(value));
			}
		} catch (IOException e) {
			LOGGER.severe("Failed to load constants file");
			throw e;
		}
	}
	
	public static void init() throws IOException {
		if (instance == null) {
			instance = new ConstantParseUtil();
		}
	}

	public static List<String> getConstantsMap() {
		return propertiesList;
	}

}
