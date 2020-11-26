package org.kakara.client;


import org.kakara.core.client.ClientSettings;
import org.kakara.core.client.ClientSettingsBuilder;
import org.kakara.engine.GameEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException {

        File testFile = new File("test" + File.separator + "test.properties");
        ClientSettings clientSettings = null;
        if (testFile.exists()) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
            //Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
            KakaraGame.LOGGER.info("In Test Mode");
            Properties properties = new Properties();
            properties.load(new FileInputStream(testFile));
            clientSettings = new ClientSettingsBuilder().
                    setAuthServer(properties.getProperty("server.auth", "")).
                    setModServers(MoreUtils.stringArrayToStringArray(properties.getProperty("mod.servers", "https://kakara.org/mods"))).
                    setTestMode(true).setAuthToken(properties.getProperty("auth.token", "")).createClientSettings();
        } else {
            KakaraGame.LOGGER.error("Regular Mode is currently unsupported.");
            return;
        }


        KakaraGame kakaraGame;
        try {
            kakaraGame = new KakaraGame(clientSettings);
        } catch (Exception e) {
            KakaraGame.LOGGER.error("Unable to load Kakara", e);
            return;
        }

        // TODO add in a ability to select vSync mode.
        GameEngine gameEngine = new GameEngine("Kakara", 1080, 720, false, kakaraGame);
        gameEngine.run();

    }


}
