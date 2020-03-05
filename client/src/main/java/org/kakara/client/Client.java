package org.kakara.client;

import org.kakara.core.GameInstance;
import org.kakara.core.GameType;
import org.kakara.core.game.CustomStackable;
import org.kakara.core.game.GameSettings;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;

public class Client implements GameInstance {
    private GameSettings settings;
    private KakaraGame kakaraGame;

    public Client(KakaraGame kakaraGame, GameSettings gameSettings) {
        this.settings = gameSettings;
        this.kakaraGame = kakaraGame;
    }

    @Override
    public GameSettings getGameSettings() {
        return settings;
    }

    @Override
    public ItemStack createItemStack(Item item) {
        if (item instanceof CustomStackable) {
            return ((CustomStackable) item).createItemStack();
        }
        ItemStack stack = null;
        //TODO create GameItemStack
        return stack;
    }

    @Override
    public GameType getType() {
        return GameType.CLIENT;
    }


}
