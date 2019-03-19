/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.call;

import com.google.gson.Gson;
import org.camunda.bpm.engine.rest.dto.entity.ActionRespone;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeSucess;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDeploymentDto;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;

/**
 *
 * @author TramSunny
 */
public class WordpressInstallRestCall extends RestEnginerCall {

    public WordpressDeploymentDto wordpressDeployment;
    private String wpFolderInstall;

    public WordpressInstallRestCall(WordpressDeploymentDto wordpressDeployment, String wpinstall) {
        super(wordpressDeployment.wphost);
        this.wordpressDeployment = wordpressDeployment;
        this.wpFolderInstall = wpinstall;
    }

//    public ActionRespone deployWordpress(WordpressDto wordpressDto, WordpressDeploymentDto wordpressDeployment_) {
//        Gson gson = new Gson();
//        String data = gson.toJson(wordpressDeployment_);
//        executeWithPostData("index.php?action=check_before_upload", data);
//        executeWithPostData("index.php?action=download_wp", data);
//        executeWithPostData("index.php?action=unzip_wp", data);
//        executeWithPostData("index.php?action=wp_config", data);
//        executeWithPostData("index.php?action=install_wp", data);
//        executeWithPostData("index.php?action=install_theme", data);
//        executeWithPostData("index.php?action=install_plugins", data);
//
//        return new ActionResponeSucess("deployWordpress", "deployment sucess");
//
//    }
    public ActionRespone deployWordpress() throws Exception {
        Gson gson = new Gson();
        String data = gson.toJson(wordpressDeployment);
        System.out.println(data);
        if (executeWithPostData(wpFolderInstall + "index.php?action=check_before_upload", data) == null) {
            throw new Exception("check_before_upload error");
        }
        if (executeWithPostData(wpFolderInstall + "index.php?action=download_wp", data) == null) {
            throw new Exception("download_wp error");
        }
        if (executeWithPostData(wpFolderInstall + "index.php?action=unzip_wp", data) == null) {
            throw new Exception("unzip_wp error");
        }
        if (executeWithPostData(wpFolderInstall + "index.php?action=wp_config", data) == null) {
            throw new Exception("wp_config error");
        }
        if (executeWithPostData(wpFolderInstall + "index.php/wp-admin/install.php?action=install_wp", data) == null) {
        }
        if (executeWithPostData(wpFolderInstall + "index.php/wp-admin/install.php?action=install_theme", data) == null) {
            throw new Exception("install_theme error");
        }
        if (executeWithPostData(wpFolderInstall + "index.php?action=install_plugins", data) == null) {
            throw new Exception("install_plugins error");
        }
        if (executeWithPostData(wpFolderInstall + "index.php?action=success", data) == null) {
            throw new Exception("success error");
        }
        return new ActionResponeSucess("deployWordpress", "deployment sucess");

    }

}
