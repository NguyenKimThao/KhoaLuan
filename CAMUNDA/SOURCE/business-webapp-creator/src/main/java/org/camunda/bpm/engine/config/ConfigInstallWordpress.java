/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.config;

import org.camunda.bpm.engine.rest.support.UriCamunda;
import org.w3c.dom.Element;

/**
 *
 * @author TramSunny
 */
public class ConfigInstallWordpress {

    private UriCamunda uriCamunda;
    private String wpinstall;

    public ConfigInstallWordpress(Element uriInstall) {
        String uri = uriInstall.getElementsByTagName("uri").item(0).getTextContent();
        wpinstall = uriInstall.getElementsByTagName("wpinstall").item(0).getTextContent();

        uriCamunda = UriCamunda.Create(uri);
    }

    public ConfigInstallWordpress(UriCamunda uriCamunda) {
        this.uriCamunda = uriCamunda;
    }

    public UriCamunda getUriCamunda() {
        return uriCamunda;
    }

    public void setUriCamunda(UriCamunda uriCamunda) {
        this.uriCamunda = uriCamunda;
    }

    public String getUrl() {
        return this.uriCamunda.getUrl();
    }

    public String getInstall() {
        return wpinstall;
    }
}
