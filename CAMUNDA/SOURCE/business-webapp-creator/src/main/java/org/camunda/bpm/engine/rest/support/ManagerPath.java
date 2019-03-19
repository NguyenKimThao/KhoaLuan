/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.rest.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;

/**
 *
 * @author TramSunny
 */
public class ManagerPath {


    public static boolean CopyFile(String fileSoucre, String fileDest) {
        try {
            File source = new File(fileSoucre);
            File dest = new File(fileDest);
            Path res = Files.copy(source.toPath(), dest.toPath());
            if (res.equals(dest.toPath())) {
                return true;
            }
            return false;
        } catch (IOException ex) {
        }
        return false;
    }

    public static void CreateFile(String name, String data) throws IOException {
        FileOutputStream fos = new FileOutputStream(name);
        fos.write(data.getBytes(),0,data.length());
        fos.flush();
        fos.close();
    }
//    public static boolean CopyFileToCamunda(String fileSoucre) {
//        return CopyFile(EntityCamunda.PathWebSrcouse + fileSoucre, EntityCamunda.PathWebAppCamuna + fileSoucre);
//    }
}
