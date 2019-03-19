/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.dto.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author TramSunny
 */
public class WordpressDto {

    public String id;
    public String name;
    public String wphost;
    public String directory;
    public String dbhost;
    public String dbname;
    public String uname;
    public String pwd;
    public String prefix;

    public WordpressDto(ResultSet rs) throws SQLException {
        this.id = rs.getString("id");
        this.name = rs.getString("name");
        this.wphost = rs.getString("wphost");
        this.directory = rs.getString("directory");
        this.dbhost = rs.getString("dbhost");
        this.dbname = rs.getString("dbname");
        this.uname = rs.getString("uname");
        this.pwd = rs.getString("pwd");
        this.prefix = rs.getString("prefix");

    }

    public WordpressDto() {
    }

    public String getTable(String name) {
        return " `" + this.prefix + name + "` ";
    }

    public String getFullUrl() {
        return wphost + "/" + directory;
    }
}
