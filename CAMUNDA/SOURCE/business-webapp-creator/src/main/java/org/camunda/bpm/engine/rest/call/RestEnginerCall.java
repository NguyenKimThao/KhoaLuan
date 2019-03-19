package org.camunda.bpm.engine.rest.call;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;

import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.camunda.bpm.engine.rest.RestEnginerCurrentService.AuthUser;
import org.camunda.bpm.engine.rest.dto.repository.WordpressDeploymentDto;
import org.camunda.bpm.engine.rest.support.UriCamunda;
import spinjar.com.minidev.json.JSONObject;
import spinjar.com.minidev.json.parser.JSONParser;

public class RestEnginerCall {

    public String host = "localhost";
    public int port = 8088;
    public String scheme = "http";
    public HttpHost httpHost;
    public UriCamunda uri;

    public RestEnginerCall(String uriCamunda) {
        this.uri = UriCamunda.Create(uriCamunda);
        System.out.println(this.uri.getHost() + " : " + this.uri.getPort() + " : " + this.uri.getScheme());
        httpHost = new HttpHost(this.uri.getHost(), this.uri.getPort(), this.uri.getScheme());

    }

    public RestEnginerCall(UriCamunda uri) {
        this.uri = uri;
        httpHost = new HttpHost(this.uri.getHost(), this.uri.getPort(), this.uri.getScheme());
    }

    public String executeGet(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            // specify the get request
            HttpResponse httpResponse = httpclient.execute(httpHost, getHttpGet(url));
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(httpResponse);
            return body;
        } catch (Exception e) {
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    public String execute(String url, String method) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {

            HttpResponse httpResponse = httpclient.execute(httpHost, getHttpRequest(method, url));
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(httpResponse);
            return body;
        } catch (Exception e) {
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    public String executeWithPostData(String url, String type, String data) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
//     
            HttpPost httppost = getHttpPost(url);

            StringEntity params = new StringEntity(data);
            params.setContentType(type);
            httppost.setEntity(params);
            httppost.setHeader(HTTP.CONTENT_TYPE, type);
            HttpResponse httpResponse = httpclient.execute(httpHost, httppost);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(httpResponse);
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            return body;
        } catch (Exception e) {
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    private HttpPost getHttpPost(String url) {

        if (!url.startsWith("/") && this.uri != null) {
            url = this.uri.getPath() + url;
        }
        System.out.println("httpPort: " + url);
        HttpPost getRequest = new HttpPost(url);
        if (this.uri != null && this.uri.getAuthor() != null && !this.uri.getAuthor().equals("")) {
            getRequest.setHeader("Authorization", this.uri.getAuthor());
        }
        return getRequest;
    }

    private HttpGet getHttpGet(String url) {
        if (!url.startsWith("/") && this.uri != null) {
            url = this.uri.getPath() + url;
        }
        HttpGet getRequest = new HttpGet(url);
        if (this.uri != null && this.uri.getAuthor() != null && !this.uri.getAuthor().equals("")) {
            getRequest.setHeader("Authorization", this.uri.getAuthor());
        }
        return getRequest;
    }

    private BasicHttpRequest getHttpRequest(String method, String url) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (this.uri != null) {
            url = this.uri.getPath() + url;
        }
        BasicHttpRequest getRequest = new BasicHttpRequest(method, url);
        if (this.uri != null && this.uri.getAuthor() != null && !this.uri.getAuthor().equals("")) {
            getRequest.setHeader("Authorization", this.uri.getAuthor());
        }
        return getRequest;
    }

    public String executeWithPostData(String url, String data) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = getHttpPost(url);

            httppost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            System.out.println(url);

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(data);
            Iterator<String> keys = object.keySet().iterator();

            while (keys.hasNext()) {
                String key = keys.next();
                postParameters.add(new BasicNameValuePair(key, object.getAsString(key)));
                System.out.println("key: " + key + " values: " + object.getAsString(key));
            }
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            HttpResponse httpResponse = httpclient.execute(httpHost, httppost);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(httpResponse);
            System.out.println(body);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            return body;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

}
