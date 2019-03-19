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
public class ActionResponeError extends ActionResponeImf {

    public ActionResponeError(String nameAction, String message) {
        super(nameAction, "error", message);
    }
}
