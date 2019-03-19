/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.dto.repository;

import java.util.Date;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;
import org.camunda.bpm.engine.config.ConfigDatabase;
import org.camunda.bpm.engine.config.ConfigInstallWordpress;
import org.camunda.bpm.engine.rest.support.UriCamunda;

/**
 *
 * @author TramSunny
 */
public class WordpressDeploymentDto {

    public String activate_plugins = "1";
    public String admin_email = "taonuaa004@gmail.com";
    public String admin_password = "12121993a";
    public String autosave_interval = "7200";
    public String blog_public = "1";
    public String dbhost = "localhost:3305";
    public String dbname = "wordpress-demo";
    public String default_content = "1";
    public String directory = "wordpress1548687418956";
    public String disallow_file_edit = "1";
    public String installDefault = "0";
    public String language = "vi";
    public String large_size_h = "0";
    public String large_size_w = "0";
    public String medium_size_h = "0";
    public String medium_size_w = "0";
    public String plugins_premium = "1";
    public String permalink_structure = "/%postname%/";
    public String plugins = "wp-website-monitoring; rocket-lazy-load";
    public String post_revisions = "0";
    public String prefix = "wp_";
    public String pwd = "";
    public String thumbnail_crop = "1";
    public String thumbnail_size_h = "0";
    public String thumbnail_size_w = "0";
    public String uname = "root";
    public String upload_dir = "";
    public String uploads_use_yearmonth_folders = "1";
    public String user_login = "taonuaa004";
    public String weblog_title = "demo";
    public String wordpressName = "wordpress1548687418956";
    public String workspaceID = "Workspace-1547540489869";
    public String workspaceName = "Workspace-1547540489869";
    public String wpcom_api_key = "";
    public String usernameWorkspace = "";
    public String wphost = "http://localhost:8888/";
    public String wpinstall = "wp-quick-install";

//    public WordpressHost getWordpressHostInstall() throws Exception {
//        return new WordpressHost(wphost, usernameWorkspace);
//    }
    public WordpressDto getWordpress() throws Exception {
        WordpressDto wordpress = new WordpressDto();
        wordpress.id = wordpressName + new Date().getTime();
        wordpress.name = wordpressName;
        wordpress.dbhost = dbhost;
        wordpress.dbname = dbname;
        wordpress.uname = uname;
        wordpress.pwd = pwd;
        wordpress.prefix = prefix;
        UriCamunda uri = UriCamunda.Create(wphost);

        wordpress.wphost = uri.getUrl();
        wordpress.directory = directory;

        return wordpress;
    }

    public void trim(WorkspaceDto workspace) throws Exception {
        workspaceID = workspace.getWorkspaceID();
        workspaceName = workspace.getWorkspaceName();
        directory = workspace.getUsername() + "/" + workspace.getWorkspaceName() + "/" + wordpressName;
        usernameWorkspace = workspace.getUsername();

        if (installDefault.equals("1")) {
            wphost = UriCamunda.Create(wphost).getUrl();
            String[] hostTemp = dbhost.split(":");
            if (hostTemp.length == 1) {
                dbhost = dbhost + ":3306";
            }
        } else {
            wphost = ((ConfigInstallWordpress) ConfigBusinessWebappCreator.getConfigWordpress("install")).getUrl();
            dbhost = ((ConfigDatabase) ConfigBusinessWebappCreator.getConfigWordpress("database")).getHostPort();
              ((ConfigDatabase) ConfigBusinessWebappCreator.getConfigWordpress("database")).databaseName=dbname;
            uname = ((ConfigDatabase) ConfigBusinessWebappCreator.getConfigWordpress("database")).username;
            pwd = ((ConfigDatabase) ConfigBusinessWebappCreator.getConfigWordpress("database")).password;
            wpinstall = ((ConfigInstallWordpress) ConfigBusinessWebappCreator.getConfigWordpress("install")).getInstall();
        }
        if (wpinstall.startsWith("/")) {
            wpinstall = wpinstall.substring(1);
        }
        if (!wpinstall.endsWith("/")) {
            wpinstall = wpinstall + "/";
        }
    }

//    public static class WordpressHost {
//
//        public String host;
//        public int port;
//        public String scheme;
//        public String folder;
//        public String username;
//
//        public WordpressHost(String uri, String username_) throws Exception {
//            String text = uri;
//            if (!text.endsWith("/")) {
//                text = text + "/";
//            }
//            scheme = "http";
//            host = "localhost";
//            folder = "wp-quick-install/";
//            username = username_;
//            final int schemeIdx = text.indexOf("://");
//            if (schemeIdx > 0) {
//                scheme = text.substring(0, schemeIdx);
//                text = text.substring(schemeIdx + 3);
//            }
//            int folderIdx = text.indexOf("/");
//            String url = text.substring(0, folderIdx);
//            text = text.substring(folderIdx + 1);
//            if (!text.isEmpty()) {
//                folder = text;
//            }
//            int portIdx = url.indexOf(":");
//            if (portIdx > 0) {
//                try {
//                    port = Integer.parseInt(url.substring(portIdx + 1));
//                } catch (final NumberFormatException ex) {
//                    throw new Exception("Invalid HTTP host: " + uri);
//                }
//                host = url.substring(0, portIdx);
//            } else {
//                if (scheme.equals("http")) {
//                    port = 80;
//                } else if (scheme.equals("http")) {
//                    port = 443;
//                } else {
//                    port = -1;
//                }
//            }
//        }
//
//        public String GetHost() {
//            return scheme + "://" + host + ":" + port;
//        }
//
//        public String GetFullUrl() {
//            return scheme + "://" + host + ":" + port + "/" + username + "/" + folder;
//
//        }
//
//        public String GetPath() {
//            return "/" + username + "/" + folder;
//        }
//    }
}
