package org.kakara.client.discord;

import org.kakara.client.KakaraGame;
import org.kakara.engine.scene.AbstractMenuScene;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DiscordManager extends Thread {
    private File fileDiscordFile;
    private volatile boolean running = true;
    private final KakaraGame kakaraGame;

    private String currentTask = "";

    public DiscordManager(KakaraGame kakaraGame) {
        this.kakaraGame = kakaraGame;
    }

    @Override
    public synchronized void start() {
        File workingDirectory = kakaraGame.getWorkingDirectory();
        fileDiscordFile = new File(workingDirectory, "discord.yml");
        try {
            fileDiscordFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileDiscordFile.deleteOnExit();
        super.start();
    }

    @Override
    public void run() {
        //Probably do a better job
        while (running) {
            if (!checkTag()) {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(fileDiscordFile);
                yamlConfiguration.set("current_task", getCurrentTask());
                currentTask = getCurrentTask();
                try {
                    yamlConfiguration.save(fileDiscordFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    //TODO Improve with langs and more details.

    private String getCurrentTask() {
        if (kakaraGame.getGameHandler().getSceneManager().getCurrentScene() instanceof AbstractMenuScene) {
            return "In the Menus";
        } else if (kakaraGame.getClient() != null && kakaraGame.getClient().isIntegratedServer()) {
            // TODO NPE occurs here sometimes.
            return "Playing in Single Player";
        } else {
            return "Having a good ole time";
        }
    }

    private boolean checkTag() {
        return getCurrentTask().equals(currentTask);
    }
}
