package org.camunda.bpm.engine.rest.call;

import org.camunda.bpm.engine.rest.RestEnginerCurrentService;

/**
 *
 * @author TramSunny
 */
public class CamundaEngineRestCall {

    public static String apiGet(String url) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        if (res == null) {
            return null;
        }
        String body = res.executeGet(url);
        return body;
    }

    public static String apiPost(String url, String contenttype, String data) {
        RestEnginerCall res = RestEnginerCurrentService.getRestEngineCurrent();
        if (res == null) {
            return null;
        }
        System.out.println("co API pist: url: " + url + " cont:" + contenttype + " data: " + data);
        String body = res.executeWithPostData(url, contenttype, data);
        return body;
    }
}
