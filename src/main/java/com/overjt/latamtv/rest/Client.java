package com.overjt.latamtv.rest;

import static com.overjt.latamtv.rest.Rsa.EncryptStr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.json.*;
public class Client {

    public Util util;

    private static String host = "https://woxitv.xyz";
    //private static String host = "http://vps.overjt.com:7878";
    private static String token_url = "/go/v/1.2/general/tk/";
    private static String channels_url = "/go/v/1.2/iptv/canales/";
    private static String channel_info_url = "/go/v/1.2/iptv/canal/";
    private static String firebase_uid = "qogLRU9DsaXMewbkonqS7PcRvM63";

    private String token = "";

    Client() {
        this.util = new Util();
    }

    public static String getID() {
        String str = "3510254478475263";
        return new UUID((long) str.hashCode(), (long) -905839116).toString();
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("pkg-name", EncryptStr("com.go.player"));
        hashMap.put("version", EncryptStr("1.2"));
        hashMap.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.1.1; Moto G Play Build/MPIS24.241-15.3-7");
        return hashMap;
    }

    public JSONObject makeRequest(String url, HashMap<String, String> body) {
        try {
            URL request_url = new URL(url);
            java.net.URLConnection con = request_url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            for (Map.Entry<String, String> entry : this.getHeaders().entrySet()) {
                http.setRequestProperty(URLEncoder.encode(entry.getKey(), "UTF-8"),
                        URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            http.setDoOutput(true);

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : body.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            // System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||");
            // System.out.println(sb.toString());
            // System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||");
            
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setToken() {
        try {
            Date time = Calendar.getInstance().getTime();
            HashMap<String, String> body = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder();
            sb.append("application/woxi");
            sb.append("?");
            sb.append(Client.firebase_uid);
            sb.append("?");
            sb.append(time);
            body.put("tk", Util.m16372c(this.util.mo14209e(sb.toString())));
            body.put("fecha_puntos", "0");
            body.put("uid", URLEncoder.encode(Rsa.EncryptStr(Rsa.m16415a(Client.getID())), "UTF-8"));
            JSONObject result = this.makeRequest(Client.host + Client.token_url, body);
            this.token = result.getJSONObject("OK").getString("tk");
        } catch (Exception e) {

        }
    }

    public HashMap<String, String> makeBody() {

        if (this.token == "") {
            this.setToken();
        }
        try {
            Date time = Calendar.getInstance().getTime();
            HashMap<String, String> body = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder();
            sb.append("application/woxi");
            sb.append("?");
            sb.append(this.token);
            sb.append("?");
            sb.append(time);
            body.put("tk", Util.m16372c(this.util.mo14209e(sb.toString())));
            body.put("uid", Util.m16372c(this.util.mo14209e(Client.firebase_uid)));
            return body;
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getChannels() {
        return this.makeRequest(Client.host + Client.channels_url, this.makeBody());
    }

    public JSONObject getChannel(String channel_id) {
        return this.makeRequest(Client.host + Client.channel_info_url + channel_id + "/", this.makeBody());
    }
}
