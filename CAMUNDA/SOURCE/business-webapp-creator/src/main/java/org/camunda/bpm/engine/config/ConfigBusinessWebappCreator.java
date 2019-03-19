/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.config;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author TramSunny
 */
public class ConfigBusinessWebappCreator {

    public static String pathServerCamunda = System.getenv("CATALINA_HOME");
    public static String pathConfig = getPathConfigInstance();
    public static String pathConfigLocalTest = "C:\\Users\\taonu\\Desktop\\business-webapp-creator\\src\\main\\webapp\\WEB-INF\\configWeb.xml";
    private static HashMap<String, Object> configWebapp = new HashMap<>();
    private static HashMap<String, Object> configWordpress = new HashMap<>();
    private static boolean isInit = false;
    public static String uriService = "http://localhost:8080/business-webapp-creator/";

    public static String getPathConfig() {
        return pathConfig;
    }

    private static String getPathConfigInstance() {
        String ks = pathServerCamunda;
        if (!(ks.endsWith("/") || ks.endsWith("\\"))) {
            ks = ks + "/";
        }
        ks = ks + "webapps/";
        return ks;
    }

    public static String getConfigFile() {
        return pathConfigLocalTest;
//        return pathConfig+"business-webapp-creator/WEB-INF/configWeb.xml";
    }

    private static void init() {

        try {
            File inputFile = new File(getConfigFile());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            Element elementSetting = (Element) doc.getElementsByTagName("setting").item(0);
            Element webapp = (Element) elementSetting.getElementsByTagName("webapp").item(0);
            Element wordpress = (Element) elementSetting.getElementsByTagName("wordpress").item(0);
            uriService = elementSetting.getElementsByTagName("uriService").item(0).getTextContent();
            parseWebapp(webapp);
            parseWordpress(wordpress);
            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseWebapp(Element webapp) {
        Element database = (Element) webapp.getElementsByTagName("database").item(0);
        Element engine = (Element) webapp.getElementsByTagName("engine").item(0);
        configWebapp.put("database", new ConfigDatabase(database));
        configWebapp.put("engine", new ConfigEngine(engine));
    }

    private static void parseWordpress(Element wordpress) {
        Element database = (Element) wordpress.getElementsByTagName("database").item(0);
        Element install = (Element) wordpress.getElementsByTagName("install").item(0);
        configWordpress.put("database", new ConfigDatabase(database));
        configWordpress.put("install", new ConfigInstallWordpress(install));
    }

    public static Object getConfigWebapp(String key) {
        if (!isInit) {
            init();
        }
        return configWebapp.get(key);
    }

    public static Object getConfigWordpress(String key) {
        if (!isInit) {
            init();
        }
        return configWordpress.get(key);
    }

}
