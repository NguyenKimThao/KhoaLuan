/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.dto.task;

/**
 *
 * @author TramSunny
 */
public class TaskLinkDto {

    public String _links;
    public String _embedded;

    public TaskLinkDto(int count) {
        this.count = count;
    }
    public int count;
}
