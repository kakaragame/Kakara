package org.kakara.client.scenes;

import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.engine.GameHandler;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.items.ComponentCanvas;

/**
 * This is class solely exists to test the new component.
 * REMOVE BEFORE MERGING WITH MASTER.
 */
public class DebugScene extends AbstractMenuScene {
    public DebugScene(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics(GameHandler gameHandler) throws Exception {
        ComponentCanvas cc = new ComponentCanvas(this);
        Font roboto = new Font("Roboto", new ResourceManager().getResource("Roboto-Regular.ttf"), this);
        ChatComponent ncc = new ChatComponent(roboto, false, this);
        ncc.addConstraint(new HorizontalCenterConstraint());
        ncc.setPosition(0, 100);
        cc.add(ncc);
        add(cc);
    }

    @Override
    public void update(float v) {

    }
}
