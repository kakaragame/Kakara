package org.kakara.client.scenes.menu;

import org.kakara.client.KakaraGame;
import org.kakara.client.join.LocalJoin;
import org.kakara.client.local.game.SaveCreator;
import org.kakara.client.local.game.WorldCreator;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.modinstance.ModInstance;
import org.kakara.core.common.modinstance.ModInstanceType;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gameitems.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.GridConstraint;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.events.UIClickEvent;
import org.kakara.engine.ui.events.UIHoverEnterEvent;
import org.kakara.engine.ui.events.UIHoverLeaveEvent;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.font.TextAlign;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.utils.RGBA;
import org.kakara.engine.window.WindowIcon;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WorldSelectMenu extends AbstractMenuScene {
    private final KakaraGame kakaraGame;

    public WorldSelectMenu(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);
        this.kakaraGame = kakaraGame;
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics(GameHandler handler) throws Exception {
        gameHandler.getWindow().setIcon(new WindowIcon(gameHandler.getResourceManager().getResource("icon.png")));

        ResourceManager resourceManager = gameHandler.getResourceManager();
        setBackground(new Texture(resourceManager.getResource("kakara_background.png"), this));

        Font pixelFont = new Font("PixelFont", resourceManager.getResource("PressStart2P-Regular.ttf"), this);
        Font roboto = new Font("Roboto", resourceManager.getResource("Roboto-Regular.ttf"), this);


        ComponentCanvas componentCanvas = new ComponentCanvas(this);


    }


    @Override
    public void update(float i) {
    }
}
