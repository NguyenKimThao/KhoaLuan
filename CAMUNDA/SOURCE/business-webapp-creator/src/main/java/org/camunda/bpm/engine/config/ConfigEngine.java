/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.config;

import java.net.URI;
import org.camunda.bpm.engine.rest.support.UriCamunda;
import org.w3c.dom.Element;

/**
 *
 * @author TramSunny
 */
public class ConfigEngine {

    private UriCamunda uriCamunda;

    public ConfigEngine(Element uriEngine) {
        String uri = uriEngine.getElementsByTagName("uri").item(0).getTextContent();
        uriCamunda = UriCamunda.Create(uri);
    }

    public UriCamunda getUriCamunda() {
        return uriCamunda.clone();
    }

    public void setUriCamunda(UriCamunda uriCamunda) {
        this.uriCamunda = uriCamunda;
    }

}
