/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.call;

import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentResourceDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;

/**
 *
 * @author TramSunny
 */
public class ProcessDefinitionRestCall {

    public static ProcessDefinitionDto getProcessDefinitionByID(String ID) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        if (res == null) {
            return null;
        }
        return getProcessDefinitionByID(res, ID);
    }

    public static ProcessDefinitionDto getProcessDefinitionByID(RestEnginerCall res, String ID) {
        ProcessDefinitionDto process = null;
        try {
            String body = res.executeGet("process-definition/" + ID);
            if (body == null) {
                return null;
            }
            Gson json = new Gson();
            process = json.fromJson(body, ProcessDefinitionDto.class);
        } catch (Exception ex) {
        }
        return process;
    }

    public static List<ProcessDefinitionDto> GetProcessDefinitionByDeploymentRescource(String deploymentId) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        String body = res.executeGet("process-definition?deploymentId=" + deploymentId);
        Gson json = new Gson();
        ProcessDefinitionDto[] listProces = json.fromJson(body, ProcessDefinitionDto[].class);
        return Arrays.asList(listProces);

    }

    public static ProcessInstanceDto StartProcessDefinition(ProcessDefinitionDto process) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        if (res == null) {
            return null;
        }
        return StartProcessDefinition(res, process);
    }

    public static ProcessInstanceDto StartProcessDefinition(RestEnginerCall res, ProcessDefinitionDto process) {
        Gson json = new Gson();
        String data = json.toJson(process);
        String body = res.executeWithPostData("process-definition/" + process.getId() + "/start", "application/json", data);
        Gson jsonRs = new Gson();
        ProcessInstanceDto processInstance = jsonRs.fromJson(body, ProcessInstanceDto.class);
        return processInstance;
    }

}
