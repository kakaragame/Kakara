package org.kakara.server;

import org.kakara.core.GameInstance;
import org.kakara.core.GameType;
import org.kakara.core.game.GameSettings;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;

public class Server implements GameInstance {
    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    @Override
    public ItemStack createItemStack(Item item) {
        return null;
    }

    @Override
    public GameType getType() {
        return GameType.SERVER;
    }
}
