package org.kakara.engine.test;

import org.kakara.engine.GameHandler;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.Rectangle;
import org.kakara.engine.ui.components.Text;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.text.Font;
import org.kakara.engine.ui.text.TextAlign;

public class TitleScreenScene extends AbstractGameScene {
    public TitleScreenScene(GameHandler gameHandler) throws Exception {
        super(gameHandler);
        ResourceManager resourceManager = gameHandler.getResourceManager();

        Font roboto = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"));
        hud.addFont(roboto);

        ComponentCanvas cc = new ComponentCanvas();

        Text title = new Text("Kakara", roboto);
        title.setSize(200);
        title.setLineWidth(500);
        title.setPosition(gameHandler.getWindow().getWidth()/2 - 250, 200);

        Rectangle playButton = new Rectangle(new Vector2(gameHandler.getWindow().getWidth()/2 - 100, gameHandler.getWindow().getHeight() - 300),
                new Vector2(100, 100));
        playButton.setColor(new RGBA(0, 150, 150, 1));
        playButton.addUActionEvent(new HUDHoverEnterEvent() {
            @Override
            public void OnHudHoverEnter(Vector2 location) {
                playButton.setColor(new RGBA(0, 150, 200, 1));
            }
        }, HUDHoverEnterEvent.class);
        playButton.addUActionEvent(new HUDHoverLeaveEvent() {
            @Override
            public void OnHudHoverLeave(Vector2 location) {
                playButton.setColor(new RGBA(0, 150, 150, 1));
            }
        }, HUDHoverLeaveEvent.class);
        playButton.addUActionEvent(new HUDClickEvent() {
            @Override
            public void OnHUDClick(Vector2 location, MouseClickType clickType) {
                if(!playButton.isVisible()) return;
                try{
                    MainGameScene mgs = new MainGameScene(gameHandler);
                    gameHandler.getSceneManager().setScene(mgs);
                }catch(Exception ex) {
                    System.out.println("Could not switch to the main scene!");
                }
            }
        }, HUDClickEvent.class);
        Text txt = new Text("Play Game!", roboto);
        txt.setPosition(0, playButton.scale.y/2);
        txt.setTextAlign(TextAlign.CENTER);
        txt.setLineWidth(playButton.scale.x);
        playButton.add(txt);
        cc.add(title);
        cc.add(playButton);

        Rectangle popupMenu = new Rectangle(new Vector2(gameHandler.getWindow().getWidth()/2-150, 100), new Vector2(300, 500));
        popupMenu.setColor(new RGBA(181, 181, 181, 1));
        popupMenu.setVisible(false);
        Text popupTitle = new Text("Popup Menu!", roboto);
        popupTitle.setLineWidth(popupMenu.scale.x);
        popupTitle.setPosition(0, 50);
        popupTitle.setSize(40);
        popupTitle.setTextAlign(TextAlign.CENTER);
        popupMenu.add(popupTitle);

        Text popupText = new Text("This here is a very cool popup menu! A menu like this has a ton of uses! Maybe a pause menu?", roboto);
        popupText.setLineWidth(popupMenu.scale.x);
        popupText.setPosition(10, 150);
        popupText.setSize(25);
        popupText.setTextAlign(TextAlign.LEFT);
        popupMenu.add(popupText);

        Rectangle popupClose = new Rectangle(new Vector2(popupMenu.getScale().x/2-50, popupMenu.getScale().y - 100), new Vector2(100, 70));
        popupClose.setColor(new RGBA(0, 150, 150, 1));
        Text popupCloseTxt = new Text("Close Menu!", roboto);
        popupCloseTxt.setPosition(0, popupClose.scale.y/2-10);
        popupCloseTxt.setTextAlign(TextAlign.CENTER);
        popupCloseTxt.setSize(30);
        popupCloseTxt.setLineWidth(popupClose.scale.x);
        popupClose.add(popupCloseTxt);

        popupMenu.add(popupClose);

        popupClose.addUActionEvent(new HUDHoverEnterEvent() {
            @Override
            public void OnHudHoverEnter(Vector2 location) {
                popupClose.setColor(new RGBA(0, 150, 200, 1));
            }
        }, HUDHoverEnterEvent.class);
        popupClose.addUActionEvent(new HUDHoverLeaveEvent() {
            @Override
            public void OnHudHoverLeave(Vector2 location) {
                popupClose.setColor(new RGBA(0, 150, 150, 1));
            }
        }, HUDHoverLeaveEvent.class);

        Rectangle openMenuButton = new Rectangle(new Vector2(gameHandler.getWindow().getWidth()/2 + 100, gameHandler.getWindow().getHeight() - 300),
                new Vector2(100, 100));
        openMenuButton.setColor(new RGBA(0, 150, 150, 1));
        openMenuButton.addUActionEvent(new HUDHoverEnterEvent() {
            @Override
            public void OnHudHoverEnter(Vector2 location) {
                openMenuButton.setColor(new RGBA(0, 150, 200, 1));
            }
        }, HUDHoverEnterEvent.class);
        openMenuButton.addUActionEvent(new HUDHoverLeaveEvent() {
            @Override
            public void OnHudHoverLeave(Vector2 location) {
                openMenuButton.setColor(new RGBA(0, 150, 150, 1));
            }
        }, HUDHoverLeaveEvent.class);
        openMenuButton.addUActionEvent(new HUDClickEvent() {
            @Override
            public void OnHUDClick(Vector2 location, MouseClickType clickType) {
                popupMenu.setVisible(true);
                openMenuButton.setVisible(false);
                playButton.setVisible(false);
            }
        }, HUDClickEvent.class);
        Text openMenuTxt = new Text("Open Menu!", roboto);
        openMenuTxt.setPosition(0, openMenuButton.scale.y/2);
        openMenuTxt.setTextAlign(TextAlign.CENTER);
        openMenuTxt.setLineWidth(openMenuButton.scale.x);
        openMenuButton.add(openMenuTxt);
        cc.add(openMenuButton);

        popupClose.addUActionEvent(new HUDClickEvent() {
            @Override
            public void OnHUDClick(Vector2 location, MouseClickType clickType) {
                popupMenu.setVisible(false);
                openMenuButton.setVisible(true);
                playButton.setVisible(true);
            }
        }, HUDClickEvent.class);

        cc.add(popupMenu);



        //end


        add(cc);

        setCurserStatus(true);


    }

    @Override
    public void update() {

    }
}
