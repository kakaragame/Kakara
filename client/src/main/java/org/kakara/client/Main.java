package org.kakara.client;


import org.apache.commons.cli.*;
import org.kakara.core.client.client.ClientSettings;
import org.kakara.core.client.client.ClientSettingsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("E","engine", true,"Engine Jar");
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            String engine_jar = cmd.getOptionValue("E");
            Path of = Path.of(engine_jar);
            Agent.addToClassPath(of);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        File testFile = new File("test" + File.separator + "test.yml");
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


        GameLoader.load(clientSettings);

    }

    private static void loadNeededLibraries() {

    }


}
