/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.dto.entity;

/**
 *
 * @author TramSunny
 */
public class ActionResponeImf implements ActionRespone {

    public String nameAction;
    public String type;
    public String message;

    public ActionResponeImf(String nameAction, String type, String message) {
        this.nameAction = nameAction;
        this.type = type;
        this.message = message;
    }

 

}
