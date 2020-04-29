package org.kakara.client.scenes;

import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.engine.GameHandler;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractMenuScene;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.properties.HorizontalCenterProperty;
import org.kakara.engine.ui.properties.VerticalCenterProperty;
import org.kakara.engine.ui.text.Font;

public class TestUIScene extends AbstractMenuScene {

    public TestUIScene(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics(GameHandler gameHandler) throws Exception {
        ResourceManager resourceManager = gameHandler.getResourceManager();
        Font roboto = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"), this);
        ComponentCanvas main = new ComponentCanvas(this);
        ChatComponent cc = new ChatComponent(roboto, false, this);
        cc.addProperty(new HorizontalCenterProperty());
        cc.addProperty(new VerticalCenterProperty());
        cc.addUActionEvent(new ChatSendEvent() {
            @Override
            public void onChatSend(String message) {
                if(message.startsWith("/"))
                    cc.addMessage("[CMD] Command triggered");
            }
        }, ChatSendEvent.class);
        main.add(cc);
        add(main);
    }

    @Override
    public void update(float v) {

    }
}
