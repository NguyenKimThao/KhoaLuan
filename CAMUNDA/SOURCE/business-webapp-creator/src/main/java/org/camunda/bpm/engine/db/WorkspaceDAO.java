/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.WorkspaceRestService;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;
import org.camunda.bpm.engine.rest.dto.repository.WorkspaceDto;

/**
 *
 * @author TramSunny
 */
public class WorkspaceDAO {

    public static List<WorkspaceDto> getWorkspaces() throws SQLException {
        List<WorkspaceDto> list = new ArrayList<>();
        String query = "select *from workspace ";
        ResultSet res = CamundaConnection.executeQuery(query, true);
        while (res.next()) {
            list.add(new WorkspaceDto(res));
        }
        return list;

    }

    public static List<WorkspaceDto> getWorkspacesByUsername(String username) throws SQLException {
        List<WorkspaceDto> list = new ArrayList<>();
        String query = "select *from workspace where username=?";
        ResultSet res = CamundaConnection.executeQuery(query, username, true);
        while (res.next()) {
            list.add(new WorkspaceDto(res));
        }
        return list;

    }

    public static WorkspaceDto getWorkspaceByWorkspaceID(String workspaceID) throws SQLException {
        String query = "select *from workspace where workspaceID= ?";
        ResultSet res = CamundaConnection.executeQuery(query, workspaceID);
        return new WorkspaceDto(res);
    }

    public static List<String> getProcessByWordspaceID(String workspaceID) throws SQLException {
        List<String> list = new ArrayList<>();
        String query = "select *from workspace_process  where workspaceID= ? ";
        ResultSet res = CamundaConnection.executeQuery(query, workspaceID, true);
        while (res.next()) {
            list.add(res.getString("processID"));
        }
        return list;
    }

    public static List<String> getWordpressByWorkspaceID(String workspaceID) throws SQLException {
        List<String> list = new ArrayList<>();
        String query = "select *from workspace_wordpress  where workspaceID= ? ";
        ResultSet res = CamundaConnection.executeQuery(query, workspaceID, true);
        while (res.next()) {
            list.add(res.getString("wordpressID"));
        }
        return list;
    }

    public static List<String> getDeploymentByWordspaceID(String workspaceID) throws SQLException {
        List<String> list = new ArrayList<>();
        String query = "select *from workspace_deployment  where workspaceID= ? ";
        ResultSet res = CamundaConnection.executeQuery(query, workspaceID, true);
        while (res.next()) {
            list.add(res.getString("deploymentID"));
        }
        return list;
    }

    public static boolean CheckWorkspaceByname(String nameWorkspace, String username) throws SQLException {
        String query = "select * from workspace where workspaceName= ? and username= ?";
        List<Object> list = new ArrayList<>();
        list.add(nameWorkspace);
        list.add(username);
        ResultSet res = CamundaConnection.executeQuery(query, list, true);
        if (!res.next()) {
            return false;
        }
        WorkspaceDto workspace = new WorkspaceDto(res);
        if (workspace == null) {
            return false;
        }
        return true;
    }

    public static boolean CheckWorkspaceByname(String nameWorkspace) throws SQLException {
        String query = "select * from workspace where workspaceName=?";
        ResultSet res = CamundaConnection.executeQuery(query, nameWorkspace, true);
        if (!res.next()) {
            return false;
        }
        WorkspaceDto workspace = new WorkspaceDto(res);
        if (workspace == null) {
            return false;
        }
        return true;
    }

    public static boolean CheckWorkspaceByID(String workspaceID) throws SQLException {
        String query = "select * from workspace where workspaceID=?";
        ResultSet res = CamundaConnection.executeQuery(query, workspaceID, true);
        if (!res.next()) {
            return false;
        }
        WorkspaceDto workspace = new WorkspaceDto(res);
        if (workspace == null) {
            return false;
        }
        return true;
    }

    public static boolean CheckWordpressInWorkspace(String workspaceID, String wordpressID) {
        try {
            String query = "SELECT * FROM workspace_wordpress where workspaceID=? AND wordpressID = ?";
            List<Object> list = new ArrayList<>();
            list.add(workspaceID);
            list.add(wordpressID);
            ResultSet res = CamundaConnection.executeQuery(query, list, true);
            if (!res.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
        }
        return false;
    }

    public static ActionRespone CreateNewWorkspace(String nameWorkspace, String username, String author) throws SQLException {
        nameWorkspace = nameWorkspace.toUpperCase();
        if (CheckWorkspaceByname(nameWorkspace, username)) {
            return new ActionResponeError("CreateNewWorkspace", nameWorkspace + " is exists");
        }
        Date date = new Date();
        String idWorkspace = "Workspace-" + date.getTime();
        System.out.println("idWorkspace!!!!" + idWorkspace);
        String query = "insert into workspace(workspaceID,workspaceName,username,author) value(?,?,?,?)";
        List<Object> list = new ArrayList<Object>();
        list.add(idWorkspace);
        list.add(nameWorkspace);
        list.add(username);
        list.add(author);
        int res = CamundaConnection.executeUpdate(query, list);
        if (res != 1) {
            return new ActionResponeError("CreateNewWorkspace", "No create sucess");
        }
        return new ActionResponeSucess("CreateNewWorkspace", "Create sucess");
    }

    public static ActionRespone CreateNewWorkspace(String nameWorkspace) throws SQLException {
        nameWorkspace = nameWorkspace.toUpperCase();
        if (CheckWorkspaceByname(nameWorkspace)) {
            return new ActionResponeError("CreateNewWorkspace", nameWorkspace + " is exists");
        }
        Date date = new Date();
        String idWorkspace = "Workspace-" + date.getTime();
        System.out.println("idWorkspace!!!!" + idWorkspace);
        String query = "insert into workspace(workspaceID,workspaceName) value(?,?)";
        List<Object> list = new ArrayList<Object>();
        list.add(idWorkspace);
        list.add(nameWorkspace);
        int res = CamundaConnection.executeUpdate(query, list);
        if (res != 1) {
            return new ActionResponeError("CreateNewWorkspace", "No create sucess");
        }
        return new ActionResponeSucess("CreateNewWorkspace", "Create sucess");
    }

    public static ActionRespone AddProcessToWorkspace(String workspaceID, String processID) throws SQLException {

        if (!CheckWorkspaceByID(workspaceID)) {
            return new ActionResponeError("AddProcessToWorkspace", workspaceID + " isn't exists");
        }
        String query = "insert into workspace_process(workspaceID,processID) values(?,?)";
        List<Object> object = new ArrayList<>();
        object.add(workspaceID);
        object.add(processID);
        int res = CamundaConnection.executeUpdate(query, object);
        if (res != 1) {
            return new ActionResponeError("AddProcessToWorkspace", "No create sucess when add database");
        }
        return new ActionResponeSucess("AddProcessToWorkspace", "Create sucess");
    }

    public static ActionRespone AddDeploymentToWorkspace(String workspaceID, String deploymentID) throws SQLException {
        if (!CheckWorkspaceByID(workspaceID)) {
            return new ActionResponeError("AddDeploymentToWorkspace", workspaceID + " isn't exists");
        }
        String query = "insert into workspace_deployment(workspaceID,deploymentID) values(?,?)";
        List<Object> object = new ArrayList<>();
        object.add(workspaceID);
        object.add(deploymentID);
        int res = CamundaConnection.executeUpdate(query, object);
        if (res != 1) {
            return new ActionResponeError("AddDeploymentToWorkspace", "No create sucess when add database");
        }
        return new ActionResponeSucess("AddProcessToWorkspace", "Create sucess");

    }

    public static ActionRespone DeleteDeploymentByWorkspaceID(String workspaceID, String deploymentID) throws SQLException {
        if (!CheckWorkspaceByID(workspaceID)) {
            return new ActionResponeError("DeleteDeploymentByWorkspaceID", workspaceID + " isn't exists");
        } else {
            String query = "delete from  workspace_deployment where workspaceID = ? AND deploymentID = ? ";
            List<Object> object = new ArrayList<>();
            object.add(workspaceID);
            object.add(deploymentID);
            int res = CamundaConnection.executeUpdate(query, object);
            if (res != 1) {
                return new ActionResponeError("DeleteDeploymentByWorkspaceID", "No create sucess when delete in  database");
            }
        }
        return new ActionResponeSucess("DeleteDeploymentByWorkspaceID", "Create sucess");
    }

    public static ActionRespone DeleteDeploymentByWorkspace(String deploymentID) throws SQLException {

        String query = "delete from  workspace_deployment where deploymentID = ? ";
        List<Object> object = new ArrayList<>();
        object.add(deploymentID);
        int res = CamundaConnection.executeUpdate(query, object);
        if (res != 1) {
            return new ActionResponeError("DeleteDeploymentByWorkspaceID", "No create sucess when delete in  database");
        }
        return new ActionResponeSucess("DeleteDeploymentByWorkspaceID", "Create sucess");
    }

    public static ActionRespone DeleteProcessByWorkspaceID(String workspaceID, ProcessDefinitionDto processDefinition) throws SQLException {
        if (!CheckWorkspaceByID(workspaceID)) {
            return new ActionResponeError("DeleteProcessByWorkspaceID", workspaceID + " isn't exists");
        } else {
            String query = "delete from  workspace_process where workspaceID = ? AND processID = ? ";
            List<Object> object = new ArrayList<>();
            object.add(workspaceID);
            object.add(processDefinition.getId());
            int res = CamundaConnection.executeUpdate(query, object);
            if (res != 1) {
                return new ActionResponeError("DeleteProcessByWorkspaceID", "No create sucess when delete in  database");
            }
        }
        return new ActionResponeSucess("DeleteProcessByWorkspaceID", "Create sucess");
    }

    public static ActionRespone DeleteProcessByWorkspace(ProcessDefinitionDto processDefinition) throws SQLException {

        String query = "delete from  workspace_process where processID = ? ";
        List<Object> object = new ArrayList<>();
        object.add(processDefinition.getId());
        int res = CamundaConnection.executeUpdate(query, object);
        if (res != 1) {
            return new ActionResponeError("DeleteProcessByWorkspaceID", "No create sucess when delete in  database");
        }
        return new ActionResponeSucess("DeleteProcessByWorkspaceID", "Create sucess");
    }

    public static ActionRespone CreateWordpress(WordpressDto wordpress) throws SQLException {
        String query = "INSERT INTO `wordpress`(`id`,`name`,`wphost`,`directory`,`dbhost`,`dbname`,`uname`,`pwd`,`prefix`) VALUES (?,?,?,?,?,?,?,?,?)";
        List<Object> list = new ArrayList<>();
        list.add(wordpress.id);
        list.add(wordpress.name);
        list.add(wordpress.wphost);
        list.add(wordpress.directory);
        list.add(wordpress.dbhost);
        list.add(wordpress.dbname);
        list.add(wordpress.uname);
        list.add(wordpress.pwd);
        list.add(wordpress.prefix);

        int res = CamundaConnection.executeUpdate(query, list);
        if (res != 1) {
            return new ActionResponeError("CreateWordpress", "No create sucess in  database");
        }
        return new ActionResponeSucess("CreateWordpress", "Create sucess");
    }

    public static ActionRespone AddWordpressToWorksapce(String workspaceID, WordpressDto wordpress) throws SQLException {
        String query = "INSERT INTO `workspace_wordpress`(`workspaceID`,`wordpressID`) VALUES(?,?)";
        List<Object> list = new ArrayList<>();
        list.add(workspaceID);
        list.add(wordpress.id);
        int res = CamundaConnection.executeUpdate(query, list);
        if (res != 1) {
            return new ActionResponeError("CreateWordpressByWorkspaceID", "No create sucess in  database");
        }
        return new ActionResponeSucess("CreateWordpressByWorkspaceID", "Create sucess");
    }

    public static ActionRespone CreateWordpressByWorkspaceID(String workspaceID, WordpressDto wdDto) {

        try {
            ActionRespone actionres = CreateWordpress(wdDto);
            if (actionres instanceof ActionResponeError) {
                return actionres;
            }
            return AddWordpressToWorksapce(workspaceID, wdDto);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return new ActionResponeError("CreateWordpress", "No create sucess in  database");
    }

    public static void DeleteWordpress(WorkspaceDto workspace, WordpressDto wordpress) throws SQLException {
        String query = "DELETE FROM  workspace_wordpress WHERE workspaceID= ? AND wordpressID= ?";
        List<Object> list = new ArrayList<>();
        list.add(workspace.getWorkspaceID());
        list.add(wordpress.id);
        CamundaConnection.executeUpdate(query, list);
    }

    public static boolean CheckProcessInWorkspace(String workspaceID, String processID) {
        try {
            String query = "SELECT * FROM workspace_process where workspaceID=? AND processID = ?";
            List<Object> list = new ArrayList<>();
            list.add(workspaceID);
            list.add(processID);
            ResultSet res = CamundaConnection.executeQuery(query, list, true);
            System.out.println("aoday");
            if (!res.next()) {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static void UpdateAuthor(RestEnginerCurrentService.AuthUser user) {
        try {
            String query = "UPDATE workspace SET author=? WHERE username= ?";
            List<Object> list = new ArrayList<>();
            list.add(user.auth);
            list.add(user.username);
            CamundaConnection.executeUpdate(query, list);
        } catch (SQLException ex) {
        }
    }
}
