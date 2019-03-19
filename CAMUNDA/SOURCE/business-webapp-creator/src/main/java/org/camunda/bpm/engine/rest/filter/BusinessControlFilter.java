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
package org.camunda.bpm.engine.rest.filter;

import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService;
import org.camunda.bpm.engine.rest.dto.entity.ActionResponeError;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <p>
 * Cache control filter setting "Cache-Control: no-cache" on all GET requests.
 *
 * @author Daniel Meyer
 *
 */
public class BusinessControlFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;
        System.out.println(request.getRequestURI());
        if (request.getRequestURI().contains("authorize")) {
        } else if (request.getRequestURI().contains("/api/")) {
            RestEnginerCurrentService.AuthUser user = new RestEnginerCurrentService.AuthUser();
            user.username = "demo";
            user.password = "demo";
            user.auth = "Basic ZGVtbzpkZW1v";
            request.getSession().setAttribute("AuthUser", user);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.username, user.password);
            SecurityContextHolder.getContext().setAuthentication(token);
        } else if (request.getRequestURI().contains("/engine/default/")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            RestEnginerCurrentService.AuthUser authUser = (RestEnginerCurrentService.AuthUser) request.getSession().getAttribute("AuthUser");
            if (authUser == null || authUser.username.equals("")) {
                System.out.println("Khong vao duoc");
            } else {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authUser.username, authUser.password);
                SecurityContextHolder.getContext().setAuthentication(token);
            }

        }
        if ("GET".equals(request.getMethod()) && !request.getRequestURI().endsWith("xml")) {
            response.setHeader("Cache-Control", "no-cache");
        }

        try {
            chain.doFilter(req, resp);
        } catch (Exception ex) {
            sendLogin(response, ex.getMessage());
        }
    }

    public void destroy() {

    }

    public void sendLogin(HttpServletResponse response, String mess) throws IOException {
        Gson gson = new Gson();
        ActionResponeError eror = new ActionResponeError("", mess);
        response.getWriter().write(gson.toJson(eror));
    }
}
