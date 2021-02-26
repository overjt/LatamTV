package com.overjt.latamtv.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ProxyClient {

    ProxyClient() {

    }

    public InputStream streamWebContents(String url, HashMap<String, String> headers) {
        try {
            URL request_url = new URL(url);
            URLConnection con = request_url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(URLEncoder.encode(entry.getKey(), "UTF-8"),entry.getValue());
            }
            return http.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getWebContents(String url, HashMap<String, String> headers) {
        StringBuilder content = new StringBuilder();
        try {
            URL request_url = new URL(url);
            URLConnection con = request_url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(URLEncoder.encode(entry.getKey(), "UTF-8"),entry.getValue());
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
