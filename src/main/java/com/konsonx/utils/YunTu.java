package com.konsonx.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.NavLocation;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component(value = "YunTu")
public class YunTu {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String key = "d81b5798220a8699daa13dad2a06e53e";
    private String tableId = "45c518cb-e9a3-4ccf-8311-14d7800cc4ac";

    public static String doPost(String url, Map<String, String> params) {
        URL u = null;
        HttpURLConnection con = null;
        StringBuffer sb = new StringBuffer();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        System.out.println("send_url:" + url);
        System.out.println("send_data:" + sb.toString());
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            osw.write(sb.toString());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            System.err.println("YunTu doPost连接异常: " + e.getClass().getName() + ": " + e.getMessage());
            return "{\"status\":\"0\",\"info\":\"YunTu doPost连接异常: " + e.getClass().getName() + ": " + e.getMessage()
                    + "\"}";
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
            br.close();
        } catch (Exception e) {
            System.err.println("YunTu doPost读取响应异常: " + e.getClass().getName() + ": " + e.getMessage());
            return "{\"status\":\"0\",\"info\":\"YunTu doPost读取响应异常: " + e.getClass().getName() + ": " + e.getMessage()
                    + "\"}";
        }

        return buffer.toString();
    }

    public ObjectNode insertByLocation(NavLocation location) {
        String url = "http://yuntuapi.amap.com/datamanage/data/create";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("tableid", tableId);
        params.put("loctype", "1");
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("_name", location.getName());
        jsonObject.put("_location", location.getLongitude() + "," + location.getLatitude());
        jsonObject.put("available", location.getAvailable());
        if (location.getAddress() != null && !"".equals(location.getAddress()) && location.getAddress().length() > 2) {
            jsonObject.put("_address", location.getAddress());
        }
        params.put("data", jsonObject.toString());
        System.out.println(params);
        ObjectNode resultObject = null;
        try {
            resultObject = objectMapper.readValue(doPost(url, params), ObjectNode.class);
        } catch (Exception e) {
            System.err.println("YunTu insertByLocation解析异常: " + e.getClass().getName() + ": " + e.getMessage());
            resultObject = objectMapper.createObjectNode();
            resultObject.put("status", "0");
            resultObject.put("info", "解析异常: " + e.getMessage());
        }
        return resultObject;
    }

    public ObjectNode insertByAddress(NavLocation location) {
        String url = "http://yuntuapi.amap.com/datamanage/data/create";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("tableid", tableId);
        params.put("loctype", "2");
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("_name", location.getName());
        jsonObject.put("_address", location.getAddress());
        jsonObject.put("available", location.getAvailable());
        params.put("data", jsonObject.toString());
        String response = doPost(url, params);
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("云图API返回为空，请求失败");
        }
        ObjectNode resultObject = null;
        try {
            resultObject = objectMapper.readValue(response, ObjectNode.class);
        } catch (Exception e) {
            throw new RuntimeException("云图API返回解析失败，原始内容: " + response, e);
        }
        return resultObject;
    }

    public ObjectNode update(NavLocation location) {
        String url = "http://yuntuapi.amap.com/datamanage/data/update";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("tableid", tableId);
        params.put("loctype", "1");
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("_id", location.getId());
        if (location.getAddress() != null && !"".equals(location.getAddress()) && location.getAddress().length() > 2) {
            jsonObject.put("_address", location.getAddress());
        }
        if (location.getAvailable() != null && location.getAvailable() >= 0) {
            jsonObject.put("available", location.getAvailable());
        }
        if (location.getLongitude() != null && location.getLatitude() != null && !"".equals(location.getLongitude())
                && !"".equals(location.getLatitude()) && location.getLatitude().length() > 1
                && location.getLongitude().length() > 1) {
            jsonObject.put("_location", location.getLongitude() + "," + location.getLatitude());
        }
        if (location.getName() != null && !"".equals(location.getName()) && location.getName().length() > 2) {
            jsonObject.put("_name", location.getName());
        }
        params.put("data", jsonObject.toString());
        ObjectNode resultObject = null;
        try {
            resultObject = objectMapper.readValue(doPost(url, params), ObjectNode.class);
        } catch (Exception e) {
            System.err.println("YunTu update解析异常: " + e.getClass().getName() + ": " + e.getMessage());
            resultObject = objectMapper.createObjectNode();
            resultObject.put("status", "0");
            resultObject.put("info", "解析异常: " + e.getMessage());
        }
        return resultObject;
    }

    public ObjectNode delete(String[] ids) {
        String url = "http://yuntuapi.amap.com/datamanage/data/delete";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("tableid", tableId);
        if (ids.length == 1) {
            params.put("ids", ids[0]);
        } else if (ids.length > 1) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ids[0]);
            for (int i = 1; i < ids.length; i++) {
                stringBuffer.append(",");
                stringBuffer.append(ids[i]);
            }
            params.put("ids", stringBuffer.toString());
        }
        ObjectNode resultObject = null;
        try {
            resultObject = objectMapper.readValue(doPost(url, params), ObjectNode.class);
        } catch (Exception e) {
            System.err.println("YunTu delete解析异常: " + e.getClass().getName() + ": " + e.getMessage());
            resultObject = objectMapper.createObjectNode();
            resultObject.put("status", "0");
            resultObject.put("info", "解析异常: " + e.getMessage());
        }
        return resultObject;
    }

}