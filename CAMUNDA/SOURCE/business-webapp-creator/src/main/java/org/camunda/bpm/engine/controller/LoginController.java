/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.camunda.bpm.engine.controller;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;
import org.camunda.bpm.engine.config.ConfigEngine;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.WorkspaceRestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.camunda.bpm.engine.rest.call.RestEnginerCall;
import org.camunda.bpm.engine.rest.support.UriCamunda;

/**
 *
 * @author TramSunny
 */
@Controller
@ResponseBody
public class LoginController {

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public String authorize(HttpServletRequest request, @RequestBody String json) {
        try {

            Gson gson = new Gson();
            RestEnginerCurrentService.AuthUser user = gson.fromJson(json, RestEnginerCurrentService.AuthUser.class);
            UriCamunda uri = ((ConfigEngine) ConfigBusinessWebappCreator.getConfigWebapp("engine")).getUriCamunda();
            uri.setAuthor(user.auth);
            RestEnginerCall rest = new RestEnginerCall(uri);
            String body = rest.executeGet("process-definition");
            if (body != null) {
                RestEnginerCurrentService.setRestEngine(user, rest);
                request.getSession().setAttribute("AuthUser", user);
                WorkspaceRestService.UpdateAuthor(user);
                return "{\"success\":\"true\"}";
            }
            return "{\"success\":\"false\"}";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "{\"success\":\"false\"}";
    }
}
