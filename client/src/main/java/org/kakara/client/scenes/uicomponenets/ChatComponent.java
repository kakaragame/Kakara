package org.kakara.client.scenes.uicomponenets;

import org.kakara.client.scenes.uicomponenets.events.ChatBlurEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatFocusEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.CharacterPressEvent;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.GeneralComponent;
import org.kakara.engine.ui.components.Panel;

import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.text.Font;
import org.kakara.engine.utils.Time;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.*;

public class ChatComponent extends GeneralComponent {

    private Font font;
    private boolean alwaysShowHistory;

    private Text textAreaText;
    private boolean focus;
    private String actualText;
    private Panel historyPanel;
    private Rectangle historyRectangle;
    private boolean wait;

    List<String> history;

    public ChatComponent(Font fontToUse, boolean alwaysShowHistory, Scene scene){
        font = fontToUse;
        this.alwaysShowHistory = alwaysShowHistory;
        focus = false;
        actualText = "";
        history = new ArrayList<>();

        this.setScale(400, 550);

        Panel textArea = new Panel();
        textArea.setScale(400, 50);
        textArea.add(new Rectangle(new Vector2(0, 0), new Vector2(400, 50), new RGBA(148, 148, 148, 200)));

        Panel histroyArea = new Panel();
        histroyArea.setScale(400, 500);
        histroyArea.setPosition(0, 0);
        this.historyRectangle = new Rectangle(new Vector2(0, 0), new Vector2(400, 500), new RGBA(186, 186, 186, 200));
        this.add(historyRectangle);
        textArea.setPosition(0, 500);

        textAreaText = new Text("Press T to type...", font);
        textAreaText.addConstraint(new VerticalCenterConstraint());
        textAreaText.setLineWidth(500);
        textAreaText.setSize(20);
        textAreaText.setPosition(textAreaText.getPosition().add(10, 0));
        textArea.add(textAreaText);


        this.add(histroyArea);
        this.add(textArea);

        this.historyPanel = histroyArea;

        this.historyPanel.setVisible(alwaysShowHistory);
        this.historyRectangle.setVisible(alwaysShowHistory);


        GameHandler.getInstance().getCurrentScene().getEventManager().registerHandler(this);
    }

    /**
     * Add a message to the chat message history.
     * <p>Does not trigger the ChatSendEvent.</p>
     * @param msg The message to send.
     */
    public void addMessage(String msg){
        history.add(msg);
    }

    /**
     * Set if the chat is in focus.
     * @param focus If the chat is in focus.
     */
    public void setFocus(boolean focus){
        this.focus = focus;
    }

    /**
     * If the chat is in focus.
     * @return If the chat is in focus.
     */
    public boolean isFocused(){
        return focus;
    }

    public void setAlwaysShown(boolean alwaysShown){
        this.alwaysShowHistory = alwaysShown;
        if(alwaysShown) swapHistory(true);
        else if(!focus) swapHistory(false);
    }

    public boolean isAlwaysShown(){
        return alwaysShowHistory;
    }

    private void swapHistory(boolean value){
        historyPanel.setVisible(value);
        historyRectangle.setVisible(value);
    }

    @EventHandler
    public void onCharacterPress(CharacterPressEvent evt){
        if(!focus || wait) return;
        byte[] ch = {(byte) evt.getCodePoint()};
        String toadd = new String(ch, StandardCharsets.UTF_8).toLowerCase();
        if(GameHandler.getInstance().getKeyInput().isKeyPressed(GLFW_KEY_LEFT_SHIFT) || GameHandler.getInstance().getKeyInput().isKeyPressed(GLFW_KEY_RIGHT_SHIFT) )
            toadd = toadd.toUpperCase();
        actualText += toadd;
    }

    @EventHandler
    public void onKeyPress(KeyPressEvent evt){
        if(evt.isKeyPressed(GLFW_KEY_T) && !focus){
            focus = true;
            wait = true;
            textAreaText.setText("_");
            actualText = "";
            triggerEvent(ChatFocusEvent.class);
            swapHistory(true);
            return;
        }
        if(evt.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || evt.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) return;
        if(!focus) return;

//        if(evt.isKeyPressed(GLFW_KEY_BACKSPACE)){
//            actualText = actualText.substring(0, actualText.length()-1);
//            return;
//        }

        if(evt.isKeyPressed(GLFW_KEY_ENTER)){
            String t = actualText;
            history.add(actualText);
            actualText = "";
            triggerEvent(ChatSendEvent.class, t);
        }
        if(evt.isKeyPressed(GLFW_KEY_ESCAPE)){
            focus = false;
            actualText = "";
            textAreaText.setText("Press T to type...");
            triggerEvent(ChatBlurEvent.class);
            if(!alwaysShowHistory)
                swapHistory(false);
        }


    }


    @Override
    public void init(HUD hud, GameHandler gameHandler) {
        pollInit(hud, gameHandler);
    }

    private float timer = 0;
    private float backspaceLag = 0;

    @Override
    public void render(Vector2 relative, HUD hud, GameHandler handler){
        pollRender(relative, hud, handler);
        timer += Time.deltaTime;
        if (wait) wait = false;

        if(focus && timer < 1){
            textAreaText.setText(actualText);
        }else if(focus){
            textAreaText.setText(actualText + "_");
        }

        if(timer > 2){
            timer = 0;
        }

        historyPanel.clearChildren();
        List<String> stringToRender = history.size() > 19 ? history.subList(history.size()-19, history.size()) : new ArrayList<>(history);
        ListIterator<String> li = stringToRender.listIterator(stringToRender.size());
        int i = 19;
        while(li.hasPrevious()){
            Text ex = new Text(li.previous(), font);
            ex.setPosition(0, i * 25);
            ex.setLineWidth(500);
            ex.setSize(25);
            historyPanel.add(ex);
            i--;
        }
        backspaceLag -= backspaceLag >= 0 ? Time.deltaTime : 0;
        if(handler.getKeyInput().isKeyPressed(GLFW_KEY_BACKSPACE) && backspaceLag < 0 && actualText.length() > 0){
            actualText = actualText.substring(0, actualText.length()-1);
            backspaceLag = 0.07f;
        }
    }
}
