package com.catboyranch.roborancher.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static String cutFirstChar(String string) {
        return string.substring(1);
    }

    public static String[] asArray(String... objects) {
        return objects;
    }

    public static RoleType[] asArray(RoleType... objects) {
        return objects;
    }

    public static int getRandom(int min, int max) {
       return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean isLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch(Exception ignored) {
            return false;
        }
    }

    public static JSONArray getJSONArrayFromArray(ArrayList<?> list) {
        JSONArray array = new JSONArray();
        for (Object str : list)
            array.put(str);
        return array;
    }

    public static List<String> split(String sentence, char delim) {
        boolean inQuotes = false;
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for(char c : sentence.toCharArray()) {
            if(c == delim) {
                if(inQuotes) {
                    current.append(c);
                } else {
                    parts.add(current.toString());
                    current = new StringBuilder();
                }
            } else if (c == '\"') {
                inQuotes = !inQuotes;
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());
        return parts;
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch(Exception ignored) {
            return false;
        }
    }
}
