/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.controller;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.camunda.bpm.engine.rest.WorkspaceRestService;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.WorkspaceDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@Produces(MediaType.APPLICATION_JSON)
public class WorkspaceController {

    @RequestMapping(value = "/engine/default/workspace", method = RequestMethod.GET)
    public List<WorkspaceDto> GetWorkspace() {
        return WorkspaceRestService.getWorkspaces();
    }

    @RequestMapping(value = "/engine/default/workspace/username/{username}", method = RequestMethod.GET)
    public List<WorkspaceDto> GetWorkspaceByUsername(@PathVariable("username") String username) {
        return WorkspaceRestService.getWorkspacesByUsername(username);
    }

    @RequestMapping(value = "/engine/default/workspace/username/{username}/create/", method = RequestMethod.POST)
    public ActionRespone CreateNewWorkspace(@RequestHeader("Authorization") String author, @RequestBody String nameWorkspace, @PathVariable("username") String username) {
        return WorkspaceRestService.CreateNewWorkspace(nameWorkspace, username, author);
    }

    @RequestMapping(value = "/engine/default/workspace/create/", method = RequestMethod.POST)
    public ActionRespone CreateNewWorkspace(@RequestBody String nameWorkspace) {
        return WorkspaceRestService.CreateNewWorkspace(nameWorkspace);
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}", method = RequestMethod.GET)
    public WorkspaceDto getWorkspaceByWorkspaceID(@PathVariable("workspaceID") String workspaceID) {
        return WorkspaceRestService.getWorkspaceByWorkspaceID(workspaceID);
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}/deployment/", method = RequestMethod.POST)
    public ActionRespone DeploymentBPMNToWorkspace(@RequestHeader("Content-Type") String contenttype, @RequestBody String data, @PathVariable("workspaceID") String workspaceID) {
        return WorkspaceRestService.DeploymentBPMNToWorkspace(contenttype, data, workspaceID);
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}/delete/deployment/{deploymentID}", method = RequestMethod.DELETE)
    public ActionRespone DeleteDeployment(@PathVariable("workspaceID") String workspaceID, @PathVariable("deploymentID") String deploymentID) {
        return WorkspaceRestService.DeleteDeployment(workspaceID, deploymentID);
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}/deletelogic/{deploymentID}", method = RequestMethod.DELETE)
    public ActionRespone DeleteLogicDeployment(@PathVariable("workspaceID") String workspaceID, @PathVariable("deploymentID") String deploymentID) {
        return WorkspaceRestService.DeleteLogicDeployment(workspaceID, deploymentID);
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}/deployment/list", method = RequestMethod.POST)
    public ActionRespone AddDeploymentToWorkspace(@PathVariable("workspaceID") String workspaceID, @RequestBody String data) {
        System.out.println(data);
        Gson gson = new Gson();
        String[] array = gson.fromJson(data, String[].class);
        return WorkspaceRestService.AddAllDeploymentToWorkspace(workspaceID, Arrays.asList(array));
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}/deployment/wordpress", method = RequestMethod.POST)
    public ActionRespone DeploymentWordpressToWorkspace(@PathVariable("workspaceID") String workspaceID, @RequestBody String data) {
        Gson gson = new Gson();
        WordpressDeploymentDto wdDto = gson.fromJson(data, WordpressDeploymentDto.class);
        return WorkspaceRestService.DeploymentWordpressToWorkspace(workspaceID, wdDto);
    }

    @RequestMapping(value = "/engine/default/workspace/{workspaceID}/delete/wordpress/{wordpressID}", method = RequestMethod.DELETE)
    public ActionRespone DeleteWordpressToWorkspace(@PathVariable("workspaceID") String workspaceID, @PathVariable("wordpressID") String wordpressID) {
        return WorkspaceRestService.DeleteWordpressToWorkspace(workspaceID, wordpressID);
    }
}
