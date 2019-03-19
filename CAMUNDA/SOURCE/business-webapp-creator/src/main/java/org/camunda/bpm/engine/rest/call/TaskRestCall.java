/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.call;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.controller.EngineRestConrtoller;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.dto.KeyFormDto;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.task.FormTaskDto;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.camunda.bpm.engine.rest.dto.task.TaskLinkDto;
import org.camunda.bpm.engine.rest.support.PasreFormTask;
import org.springframework.web.bind.annotation.PathVariable;
import spinjar.com.minidev.json.JSONObject;
import spinjar.com.minidev.json.parser.JSONParser;
import spinjar.com.minidev.json.parser.ParseException;

/**
 *
 * @author TramSunny
 */
public class TaskRestCall {

    public static TaskDto getTask(ProcessInstanceDto process) throws ParseException {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet("task?processInstanceId=" + process.getId());
        Gson json = new Gson();
        TaskDto[] taskArray = json.fromJson(body, TaskDto[].class);
        return taskArray[0];
    }

    public static TaskDto[] getTask(String processID) throws ParseException {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet("task?processInstanceId=" + processID);
        System.out.println(body);
        Gson json = new Gson();
        TaskDto[] taskArray = json.fromJson(body, TaskDto[].class);
        return taskArray;
    }

    public static KeyFormDto getFormKey(TaskDto task) throws ParseException {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet("task/" + task.getId() + "/form");
        System.out.println(body);
        Gson json = new Gson();
        KeyFormDto keyform = json.fromJson(body, KeyFormDto.class);
        return keyform;
    }

    public static Map<String, VariableValueDto> getVariableValue(TaskDto task) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet("task/" + task.getId() + "/form-variables");
        System.out.println(body);
        Gson json = new Gson();
        HashMap<String, VariableValueDto> map = json.fromJson(body, HashMap.class);
        return map;
    }

    public static String getForm(TaskDto task, KeyFormDto formKey) {
        String key = formKey.key;
        if (key == null || key.isEmpty()) {
            key = "task/" + task.getId() + "/rendered-form";
        } else {
            if (key.indexOf("embedded:engine://engine/:engine") > -1) {
                key = key.replaceAll("embedded:engine://engine/:engine", "/engine-rest");
            } else {
                key = key.replaceAll("embedded:app:", formKey.contextPath + "/");
            }
        }
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet(key);
        return body;

    }

    public static FormTaskDto GetFormTaskToProcessDefinition(ProcessDefinitionDto procesDef) {
        try {
            ProcessInstanceDto processIns = ProcessDefinitionRestCall.StartProcessDefinition(procesDef);
            TaskDto task = TaskRestCall.getTask(processIns);
            FormTaskDto formTask = PasreFormTask.pasre(task);
            return formTask;
        } catch (ParseException ex) {
            Logger.getLogger(EngineRestConrtoller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static TaskDto getTaskById(String taskID) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet("task/" + taskID + "/");
        System.out.println(body);
        Gson json = new Gson();
        TaskDto task = json.fromJson(body, TaskDto.class);
        return task;
    }
}
