package com.landis.breakdowncore.system.automatic.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final String updateUrl;
    private final int version;

    public UpdateChecker(String updateUrl,int version){
        this.updateUrl = updateUrl;
        this.version = version;
    }

    public boolean isUpdateAvailable(){
        try {
            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = reader.readLine())!= null){
                    response.append(line);
                }
                reader.close();
                JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();
                int latestVersion = json.get("version").getAsInt();
                if(latestVersion > version){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}