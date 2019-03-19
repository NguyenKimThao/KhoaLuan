/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.call;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.HttpMethod;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentResourceDto;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentWithDefinitionsDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;

/**
 *
 * @author TramSunny
 */
public class DeploymentRestCall {

    public static DeploymentDto DeploymentBPMNToWorkspace(String type, String data) {
        DeploymentDto deployment = null;
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        if (res == null) {
            return null;
        }
        String body = res.executeWithPostData("deployment/create", type, data);
        if (body == null) {
            return null;
        }
        Gson json = new Gson();
        deployment = json.fromJson(body, DeploymentDto.class);

        return deployment;
    }

    public static DeploymentDto getDeploymentByID(String deploymentID) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        DeploymentDto deploymentDto = null;
        try {
            String body = res.executeGet("deployment/" + deploymentID);
            Gson json = new Gson();
            deploymentDto = json.fromJson(body, DeploymentDto.class);
        } catch (Exception ex) {

        }

        return deploymentDto;
    }

    public static List<DeploymentResourceDto> GetResourceByDeploymentByID(String deploymentID) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        List<DeploymentResourceDto> listDeploymentResource = null;
        try {
            String body = res.executeGet("deployment/" + deploymentID + "/resources");
            Gson json = new Gson();
            DeploymentResourceDto[] listProces = json.fromJson(body, DeploymentResourceDto[].class);
            listDeploymentResource = Arrays.asList(listProces);
        } catch (Exception ex) {

        }

        return listDeploymentResource;
    }

    public static ActionRespone DeleteDeployment(String deploymentID) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        res.execute("deployment/" + deploymentID + "?cascade=true&skipCustomListeners=true&skipIoMappings=true", HttpMethod.DELETE);
        return new ActionResponeSucess("DeleteDeploymentByWorkspaceID", "Delete deployment sucess");
    }

    public static List<DeploymentDto> GetDeployments() {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        List<DeploymentDto> lisDeployment = null;
        try {
            String body = res.executeGet("deployment");
            Gson json = new Gson();
            DeploymentDto[] arrDeployment = json.fromJson(body, DeploymentDto[].class);
            lisDeployment = Arrays.asList(arrDeployment);
        } catch (Exception ex) {

        }

        return lisDeployment;
    }

}
