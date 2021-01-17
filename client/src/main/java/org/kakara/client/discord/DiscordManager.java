package org.kakara.client.discord;

import org.kakara.client.KakaraGame;
import org.kakara.engine.scene.AbstractMenuScene;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DiscordManager extends Thread {
    private File fileDiscordFile;
    private boolean running = true;
    private KakaraGame kakaraGame;

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
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(fileDiscordFile);
            yamlConfiguration.set("current_task", getCurrentTask());
            try {
                yamlConfiguration.save(fileDiscordFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sleep(45000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        } else if (kakaraGame.getClient().isIntegratedServer()) {
            return "Playing in Single Player";
        } else {
            return "Having a good ole time";
        }
    }
}
