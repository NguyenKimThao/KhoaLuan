/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.rest;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import org.camunda.bpm.engine.rest.call.RestEnginerCall;
import java.util.HashMap;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.ServletContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.camunda.bpm.engine.config.ConfigBusinessWebappCreator;
import org.camunda.bpm.engine.config.ConfigEngine;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDto;
import org.camunda.bpm.engine.rest.support.UriCamunda;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Daniel Meyer
 *
 */
public class RestEnginerCurrentService {

    public static class AuthUser {

        public String username;
        public String password;
        public String auth;
    }

    public static HashMap<String, RestEnginerCall> mapAuth = new HashMap<>();

    public static HashMap<String, RestEnginerCall> mapWordpressCall = new HashMap<String, RestEnginerCall>();

    public static void setRestEngine(AuthUser user, RestEnginerCall rest) {
        mapAuth.put(user.username, rest);
    }

    public static void updateRestEngine(AuthUser user) {
        if (mapAuth.containsKey(user.username)) {
            RestEnginerCall res = mapAuth.get(user.username);
            res.uri.setAuthor(user.auth);
        }
    }

    public static RestEnginerCall getRestEngineCurrent(String name, String author) {
        if (mapAuth.containsKey(name)) {
            return mapAuth.get(name);
        }
        UriCamunda uri = ((ConfigEngine) ConfigBusinessWebappCreator.getConfigWebapp("engine")).getUriCamunda();
        uri.setAuthor(author);
        RestEnginerCall rest = new RestEnginerCall(uri);
        mapAuth.put(name, rest);
        return rest;
    }

    public static RestEnginerCall getRestEngineCurrent() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return mapAuth.get(name);
    }

    public static RestEnginerCall getRestEngineWordpress(WordpressDto wordpressdto) {
        return mapAuth.get(wordpressdto.id);
    }

    public static RestEnginerCall getRestEngineWordpress(String wordpressID) {
        return mapAuth.get(wordpressID);
    }

    public static String getAuthorization() {
        RestEnginerCall call = getRestEngineCurrent();
        if (call == null) {
            return "";
        }
        return call.uri.getAuthor();
    }

    public static String GetHostCurrent() throws MalformedObjectNameException {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames;
        String context = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext().getContextPath();
        objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        String host = InetAddress.getLoopbackAddress().getHostAddress();
        int port = Integer.parseInt(objectNames.iterator().next().getKeyProperty("port"));
        String scheme = objectNames.iterator().next().getKeyProperty("scheme");
        System.out.println(host + " " + port + " " + scheme + " " + context);
        return "http" + "://" + host + ":" + port + "/business-webapp-creator";
    }
}
