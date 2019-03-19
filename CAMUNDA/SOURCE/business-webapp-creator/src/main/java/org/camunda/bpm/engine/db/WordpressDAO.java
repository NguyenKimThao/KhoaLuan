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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;
import org.camunda.bpm.engine.rest.dto.repository.WorkspaceDto;
import org.camunda.bpm.engine.rest.dto.task.FormTaskDto;

/**
 *
 * @author TramSunny
 */
public class WordpressDAO {

    public static WordpressDto getWordpressByWordpressID(String wordpressID) {
        try {
            String query = "select *from wordpress  where id= ? ";
            ResultSet res = CamundaConnection.executeQuery(query, wordpressID);
            return new WordpressDto(res);
        } catch (SQLException ex) {
            Logger.getLogger(WordpressDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void CreateIfExistDatabaseWordpress(WordpressDto wordpress) throws SQLException, Exception {
        WordpressConnection con = new WordpressConnection(wordpress);
        con.createIfExistDatabase();
    }

    public static void CreateIfExistTableWordpress(WordpressDto wordpress) throws SQLException {
        WordpressConnection con = new WordpressConnection(wordpress);
        //Create wp_task;
        String sql = "CREATE TABLE IF NOT EXISTS " + wordpress.getTable("tasks") + " ( "
                + "  `idTask` VARCHAR(100) NOT NULL, "
                + "  `nameTask` VARCHAR(500) NOT NULL, "
                + "  `idProcess` INT NOT NULL, "
                + "  `post_content` LONGTEXT NOT NULL, "
                + "  PRIMARY KEY (`idtask`, `idprocess`)) ";
        con.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + wordpress.getTable("processdefinition") + "(\n"
                + "  `id` int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  `idprocessdefinition` varchar(100) DEFAULT NULL,\n"
                + "  `idworkspace` varchar(100) DEFAULT NULL,\n"
                + "  `hostworkspace` varchar(500) DEFAULT NULL,\n"
                + "  `idwordpress` varchar(100),\n"
                + "  `content` longtext,\n"
                + "  PRIMARY KEY (`id`))";
        con.executeUpdate(sql);

    }

    public static Long CreateProcessDefinitionWordpress(WorkspaceDto workspace, WordpressDto wordpress, ProcessDefinitionDto process, String hostWorkspace) throws SQLException {
        WordpressConnection con = WordpressConnection.getWordpressConnection(wordpress);
        String sql = "INSERT INTO " + wordpress.getTable("processdefinition") + "(`idprocessdefinition`,`idworkspace`,`hostworkspace`,`idwordpress`,`content`) VALUES (?,?,?,?,?)";
        List<Object> list = new ArrayList<>();
        list.add(process.getId());
        list.add(workspace.getWorkspaceID());
        list.add(hostWorkspace);
        list.add(wordpress.id);
        list.add("all");

        con.executeUpdate(sql, list);
        String sqlCurrent = "SELECT MAX(ID) as ID FROM " + wordpress.getTable("processdefinition");
        Long id = con.executeQueryAsLong(sqlCurrent, "ID");
        System.out.println("ID ProcessDefinition:" + id);

        return id;
    }

    public static String GetForm(Long idProcessDefinitionWordpress) throws SQLException {
        String url = "<div id=\"formProcess\" ng-app=\"myApp\" ng-controller=\"HomeController\" data-process-id=\"" + idProcessDefinitionWordpress + "\"></div>";
        return url;
    }

    public static Long CreatePostWordpress(WorkspaceDto workspace, WordpressDto wordpress, ProcessDefinitionDto process, Long idProcessDefinitionWordpress) throws SQLException {
        WordpressConnection con = WordpressConnection.getWordpressConnection(wordpress);
        String sqlCurrent = "SELECT MAX(ID) as ID FROM " + wordpress.getTable("posts");
        Long id = con.executeQueryAsLong(sqlCurrent, "ID") + 1;
        System.out.println("id post:" + id);
        String sql = "INSERT INTO `wp_posts` (`post_author`, `post_date`, `post_date_gmt`, `post_content`, "
                + " `post_title`, `post_excerpt`, `post_status`, `comment_status`, `ping_status`, "
                + "`post_password`, `post_name`, `to_ping`, `pinged`, `post_modified`, "
                + "`post_modified_gmt`, `post_content_filtered`, `post_parent`, `guid`, `menu_order`, "
                + "`post_type`, `post_mime_type`, `comment_count`) "
                + "VALUES (?,?,?,?,"
                + " ?,'',?,?,?, "
                + " '',?,?,'',?, "
                + " ?,'',?,?,?,"
                + "?,'',?)";
        Date date = new Date();
        List<Object> listWordpress = new ArrayList<>();
        listWordpress.add(1);
        listWordpress.add(date);
        listWordpress.add(date);
        listWordpress.add(GetForm(idProcessDefinitionWordpress));

        listWordpress.add(process.getName());
//        listWordpress.add("''");
        listWordpress.add("publish");
        listWordpress.add("open");
        listWordpress.add("open");

//        listWordpress.add("''");
        listWordpress.add("process_" + idProcessDefinitionWordpress);
        listWordpress.add("publish");
//        listWordpress.add("''");
        listWordpress.add(date);

        listWordpress.add(date);
//        listWordpress.add("''");
        listWordpress.add(0);
        listWordpress.add(wordpress.getFullUrl() + "/?p=" + id);
        listWordpress.add(0);

        listWordpress.add("process");
//        listWordpress.add("''");
        listWordpress.add(0);

        con.executeUpdate(sql, listWordpress);

        return id;
    }

    public static Long CreateProcessWordpress(WorkspaceDto workspace, WordpressDto wordpress, ProcessDefinitionDto process, String hostWorkspace) throws SQLException {
        Long idProcessDefinitionWordpress = CreateProcessDefinitionWordpress(workspace, wordpress, process, hostWorkspace);
        Long idPost = CreatePostWordpress(workspace, wordpress, process, idProcessDefinitionWordpress);
        return idPost;
    }

    public static void CreateTaskWorkpress(WordpressDto wordpress, FormTaskDto formTask) throws SQLException {
        WordpressConnection con = new WordpressConnection(wordpress);
        String query = "INSERT INTO " + wordpress.getTable("tasks") + " (`idTask`, `nameTask`,`idProcess`,`post_content`) VALUES(?,?,?,?)";
        List<Object> list = new ArrayList<>();
        list.add(formTask.idTask);
        list.add(formTask.nameTask);
        list.add(formTask.idProcess);
        list.add(formTask.post_content);
        con.executeUpdate(query, list);

    }

    public static void DeleteProcess(WordpressDto wordpress) throws SQLException {
        WordpressConnection con = new WordpressConnection(wordpress);
        String query = "UPDATE " + wordpress.getTable("posts") + " SET post_status='trash' WHERE post_type='process'";
        System.out.println(query);
        con.executeUpdate(query);

    }

}
