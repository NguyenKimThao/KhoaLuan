/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 *
 * @author TramSunny
 */
public class ConfigDatabase {

    public String driver;
    public String urlHost;
    public String host;
    public String port;
    public String databaseName;
    public String username;
    public String password;
    public String useSSL;

    public ConfigDatabase() {
    }

    public ConfigDatabase(Element databaseElement) {
        String dr = databaseElement.getElementsByTagName("driver").item(0).getTextContent();
        this.databaseName = databaseElement.getElementsByTagName("databaseName").item(0).getTextContent();
        this.username = databaseElement.getElementsByTagName("username").item(0).getTextContent();
        this.password = databaseElement.getElementsByTagName("password").item(0).getTextContent();
        if (databaseElement.getElementsByTagName("useSSL") != null && databaseElement.getElementsByTagName("useSSL").getLength() != 0) {
            this.useSSL = databaseElement.getElementsByTagName("useSSL").item(0).getTextContent();
        }
        if (dr.equals("mysql")) {
            this.driver = "org.gjt.mm.mysql.Driver";
            urlHost = "jdbc:mysql";
        } else if (dr.equals("psql") || dr.equals("postgresql")) {
            this.driver = "org.postgresql.Driver";
            urlHost = "jdbc:postgresql";
        } else {
            this.driver = dr;
            if (databaseElement.getElementsByTagName("urlHost") != null && databaseElement.getElementsByTagName("urlHost").getLength() != 0) {
                urlHost = databaseElement.getElementsByTagName("urlHost").item(0).getTextContent();
            }
        }

        String p = databaseElement.getElementsByTagName("port").item(0).getTextContent();
        String h = databaseElement.getElementsByTagName("host").item(0).getTextContent();
        if (p == "") {
            int index = h.lastIndexOf(":");
            if (index > 0) {
                try {
                    int portParse = Integer.parseInt(h.substring(index + 1));
                    this.port = String.valueOf(portParse);
                    this.host = h.substring(0, index);
                } catch (Exception e) {
                    this.host = h;
                    this.port = "";
                }
            } else {
                this.host = h;
                if (this.driver.equals("org.postgresql.Driver")) {
                    this.port = "5432";
                } else {
                    this.port = "3306";
                }
            }
        } else {
            this.host = h;
            this.port = p;
        }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName(this.driver);
        String url = urlHost + "://" + host;
        if (!port.equals("")) {
            url += ":" + port;
        }
        url += "/" + databaseName + "?useSSL=" + useSSL;
        System.out.println(url + " " + username + " " + password);
        return DriverManager.getConnection(url, username, password);
    }

    public Connection getConnectionNodatabase() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName(this.driver);
        String url = urlHost + "://" + host;
        if (!port.equals("")) {
            url += ":" + port;
        }
        url += "/" + "?useSSL=" + useSSL;
        System.out.println(url + " " + username + " " + password);
        return DriverManager.getConnection(url, username, password);
    }

    public String getHostPort() {
        return host + ":" + port;
    }
}
