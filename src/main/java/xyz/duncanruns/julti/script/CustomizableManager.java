package xyz.duncanruns.julti.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.util.ExceptionUtil;
import xyz.duncanruns.julti.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomizableManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject json = new JsonObject();
    private static final Path STORAGE_PATH = ScriptManager.SCRIPTS_FOLDER.resolve("customizable.storage");

    public synchronized static void load() {
        if (Files.exists(STORAGE_PATH)) {
            String s;
            try {
                s = FileUtil.readString(STORAGE_PATH);
                json = GSON.fromJson(s, JsonObject.class);
            } catch (IOException e) {
                Julti.log(Level.ERROR, "Failed to load customizable storage: " + ExceptionUtil.toDetailedString(e));
            }
        }
    }

    public synchronized static void save() {
        try {
            FileUtil.writeString(STORAGE_PATH, GSON.toJson(json));
        } catch (IOException e) {
            Julti.log(Level.ERROR, "Failed to save customizable storage: " + ExceptionUtil.toDetailedString(e));
        }
    }

    public synchronized static void set(String scriptName, String key, Object val) {
        if (!json.has(scriptName)) {
            json.add(scriptName, new JsonObject());
        }
        JsonObject scriptSpace = json.getAsJsonObject(scriptName);
        scriptSpace.addProperty(key, val.toString());
        save();
    }

    public synchronized static String get(String scriptName, String key) {
        if (!json.has(scriptName)) {
            return null;
        }
        JsonObject scriptSpace = json.getAsJsonObject(scriptName);
        if (!scriptSpace.has(key)) {
            return null;
        }
        return scriptSpace.get(key).getAsString();
    }
}
