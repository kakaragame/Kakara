package org.kakara.client.local.game;

import org.kakara.client.scenes.maingamescene.MainGameScene;

import org.kakara.core.common.gui.EngineController;
import org.kakara.core.common.gui.Inventory;
import org.kakara.core.common.gui.InventoryProperties;
import org.kakara.core.common.gui.menu.items.MenuElement;
import org.kakara.core.common.resources.Texture;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.UICanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameEngineInventoryController implements EngineController {
    private static final String TAG = "CONTROLLER_INVENTORY";

    @Override
    public void render(Inventory inventory, Texture inventoryBackground, Set<MenuElement> elementList, InventoryProperties properties) {
        Scene scene = GameHandler.getInstance().getSceneManager().getCurrentScene();
        if (!(scene instanceof MainGameScene)) return;
        InventoryCanvas inventoryCanvas = new InventoryCanvas(scene, inventoryBackground, elementList, inventory, scene.getUserInterface().getFont("Roboto"));
        ((MainGameScene) scene).add(inventoryCanvas);

        //TODO render Inventory
        scene.setCurserStatus(true);
    }

    @Override
    public void redraw(Inventory inventory, Texture inventoryBackground, Set<MenuElement> elementList, InventoryProperties properties) {
        Scene scene = GameHandler.getInstance().getSceneManager().getCurrentScene();
        if (!(scene instanceof MainGameScene)) return;
        scene.getUserInterface().getUICanvases().stream().filter(uiCanvas -> uiCanvas instanceof InventoryCanvas).findFirst().ifPresent(uiCanvas -> {
            ((InventoryCanvas) uiCanvas).renderItems();
        });
    }

    @Override
    public void close() {
        Scene scene = GameHandler.getInstance().getSceneManager().getCurrentScene();
        if (!(scene instanceof MainGameScene)) return;
        scene.setCurserStatus(false);

        //This will logically remove all UICanvas's by GameEngineInventoryController
        //DONT BE STUPID!
        List<UICanvas> canvas = new ArrayList<>();
        scene.getUserInterface().getUICanvases().stream().filter(uiCanvas -> uiCanvas instanceof InventoryCanvas).forEach(canvas::add);
        for (UICanvas canva : canvas) {
            scene.getUserInterface().removeItem(canva);
        }

    }
}
