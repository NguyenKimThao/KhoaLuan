/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.support;

import java.net.URI;
import org.w3c.dom.Element;

/**
 *
 * @author TramSunny
 */
public class UriCamunda {

    private String host;
    private int port;
    private String scheme;
    private String path;
    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static UriCamunda Create(String uri) {
        return new UriCamunda(uri);
    }

    public UriCamunda(String uri) {
        if (!uri.endsWith("/")) {
            uri = uri + "/";
        }
        if (!uri.startsWith("http")) {
            uri = "http://" + uri;
        }
        URI uriFull = URI.create(uri);
        this.scheme = uriFull.getScheme();
        this.host = uriFull.getHost();
        this.port = uriFull.getPort();
        this.path = uriFull.getPath();
    }

    public UriCamunda(String host, int port, String scheme, String path, String author) {
        this.host = host;
        this.port = port;
        this.scheme = scheme;
        this.path = path;
        this.author = author;
    }

    public UriCamunda clone() {
        return new UriCamunda(host, port, scheme, path, author);
    }

    public String getUrl() {
        if (port == -1) {
            return scheme + "://" + host + path;
        }
        return scheme + "://" + host + ":" + port + path;
    }
}
