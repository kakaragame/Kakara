package org.kakara.client.game;

import org.kakara.client.MoreUtils;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.EngineController;
import org.kakara.core.gui.menu.items.MenuElement;
import org.kakara.core.resources.Texture;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.UICanvas;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.items.ObjectCanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameEngineInventoryController implements EngineController {
    private static final String TAG = "CONTROLLER_INVENTORY";

    @Override
    public void render(ItemStack[] itemStacks, Texture inventoryBackground, List<MenuElement> elementList) {
        Scene scene = GameHandler.getInstance().getSceneManager().getCurrentScene();
        if (!(scene instanceof MainGameScene)) return;
        ComponentCanvas componentCanvas = new ComponentCanvas(scene);
        componentCanvas.setTag(TAG);
        Sprite sprite = new Sprite(MoreUtils.coreTextureToEngineTexture(inventoryBackground));
        //TODO position it
        componentCanvas.add(sprite);
        scene.getUserInterface().addItem(componentCanvas);
        ObjectCanvas canvas = new ObjectCanvas(scene);
        componentCanvas.setTag(TAG);

        //TODO render Inventory
        scene.setCurserStatus(true);
    }

    @Override
    public void close() {
        Scene scene = GameHandler.getInstance().getSceneManager().getCurrentScene();
        if (!(scene instanceof MainGameScene)) return;
        scene.setCurserStatus(false);

        //This will logically remove all UICanvas's by GameEngineInventoryController
        //DONT BE STUPID!
        List<UICanvas> canvas = new ArrayList<>();
        scene.getUserInterface().getUICanvases().stream().filter(uiCanvas -> uiCanvas.getTag().equals(TAG)).forEach(uiCanvas -> canvas.add(uiCanvas));
        for (UICanvas canva : canvas) {
            scene.getUserInterface().removeItem(canva);
        }

    }
}
