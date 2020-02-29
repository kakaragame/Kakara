package org.kakara.client;

import org.apache.commons.cli.CommandLine;
import org.kakara.core.KakaraCore;
import org.kakara.core.KakaraCoreBuilder;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.engine.Game;
import org.kakara.engine.GameHandler;
import org.kakara.game.item.GameItemManager;
import org.kakara.game.mod.KakaraMod;
import org.kakara.game.resources.GameResourceManager;

import java.io.IOException;

public class KakaraGame implements Game {
    private KakaraCore kakaraCore;
    private GameHandler gameHandler;

    public KakaraGame(CommandLine parse) {
        KakaraCoreBuilder kakaraCoreBuilder = new KakaraCoreBuilder()
                .setResourceManager(new GameResourceManager())
                .setModManager(new GameModManager(new KakaraMod()))
                .setGameInstance(new Client())
                .setItemManager(new GameItemManager());

        kakaraCore = kakaraCoreBuilder.createKakaraCore();

        try {
            kakaraCore.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(GameHandler gameHandler) throws Exception {
        this.gameHandler = gameHandler;
    }

    @Override
    public void update() {

    }
}
