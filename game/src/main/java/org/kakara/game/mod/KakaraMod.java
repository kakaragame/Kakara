package org.kakara.game.mod;

import org.kakara.core.GameInstance;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.ModRules;
import org.kakara.core.mod.ModType;
import org.slf4j.Logger;

public class KakaraMod implements Mod {
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
        return null;
    }

    @Override
    public void preEnable() {

    }

    @Override
    public void postEnable() {

    }

    @Override
    public String getUppercaseName() {
        return "KAKARA";
    }

    @Override
    public GameInstance getGameInstance() {
        return null;
    }
}
