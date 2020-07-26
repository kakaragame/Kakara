package org.kakara.client.game;

import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.EngineController;
import org.kakara.core.gui.menu.items.MenuElement;
import org.kakara.core.resources.Texture;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;

import java.util.List;

public class GameEngineInventoryController implements EngineController {


    @Override
    public void render(ItemStack[] itemStacks, Texture inventoryBackground, List<MenuElement> elementList) {
        Scene scene = GameHandler.getInstance().getSceneManager().getCurrentScene();
        if (!(scene instanceof MainGameScene)) return;
        //TODO render Inventory
        scene.setCurserStatus(true);
    }
}
