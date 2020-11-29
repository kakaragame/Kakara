package org.kakara.game.mod;


import org.kakara.core.common.GameInstance;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.mod.Mod;
import org.kakara.core.common.mod.ModRules;
import org.kakara.core.common.mod.ModType;
import org.slf4j.Logger;

public class KakaraMod implements Mod {
    private static KakaraMod instance;
    private GameInstance gameInstance;

    public KakaraMod(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        if (instance != null) throw new RuntimeException("Kakara has already been initialized");
        instance = this;
    }

    public static KakaraMod getInstance() {
        if (instance == null) throw new RuntimeException("Kakara is Not Ready");
        return instance;
    }

    @Override
    public String getName() {
        return "Kakara";
    }

    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public String[] getAuthors() {
        return new String[]{"Kakara Dev Team"};
    }

    @Override
    public String getDescription() {
        return "The Internal mod for Kakara";
    }

    @Override
    public ModType getModType() {
        return ModType.REGULAR;
    }

    @Override
    public ModRules getModRules() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return Kakara.LOGGER;
    }

    @Override
    public void preEnable() {

    }

    @Override
    public void postEnable() {

    }

    @Override
    public void enableCompletion() {

    }

    @Override
    public String getUppercaseName() {
        return "KAKARA";
    }

    @Override
    public GameInstance getGameInstance() {
        return gameInstance;
    }
}
