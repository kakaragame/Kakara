package org.kakara.game.mod;

import org.kakara.core.KakaraCore;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.ModType;

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
    public KakaraCore getKakaraCore() {
        return null;
    }
}
