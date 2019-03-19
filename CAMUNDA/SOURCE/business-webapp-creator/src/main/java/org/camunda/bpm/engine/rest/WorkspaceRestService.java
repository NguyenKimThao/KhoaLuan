/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;
import org.camunda.bpm.engine.db.CamundaConnection;
import org.camunda.bpm.engine.db.WordpressDAO;
import org.camunda.bpm.engine.db.WorkspaceDAO;
import org.camunda.bpm.engine.rest.call.DeploymentRestCall;
import org.camunda.bpm.engine.rest.call.ProcessDefinitionRestCall;
import org.camunda.bpm.engine.rest.call.RestEnginerCall;
import org.camunda.bpm.engine.rest.call.TaskRestCall;
import org.camunda.bpm.engine.rest.call.WordpressInstallRestCall;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentResourceDto;
import org.camunda.bpm.engine.rest.dto.repository.DeploymentWithDefinitionsDto;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;
import org.camunda.bpm.engine.rest.dto.repository.WorkspaceDto;
import org.camunda.bpm.engine.rest.dto.task.FormTaskDto;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 *
 * @author TramSunny
 */
public class WorkspaceRestService {

    public static List<WorkspaceDto> getWorkspaces() {
        List<WorkspaceDto> listRes = new ArrayList<>();
        try {
            List<WorkspaceDto> list = WorkspaceDAO.getWorkspaces();

            for (WorkspaceDto workspaceDto : list) {
                listRes.add(getWorkspaceDto(workspaceDto));
            }
            return listRes;
        } catch (SQLException ex) {
        }
        return listRes;
    }

    public static List<WorkspaceDto> getWorkspacesByUsername(String username) {
        List<WorkspaceDto> listRes = new ArrayList<>();
        try {
            List<WorkspaceDto> list = WorkspaceDAO.getWorkspacesByUsername(username);

            for (WorkspaceDto workspaceDto : list) {
                listRes.add(getWorkspaceDto(workspaceDto));
            }
            return listRes;
        } catch (SQLException ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listRes;
    }

    public static WorkspaceDto getWorkspaceByWorkspaceID(String workspaceID) {
        try {
            WorkspaceDto workspace = WorkspaceDAO.getWorkspaceByWorkspaceID(workspaceID);
            return getWorkspaceDto(workspace);
        } catch (SQLException ex) {
        }
        return null;
    }

    public static WorkspaceDto getWorkspaceDto(WorkspaceDto workspaceDto) {
        try {
            List<String> listprocesID = WorkspaceDAO.getProcessByWordspaceID(workspaceDto.getWorkspaceID());
            List<String> listDeploymentID = WorkspaceDAO.getDeploymentByWordspaceID(workspaceDto.getWorkspaceID());
            List<String> listWordpressID = WorkspaceDAO.getWordpressByWorkspaceID(workspaceDto.getWorkspaceID());
            List<ProcessDefinitionDto> listProcessDefinitions = new ArrayList<ProcessDefinitionDto>();
            List<DeploymentDto> listDeploymentDtos = new ArrayList<DeploymentDto>();
            List<WordpressDto> listWordpress = new ArrayList<WordpressDto>();
            for (String procesID : listprocesID) {
                ProcessDefinitionDto process = ProcessDefinitionRestCall.getProcessDefinitionByID(procesID);
                if (process != null) {
                    listProcessDefinitions.add(process);
                }
            }
            for (String deploymentID : listDeploymentID) {
                DeploymentDto deployment = DeploymentRestCall.getDeploymentByID(deploymentID);
                if (deployment != null) {
                    listDeploymentDtos.add(deployment);
                }
            }
            for (String wordpressID : listWordpressID) {
                WordpressDto wordpress = WordpressDAO.getWordpressByWordpressID(wordpressID);
                if (wordpress != null) {
                    listWordpress.add(wordpress);
                }
            }
            workspaceDto.setListProcess(listProcessDefinitions);
            workspaceDto.setListDeployment(listDeploymentDtos);
            workspaceDto.setListWordpress(listWordpress);
            return workspaceDto;
        } catch (SQLException ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return workspaceDto;
    }

    public static ActionRespone CreateNewWorkspace(String nameWorkspace, String username, String author) {
        String error = "No create sucess :";
        try {
            return WorkspaceDAO.CreateNewWorkspace(nameWorkspace, username, author);
        } catch (SQLException ex) {
            error += ex.getMessage();
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("CreateNewWorkspace", error);
    }

    public static ActionRespone CreateNewWorkspace(String nameWorkspace) {
        String error = "No create sucess :";
        try {
            return WorkspaceDAO.CreateNewWorkspace(nameWorkspace);
        } catch (SQLException ex) {
            error += ex.getMessage();
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("CreateNewWorkspace", error);
    }

    public static ActionRespone DeploymentBPMNToWorkspace(String type, String data, String workspaceID) {
        String error = "No deploy sucess :";
        try {
            WorkspaceDto workspace = getWorkspaceByWorkspaceID(workspaceID);
            DeploymentDto deployment = DeploymentRestCall.DeploymentBPMNToWorkspace(type, data);
            if (deployment == null) {
                return new ActionResponeError("DeploymentBPMNToWorkspace", "deployment error");
            }
            return AddDeploymentToWorkspace(workspace, deployment.getId());
        } catch (Exception ex) {
            error += ex.getMessage();
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("CreateNewWorkspace", error);
    }

    public static ActionRespone DeleteLogicDeployment(String workspaceID, String deploymentID) {
        try {
            List<ProcessDefinitionDto> listProcessDefinition = ProcessDefinitionRestCall.GetProcessDefinitionByDeploymentRescource(deploymentID);
            for (ProcessDefinitionDto processDefinitionDto : listProcessDefinition) {
                ActionRespone actionres = WorkspaceDAO.DeleteProcessByWorkspaceID(workspaceID, processDefinitionDto);
                if (actionres instanceof ActionResponeError) {
                    return actionres;
                }
            }
            return WorkspaceDAO.DeleteDeploymentByWorkspaceID(workspaceID, deploymentID);
        } catch (SQLException ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("DeleteDeployment", "DeleteDeployment error");
    }

    public static ActionRespone DeleteDeployment(String workspaceID, String deploymentID) {
        try {
            List<ProcessDefinitionDto> listProcessDefinition = ProcessDefinitionRestCall.GetProcessDefinitionByDeploymentRescource(deploymentID);
            for (ProcessDefinitionDto processDefinitionDto : listProcessDefinition) {
                ActionRespone actionres = WorkspaceDAO.DeleteProcessByWorkspace(processDefinitionDto);
                if (actionres instanceof ActionResponeError) {
                    return actionres;
                }
            }

            ActionRespone actionres = WorkspaceDAO.DeleteDeploymentByWorkspace(deploymentID);
            if (actionres instanceof ActionResponeError) {
                return actionres;
            }
            DeploymentDto deployment = DeploymentRestCall.getDeploymentByID(deploymentID);
            if (deployment == null) {
                return new ActionResponeError("DeleteDeployment", "DeleteDeployment error");
            }
            return DeploymentRestCall.DeleteDeployment(deploymentID);

        } catch (SQLException ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("DeleteDeployment", "DeleteDeployment error");
    }

    public static ActionRespone AddDeploymentToWorkspace(WorkspaceDto workspace, String deploymentID) {
        try {
            ActionRespone actionres = WorkspaceDAO.AddDeploymentToWorkspace(workspace.getWorkspaceID(), deploymentID);
            if (actionres instanceof ActionResponeError) {
                return actionres;
            }
            List<ProcessDefinitionDto> listProcessDefinition = ProcessDefinitionRestCall.GetProcessDefinitionByDeploymentRescource(deploymentID);

            String host = ConfigBusinessWebappCreator.uriService;
            for (WordpressDto wordpress : workspace.getListWordpress()) {
                WordpressDAO.CreateIfExistTableWordpress(wordpress);
                for (ProcessDefinitionDto processDef : listProcessDefinition) {
                    Long idProcess = WordpressDAO.CreateProcessWordpress(workspace, wordpress, processDef, host);
                    FormTaskDto formTask = TaskRestCall.GetFormTaskToProcessDefinition(processDef);
                    formTask.idProcess = idProcess;
                    WordpressDAO.CreateTaskWorkpress(wordpress, formTask);
                }
            }

            for (ProcessDefinitionDto deployedProcessDefinition : listProcessDefinition) {

                if (actionres instanceof ActionResponeError) {
                    return actionres;
                }

                actionres = WorkspaceDAO.AddProcessToWorkspace(workspace.getWorkspaceID(), deployedProcessDefinition.getId());
                if (actionres instanceof ActionResponeError) {
                    return actionres;
                }
            }
            return new ActionResponeSucess("CreateNewWorkspace", "deployment sucess");
        } catch (SQLException ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("AddDeploymentToWorkspace", "AddDeploymentToWorkspace error");
    }

    public static ActionRespone AddAllDeploymentToWorkspace(String workspaceID, List<String> listDeploymentID) {
        try {
            WorkspaceDto workspace = getWorkspaceByWorkspaceID(workspaceID);
            for (String deploymentID : listDeploymentID) {
                AddDeploymentToWorkspace(workspace, deploymentID);
            }
            return new ActionResponeSucess("CreateNewWorkspace", "deployment sucess");
        } catch (Exception ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("AddDeploymentToWorkspace", "AddDeploymentToWorkspace error");
    }

    public static ActionRespone DeploymentWordpressToWorkspace(String workspaceID, WordpressDeploymentDto wdDto) {
        try {
            ActionRespone actionres = null;
            WorkspaceDto workspace = getWorkspaceByWorkspaceID(workspaceID);
            for (WordpressDto wp : workspace.getListWordpress()) {
                if (wp.name.equals(wdDto.wordpressName)) {
                    return new ActionResponeError("DeploymentWordpressToWorkspace", wdDto.wordpressName + " is exists");
                }
            }
            wdDto.trim(workspace);
            WordpressDto wordpress = wdDto.getWordpress();
            WordpressDAO.CreateIfExistDatabaseWordpress(wordpress);
            WordpressInstallRestCall install = new WordpressInstallRestCall(wdDto, wdDto.wpinstall);

            actionres = install.deployWordpress();
            if (actionres instanceof ActionResponeError) {
                return actionres;
            }

            WordpressDAO.CreateIfExistTableWordpress(wordpress);

            String host = ConfigBusinessWebappCreator.uriService;
            for (ProcessDefinitionDto processDef : workspace.getListProcess()) {
                Long idProcess = WordpressDAO.CreateProcessWordpress(workspace, wordpress, processDef, host);
                FormTaskDto formTask = TaskRestCall.GetFormTaskToProcessDefinition(processDef);
                formTask.idProcess = idProcess;
                WordpressDAO.CreateTaskWorkpress(wordpress, formTask);
            }
            System.out.println("CreateTaskWorkpress:");
            actionres = WorkspaceDAO.CreateWordpressByWorkspaceID(workspaceID, wordpress);

            if (actionres instanceof ActionResponeError) {
                return actionres;
            }

            return new ActionResponeSucess("CreateNewWorkspace", wordpress.wphost + "/" + wordpress.directory);
        } catch (Exception ex) {
            return new ActionResponeError("DeploymentWordpressToWorkspace", "Error: " + ex.getMessage());
        }
    }

    public static ActionRespone DeleteWordpressToWorkspace(String workspaceID, String wordpressID) {
        try {
            WorkspaceDto workspace = getWorkspaceByWorkspaceID(workspaceID);
            WordpressDto wordpress = WordpressDAO.getWordpressByWordpressID(wordpressID);

            if (wordpress == null || workspace == null) {
                return new ActionResponeError("DeleteWordpressToWorkspace", "DeleteWordpressToWorkspace error");
            }
            WordpressDAO.DeleteProcess(wordpress);
            WorkspaceDAO.DeleteWordpress(workspace, wordpress);
            return new ActionResponeSucess("DeleteWordpressToWorkspace", "delete wordpress" + wordpress.name + " sucess");
        } catch (Exception ex) {
            Logger.getLogger(WorkspaceRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ActionResponeError("DeleteWordpressToWorkspace", "DeleteWordpressToWorkspace error");
    }

    public static boolean CheckWordpressInWorkspace(String workspaceID, String wordpressID) {
        return WorkspaceDAO.CheckWordpressInWorkspace(workspaceID, wordpressID);
    }

    public static boolean CheckProcessInWorkspace(String workspaceID, String processID) {
        return WorkspaceDAO.CheckProcessInWorkspace(workspaceID, processID);
    }

    public static RestEnginerCall getRestEnginerCallByWorkspaceID(String workspaceID) {
        try {
            WorkspaceDto workspace = WorkspaceDAO.getWorkspaceByWorkspaceID(workspaceID);
            if (workspace == null) {
                return null;
            }
            return RestEnginerCurrentService.getRestEngineCurrent(workspace.getUsername(), workspace.getAuthor());
        } catch (SQLException ex) {
        }
        return null;
    }

    public static void UpdateAuthor(RestEnginerCurrentService.AuthUser user) {
        WorkspaceDAO.UpdateAuthor(user);
        RestEnginerCurrentService.updateRestEngine(user);
    }
}
