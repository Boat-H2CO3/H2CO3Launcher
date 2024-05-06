/*
 * //
 * // Created by cainiaohh on 2024-04-04.
 * //
 */

package org.koishi.launcher.h2co3.core.download;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MCAssetsDownloader {

    public static void main(String[] args) {
        String version = "1.16.5"; // Minecraft 版本
        String os = "linux"; // 操作系统
        String architecture = "x64"; // 架构
        String librariesPath = "/path/to/libraries/"; // libraries 文件保存路径

        try {
            URL url = new URL("https://libraries.minecraft.net/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder librariesList = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    librariesList.append(line);
                }

                bufferedReader.close();
                inputStream.close();

                String libraries = getStringBetween(librariesList.toString(), "<libraries>", "</libraries>");
                if (libraries != null) {
                    String[] libraryArray = libraries.split("<library>");
                    for (String library : libraryArray) {
                        String path = getStringBetween(library, "<path>", "</path>");
                        String name = getStringBetween(library, "<name>", "</name>");
                        if (path != null && name != null) {
                            String libraryUrl = "https://libraries.minecraft.net/" + path;
                            downloadLibrary(libraryUrl, librariesPath + name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadLibrary(String urlString, String filePath) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        }
    }

    public static String getStringBetween(String str, String start, String end) {
        int startIndex = str.indexOf(start);
        if (startIndex == -1) {
            return null;
        }
        int endIndex = str.indexOf(end, startIndex + start.length());
        if (endIndex == -1) {
            return null;
        }
        return str.substring(startIndex + start.length(), endIndex);
    }
}
