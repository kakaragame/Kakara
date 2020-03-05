package org.kakara.client;


import org.apache.commons.cli.*;
import org.kakara.core.KakaraCore;
import org.kakara.core.client.ClientSettings;
import org.kakara.core.client.ClientSettingsBuilder;
import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static org.kakara.client.CommandArguments.*;

public class Main {

    public static void main(String[] args) throws ParseException, IOException {
        File testFile = new File("test" + File.separator + "test.properties");
        ClientSettings clientSettings = null;
        if (testFile.exists()) {
            KakaraGame.LOGGER.info("In Test Mode");
            Properties properties = new Properties();
            properties.load(new FileInputStream(testFile));
            clientSettings = new ClientSettingsBuilder().
                    setAuthServer(properties.getProperty("server.auth", "")).
                    setModServers(MoreUtils.stringArrayToStringArray(properties.getProperty("mod.servers", "https://kakara.org/mods"))).
                    setTestMode(true).setAuthToken(properties.getProperty("auth.token", "")).createClientSettings();

        } else {

        }

        KakaraGame kakaraGame;
        try {
            kakaraGame = new KakaraGame(clientSettings);
        } catch (Exception e) {
            KakaraGame.LOGGER.error("Unable to load Kakara", e);
            return;
        }

        GameEngine gameEngine = new GameEngine("Kakara", 1080, 720, true, kakaraGame);
        gameEngine.run();
    }
}
