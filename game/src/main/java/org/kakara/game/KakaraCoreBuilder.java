package org.kakara.game;

import org.kakara.core.GameInstance;
import org.kakara.core.GameType;
import org.kakara.core.KakaraCore;
import org.kakara.core.mod.game.GameModManager;

public class KakaraCoreBuilder {
    public static KakaraCore build(GameInstance gameInstance, GameType type) {
        return new KakaraCore(new GameModManager()gameInstance, type)
    }
}
