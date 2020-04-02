package org.kakara.game.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class GsonUtils {
    private static Gson gson = new Gson();

    public static JsonObject loadFile(File worldsJson) {
        try {
            return gson.fromJson(new FileReader(worldsJson), JsonObject.class);
        } catch (FileNotFoundException e) {
            return new JsonObject();
        }
    }
}
