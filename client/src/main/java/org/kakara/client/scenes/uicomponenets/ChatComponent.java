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
import org.kakara.engine.ui.UserInterface;
import org.kakara.engine.ui.components.GeneralUIComponent;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.BoundedColoredText;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.utils.RGBA;
import org.kakara.engine.utils.Time;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.*;

public class ChatComponent extends GeneralUIComponent {

    final float xSize = 700;
    String tempStorage = "";
    private final Font font;
    private boolean alwaysShowHistory;
    private final Text textAreaText;
    private boolean focus;
    private String actualText;
    private final Panel historyPanel;
    private final Rectangle historyRectangle;
    private boolean wait;
    private final List<String> history;
    private final List<String> sentHistory = new ArrayList<>();
    private int historyIndex = -1;
    private float timer = 0;
    private long backspaceLag = System.currentTimeMillis();

    public ChatComponent(Font fontToUse, boolean alwaysShowHistory, Scene scene) {
        font = fontToUse;
        this.alwaysShowHistory = alwaysShowHistory;
        focus = false;
        actualText = "";
        history = new ArrayList<>();

        this.setScale(xSize, 550);

        Panel textArea = new Panel();
        textArea.setScale(xSize, 50);
        textArea.add(new Rectangle(new Vector2(0, 0), new Vector2(500, 50), new RGBA(140, 140, 140, 0.4f)));

        Panel histroyArea = new Panel();
        histroyArea.setScale(xSize, 500);
        histroyArea.setPosition(0, 0);
        this.historyRectangle = new Rectangle(new Vector2(0, 0), new Vector2(xSize, 500), new RGBA(168, 168, 168, 0.6f));
        this.add(historyRectangle);
        textArea.setPosition(0, 500);

        textAreaText = new Text("Press T to type...", font);
        textAreaText.addConstraint(new VerticalCenterConstraint());
        textAreaText.setLineWidth(600);
        textAreaText.setSize(20);
        textAreaText.setPosition(textAreaText.getPosition().add(10, 0));
        textArea.add(textAreaText);


        this.add(histroyArea);
        this.add(textArea);

        this.historyPanel = histroyArea;

        this.historyPanel.setVisible(alwaysShowHistory);
        this.historyRectangle.setVisible(alwaysShowHistory);


        scene.getEventManager().registerHandler(this);
    }

    /**
     * Add a message to the chat message history.
     * <p>Does not trigger the ChatSendEvent.</p>
     *
     * @param msg The message to send.
     */
    public void addMessage(String msg) {
        history.add(msg);
    }

    /**
     * Set if the chat is in focus.
     *
     * @param focus If the chat is in focus.
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * If the chat is in focus.
     *
     * @return If the chat is in focus.
     */
    public boolean isFocused() {
        return focus;
    }

    public boolean isAlwaysShown() {
        return alwaysShowHistory;
    }

    public void setAlwaysShown(boolean alwaysShown) {
        this.alwaysShowHistory = alwaysShown;
        if (alwaysShown) swapHistory(true);
        else if (!focus) swapHistory(false);
    }

    private void swapHistory(boolean value) {
        historyPanel.setVisible(value);
        historyRectangle.setVisible(value);
    }

    @EventHandler
    public void onCharacterPress(CharacterPressEvent evt) {
        if (!focus || wait) return;
        if (actualText.length() > 100) return;
        byte[] ch = {(byte) evt.getCodePoint()};
        String toadd = new String(ch, StandardCharsets.UTF_8).toLowerCase();
        if (GameHandler.getInstance().getKeyInput().isKeyPressed(GLFW_KEY_LEFT_SHIFT) || GameHandler.getInstance().getKeyInput().isKeyPressed(GLFW_KEY_RIGHT_SHIFT))
            toadd = toadd.toUpperCase();
        actualText += toadd;
    }

    @EventHandler
    public void onKeyPress(KeyPressEvent evt) {
        if (evt.isKeyPressed(GLFW_KEY_T) && !focus) {
            focus = true;
            wait = true;
            textAreaText.setText("_");
            actualText = "";
            triggerEvent(ChatFocusEvent.class);
            swapHistory(true);
            historyIndex = -1;
            return;
        }
        if (evt.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || evt.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) return;
        if (!focus) return;

        GameHandler handler = GameHandler.getInstance();
        if (handler.getKeyInput().isKeyPressed(GLFW_KEY_LEFT_CONTROL) && handler.getKeyInput().isKeyPressed(GLFW_KEY_V)) {
            actualText += GameHandler.getInstance().getClipboard().getClipboard();
            if (actualText.length() > 100)
                actualText = actualText.substring(0, 100);
        }

        //TODO add text copying and highlighting.

        if (handler.getKeyInput().isKeyPressed(GLFW_KEY_UP)) {
            if (historyIndex + 1 < sentHistory.size()) {
                if (historyIndex == -1)
                    tempStorage = actualText;
                ++historyIndex;
                actualText = sentHistory.get(sentHistory.size() - 1 - historyIndex);
            }
        }
        if (handler.getKeyInput().isKeyPressed(GLFW_KEY_DOWN)) {
            if (historyIndex - 1 > -2) {
                historyIndex--;
                if (historyIndex == -1) {
                    actualText = tempStorage;
                } else {
                    actualText = sentHistory.get(sentHistory.size() - 1 - historyIndex);
                }
            }
        }

        if (evt.isKeyPressed(GLFW_KEY_ENTER)) {
            String t = actualText;
            //history.add(actualText);
            //sentHistory.add(actualText);
            actualText = "";
            triggerEvent(ChatSendEvent.class, t);
        }
        if (evt.isKeyPressed(GLFW_KEY_ESCAPE)) {
            focus = false;
            actualText = "";
            textAreaText.setText("Press T to type...");
            triggerEvent(ChatBlurEvent.class);
            if (!alwaysShowHistory)
                swapHistory(false);
        }


    }

    @Override
    public void init(UserInterface hud, GameHandler gameHandler) {
        pollInit(hud, gameHandler);
    }

    @Override
    public void render(Vector2 relative, UserInterface hud, GameHandler handler) {
        pollRender(relative, hud, handler);
        timer += Time.getDeltaTime();
        if (wait) wait = false;

        if (focus && timer < 1) {
            textAreaText.setText(actualText);
        } else if (focus) {
            textAreaText.setText(actualText + "_");
        }

        if (timer > 2) {
            timer = 0;
        }

        if (!focus)
            return;

        historyPanel.clearChildren();
        List<String> stringToRender = history.size() > 19 ? history.subList(history.size() - 19, history.size()) : new ArrayList<>(history);
        ListIterator<String> li = stringToRender.listIterator(Math.min(history.size(), 19));
//        19;
        float prevPos = 500;
        while (li.hasPrevious()) {
            BoundedColoredText ex = new BoundedColoredText(li.previous(), font);
            ex.setMaximumBound(new Vector2(350, 55));
            ex.init(hud, handler);
            ex.calculateLineNumbers(hud, handler);
            prevPos -= (23 * ex.getLineNumbers()) - (ex.getLineHeight() * ex.getLineNumbers());
            ex.setPosition(0, prevPos);
            if (prevPos < 10) break;

            ex.setSize(23);
            historyPanel.add(ex);
        }
        if (handler.getKeyInput().isKeyPressed(GLFW_KEY_BACKSPACE) && actualText.length() > 0 && System.currentTimeMillis() - backspaceLag > 100) {
            backspaceLag = System.currentTimeMillis();
            actualText = actualText.substring(0, actualText.length() - 1);
        }
    }
}
