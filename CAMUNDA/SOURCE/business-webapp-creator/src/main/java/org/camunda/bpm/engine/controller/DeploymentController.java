/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.controller;

import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.camunda.bpm.engine.rest.DeploymentRestService;
import org.camunda.bpm.engine.rest.WorkspaceRestService;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentResourceDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentController {

    @RequestMapping(value = "/engine/default/deployment/{deploymentID}/resources", method = RequestMethod.GET)
    public List<DeploymentResourceDto> GetDeploymentResources(@PathVariable("deploymentID") String deploymentID) {
        return DeploymentRestService.GetDeploymentResources(deploymentID);
    }

    @RequestMapping(value = "/engine/default/deployment/", method = RequestMethod.GET)
    public List<DeploymentDto> GetDeployments() {
        return DeploymentRestService.GetDeployments();
    }

    @RequestMapping(value = "/engine/default/deployment/createfilewar/{namefile}", method = RequestMethod.POST)
    public ActionRespone DeploymentFileWar(@PathVariable("namefile") String namefile, @RequestBody String data) {
        return DeploymentRestService.DeploymentFileWar(namefile + ".war", data);
    }
}
