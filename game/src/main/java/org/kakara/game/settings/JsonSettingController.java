package org.kakara.game.settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.Utils;
import org.kakara.core.common.settings.Setting;
import org.kakara.core.common.settings.SettingControllerService;
import org.kakara.core.common.settings.SettingsValue;

import java.io.*;

public class JsonSettingController extends SettingControllerService {
    private ControllerKey controllerKey = Kakara.getEnvironmentInstance().createSystemKey("JSON_SETTING_CONTROLLER");

    private File getSettingFile(File folder, Setting setting) throws IOException {
        File jsonFile = new File(folder, String.format("%s.json", setting.getControllerKey().getController()));
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
        }
        return jsonFile;
    }

    @Override
    public void writeSetting(SettingsValue settingsValue, File file) {
        File settingFile = null;
        try {
            settingFile = getSettingFile(file, settingsValue.getSettingKey());
        } catch (IOException e) {
            Kakara.LOGGER.error("Unable to read File.", e);
            return;
        }
        JsonObject jsonObject;
        try {
            jsonObject = Utils.getGson().fromJson(new FileReader(settingFile), JsonObject.class);
        } catch (FileNotFoundException e) {
            Kakara.LOGGER.error("Unable to read File.", e);
            return;
        }
        jsonObject.remove(settingsValue.getSettingKey().getControllerKey().toString());
        jsonObject.addProperty(settingsValue.getSettingKey().getControllerKey().toString(), settingsValue.getValue());
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(Utils.getGson().toJson(jsonObject));
        } catch (IOException e) {
            Kakara.LOGGER.error("Unable to write File.", e);
        }
    }

    @Override
    public SettingsValue getSetting(Setting<?> setting, File file) {
        File settingFile = null;
        try {
            settingFile = getSettingFile(file, setting);
        } catch (IOException e) {
            Kakara.LOGGER.error("Unable to read File.", e);
            return null;
        }
        JsonObject jsonObject;
        try {
            jsonObject = Utils.getGson().fromJson(new FileReader(settingFile), JsonObject.class);
        } catch (FileNotFoundException e) {
            Kakara.LOGGER.error("Unable to read File.", e);
            return null;
        }
        String asString = jsonObject.get(setting.getControllerKey().toString()).getAsString();
        return new SettingsValue(setting, asString);
    }

    @Override
    public ControllerKey getImplementationControllerKey() {
        return controllerKey;
    }

    @Override
    public String getImplementationName() {
        return "Json Setting Controller";
    }

    @Override
    public int getImplementationId() {
        return controllerKey.hashCode();
    }
}
