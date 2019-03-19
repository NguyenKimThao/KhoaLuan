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
public class FormTaskDto {

    public String idTask;
    public String nameTask;
    public Long idProcess;
    public String post_content;
    public String script_content;

    @Override
    public String toString() {
        return "FormTaskDto{" + "idTask=" + idTask + ", nameTask=" + nameTask + ", processID=" + idProcess + ", post_content=" + post_content + ", script_content=" + script_content + '}';
    }

}
