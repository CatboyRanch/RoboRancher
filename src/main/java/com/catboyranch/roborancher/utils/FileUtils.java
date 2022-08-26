package com.catboyranch.roborancher.utils;

import com.catboyranch.roborancher.RoboRancher;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    private static String jarDirectory;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static String getJarDirectory() {
        if(jarDirectory == null) {
            try {
                URL location = RoboRancher.class.getProtectionDomain().getCodeSource().getLocation();
                String filename = new File(location.getPath()).getName();
                jarDirectory = new File(location.toURI()).getPath().replace(filename, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jarDirectory;
    }

    public static String loadFromJar(final String _path) {
        //Prepare path
        String path = _path;
        if(path.startsWith("."))
            path = Utils.cutFirstChar(_path);
        else if(!path.startsWith("/"))
            path = "/" + path;

        StringBuilder content = new StringBuilder();
        try {
            InputStream is = Utils.class.getResourceAsStream(path);
            if(is == null)
                throw new IOException("File not found! InputStream is null!");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null) {
                content.append(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static String loadAndVerify(String path, String file) {
        String folderPath = FileUtils.getJarDirectory() + path;
        String filePath = folderPath + file;

        String configString = FileUtils.loadString(filePath);
        if(configString == null) {
            configString = FileUtils.loadFromJar(path + file);
            createFolder(folderPath);
            FileUtils.saveString(filePath, configString);
        }
        return configString;
    }

    public static String loadString(String path) {
        try {
            return Files.readString(Paths.get(path), DEFAULT_CHARSET);
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveString(String configPath, String content) {
        try {
            Files.writeString(Path.of(configPath), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFolder(String path) {
        File f = new File(path);
        if(!f.exists() && !f.mkdirs())
            System.out.printf("Warning: Could not create folder %s\n!", path);
    }
}
