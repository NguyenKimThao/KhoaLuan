/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.camunda.bpm.engine.db.WorkspaceDAO;
import org.camunda.bpm.engine.rest.WorkspaceRestService;
import org.camunda.bpm.engine.rest.call.CamundaEngineRestCall;
import org.camunda.bpm.engine.rest.call.ProcessDefinitionRestCall;
import org.camunda.bpm.engine.rest.call.RestEnginerCall;
import org.camunda.bpm.engine.rest.call.TaskRestCall;
import org.camunda.bpm.engine.rest.dto.KeyFormDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.repository.WorkspaceDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spinjar.com.minidev.json.parser.ParseException;

/**
 *
 * @author TramSunny
 */
@Controller
@ResponseBody
@Produces(MediaType.APPLICATION_JSON)
public class EngineRestConrtoller {

    @RequestMapping(value = "/api-extend/workspace/{workspaceID}/wordpress/{wordpressID}/process-definition/{id}/start", method = RequestMethod.POST)
    public ProcessInstanceDto GetDeploymentResources(@PathVariable("workspaceID") String workspaceID, @PathVariable("wordpressID") String wordpressID, @PathVariable("id") String id) {
        boolean check = WorkspaceRestService.CheckProcessInWorkspace(workspaceID, id);
        System.out.println("ket qua: " + check);
        if (check == false) {
            return null;
        }
        RestEnginerCall rescall = WorkspaceRestService.getRestEnginerCallByWorkspaceID(workspaceID);
        if (rescall == null) {
            return null;
        }
        ProcessDefinitionDto procesDef = ProcessDefinitionRestCall.getProcessDefinitionByID(rescall, id);
        ProcessInstanceDto processIns = ProcessDefinitionRestCall.StartProcessDefinition(rescall, procesDef);
//            TaskDto task = TaskRestCall.getTask(processIns);
//            FormTaskDto formTask = PasreFormTask.pasre(task);
        return processIns;
    }

    @RequestMapping(value = "/api/task/processInstance/{processInstanceID}", method = RequestMethod.GET)
    public TaskDto[] getTaskByProcessInstance(@PathVariable("processInstanceID") String processInstanceID) {
        try {
            TaskDto[] task = TaskRestCall.getTask(processInstanceID);
            return task;
        } catch (ParseException ex) {
            Logger.getLogger(EngineRestConrtoller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @RequestMapping(value = "/api/task/{taskID}/form", method = RequestMethod.GET)
    public String getTask(@PathVariable("taskID") String taskID) {
        try {
            TaskDto task = TaskRestCall.getTaskById(taskID);
            System.out.println("Load duoc task");
            KeyFormDto keyform = TaskRestCall.getFormKey(task);
            return TaskRestCall.getForm(task, keyform);
        } catch (ParseException ex) {
            Logger.getLogger(EngineRestConrtoller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @RequestMapping(value = "/api/**", method = RequestMethod.GET)
    public String apiCamundaGet(HttpServletRequest request) {
        System.out.println("vaoday");
        String urlRequest = request.getRequestURI().substring(5 + request.getContextPath().length());

        if (urlRequest.startsWith("/")) {
            urlRequest = urlRequest.substring(1);
        }
        if (request.getQueryString() != null && !request.getQueryString().equals("")) {
            urlRequest = urlRequest + "?" + request.getQueryString();
        }
        return CamundaEngineRestCall.apiGet(urlRequest);
    }

    @RequestMapping(value = "/api/**", method = RequestMethod.POST)
    public String apiCamundaPost(HttpServletRequest request, @RequestHeader("Content-Type") String contenttype, @RequestBody String data) {
        String urlRequest = request.getRequestURI().substring(5 + request.getContextPath().length());
        if (urlRequest.startsWith("/")) {
            urlRequest = urlRequest.substring(1);
        }
        if (request.getQueryString() != null && !request.getQueryString().equals("")) {
            urlRequest = urlRequest + "?" + request.getQueryString();
        }
        return CamundaEngineRestCall.apiPost(urlRequest, contenttype, data);
    }

}
