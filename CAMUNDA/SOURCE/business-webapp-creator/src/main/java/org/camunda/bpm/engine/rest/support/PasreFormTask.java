/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.support;

import org.camunda.bpm.engine.rest.call.TaskRestCall;
import org.camunda.bpm.engine.rest.dto.KeyFormDto;
import org.camunda.bpm.engine.rest.dto.task.FormTaskDto;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import spinjar.com.minidev.json.parser.ParseException;

/**
 *
 * @author TramSunny
 */
public class PasreFormTask {

    public static FormTaskDto pasre(TaskDto task) throws ParseException {
        FormTaskDto formTask = new FormTaskDto();
        formTask.idTask = task.getTaskDefinitionKey();
        formTask.nameTask = task.getName();
        KeyFormDto formKey = TaskRestCall.getFormKey(task);
        String content = TaskRestCall.getForm(task, formKey);
        formTask.post_content = content;
        formTask.script_content = "";
        return formTask;
    }
}
