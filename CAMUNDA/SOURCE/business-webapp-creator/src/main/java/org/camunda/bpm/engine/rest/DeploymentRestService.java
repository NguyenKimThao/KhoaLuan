/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;
import org.camunda.bpm.engine.rest.call.DeploymentRestCall;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentResourceDto;
import org.camunda.bpm.engine.rest.support.ManagerPath;

/**
 *
 * @author TramSunny
 */
public class DeploymentRestService {

    public static List<DeploymentResourceDto> GetDeploymentResources(String deploymentID) {
        return DeploymentRestCall.GetResourceByDeploymentByID(deploymentID);
    }

    public static List<DeploymentDto> GetDeployments() {
        return DeploymentRestCall.GetDeployments();
    }

    public static ActionRespone DeploymentFileWar(String namefile, String data) {
        String error = "";
        try {
            String path = ConfigBusinessWebappCreator.pathServerCamunda + "/webapps/" + namefile;
            ManagerPath.CreateFile(path, data);
            return new ActionResponeSucess("DeploymentFileWar", "deployment sucess");
        } catch (IOException ex) {
            error += ex.getMessage();
        }
        return new ActionResponeError("DeploymentFileWar", "deployment error" + error);

    }

}
