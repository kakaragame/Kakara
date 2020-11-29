package org.kakara.client.game.player;

import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.gui.EngineInventoryRenderer;
import org.kakara.core.common.gui.InventoryRenderer;
import org.kakara.core.common.gui.annotations.BuilderClass;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.game.server.gui.bnbi.BasicNineBoxedInventory;

@BuilderClass(PlayerContentInventoryBuilder.class)
public class PlayerContentInventory extends BasicNineBoxedInventory {
    private final int hotbarSize = 5;
    private EngineInventoryRenderer engineInventoryRenderer;

    public PlayerContentInventory() {
        super(36);
    }

    public ItemStack[] getHotBarContents() {
        ItemStack[] itemStacks = new ItemStack[hotbarSize];
        for (int i = 0; i < hotbarSize; i++) {
            itemStacks[i] = getContents()[i];
        }
        return itemStacks;
    }

    public void addItemStackForPickup(ItemStack itemStack) {
        addItemStack(itemStack);
    }

    @Override
    public void redraw() {
        Scene scene = GameHandler.getInstance().getCurrentScene();
        if (!(scene instanceof MainGameScene)) {
            throw new IllegalStateException("Must be inside a MainGameScene");
        }
        MainGameScene gameScene = (MainGameScene) scene;
        gameScene.getHotBar().renderItems();
//TODO implement this
        //engineInventoryRenderer.redraw(this);
    }

    @Override
    public InventoryRenderer getRenderer() {
        return engineInventoryRenderer;
    }
}
