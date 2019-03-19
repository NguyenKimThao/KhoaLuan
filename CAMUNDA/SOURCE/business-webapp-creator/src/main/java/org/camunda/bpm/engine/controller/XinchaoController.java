package org.camunda.bpm.engine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class XinchaoController {

    @RequestMapping(value = "/xinchao", method = RequestMethod.GET)
    public String authorize(@RequestBody String authData) {
        try {
            System.out.println("De test chuong trinh hello");
            return "TRUE";
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "FALSE";
    }

}
