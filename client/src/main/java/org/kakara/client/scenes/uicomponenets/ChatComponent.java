package org.kakara.client.scenes.uicomponenets;

import org.kakara.client.scenes.uicomponenets.events.ChatBlurEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatFocusEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.CharacterPressEvent;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.input.Input;
import org.kakara.engine.input.key.KeyCode;
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

/**
 * The component for the chat within the game.
 */
public class ChatComponent extends GeneralUIComponent {

    // The of the xSize.
    private final float xSize = 700;


    // Temporary stores what the user has typed when they push the up or down arrows.
    private String tempStorage = "";
    private final Font font;
    private boolean alwaysShowHistory;
    private final Text textAreaText;
    private boolean focus;
    private String actualText;
    private final Panel historyPanel;
    private final Rectangle historyRectangle;
    // This boolean is set to true when the chat is first opened so that the Key system does not
    // put the letter T in the chat box.
    private boolean wait;
    private final List<String> history;
    // The list of sent history. (This is use for the up and down arrow functionality).
    private final List<String> sentHistory = new ArrayList<>();
    private int historyIndex = -1;
    private float timer = 0;
    private long backspaceLag = System.currentTimeMillis();

    /**
     * Create the Chat Component.
     *
     * @param fontToUse         The font to use for the chat.
     * @param alwaysShowHistory If chat history should always be shown.
     * @param scene             The scene that this chat component belongs to.
     */
    public ChatComponent(Font fontToUse, boolean alwaysShowHistory, Scene scene) {
        font = fontToUse;
        this.alwaysShowHistory = alwaysShowHistory;
        focus = false;
        actualText = "";
        history = new ArrayList<>();

        this.setScale(xSize, 550);

        Rectangle textArea = new Rectangle(new Vector2(0, 0), new Vector2(500, 50), new RGBA(140, 140, 140, 0.4f));
        textArea.setVisible(true);

        Panel historyArea = new Panel();
        historyArea.setScale(xSize, 500);
        historyArea.setPosition(0, 0);
        historyArea.setAllowOverflow(true);
        this.historyRectangle = new Rectangle(new Vector2(0, 0), new Vector2(xSize, 500), new RGBA(168, 168, 168, 0.6f));
        this.add(historyRectangle);
        textArea.setPosition(0, 500);

        textAreaText = new Text("Press T to type...", font);
        textAreaText.addConstraint(new VerticalCenterConstraint());
        textAreaText.setLineWidth(600);
        textAreaText.setSize(20);
        textAreaText.setPosition(textAreaText.getPosition().add(10, 0));
        textArea.add(textAreaText);


        this.add(historyArea);
        this.add(textArea);

        this.historyPanel = historyArea;

        this.historyPanel.setVisible(alwaysShowHistory);
        this.historyRectangle.setVisible(alwaysShowHistory);

        setTag("chatcomponent");
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

    /**
     * Check if the chat is always shown.
     *
     * @return If the chat is always shown.
     */
    public boolean isAlwaysShown() {
        return alwaysShowHistory;
    }

    /**
     * Set if the history should always be shown.
     *
     * @param alwaysShown If the history should always be shown.
     */
    public void setAlwaysShown(boolean alwaysShown) {
        this.alwaysShowHistory = alwaysShown;
        if (alwaysShown) swapHistory(true);
        else if (!focus) swapHistory(false);
    }

    /**
     * Swap the history between shown and hidden.
     *
     * @param value If the history should be shown or hidden.
     */
    private void swapHistory(boolean value) {
        historyPanel.setVisible(value);
        historyRectangle.setVisible(value);
    }

    @EventHandler
    public void onCharacterPress(CharacterPressEvent evt) {
        // If T was just pressed or the chat is not in focus.
        if (!focus || wait) return;
        // If the text is too long.
        if (actualText.length() > 100) return;
        // Enter the text into the text box.
        byte[] ch = {(byte) evt.getCodePoint()};
        String toadd = new String(ch, StandardCharsets.UTF_8).toLowerCase();
        if (Input.isKeyDown(KeyCode.LEFT_SHIFT) || Input.isKeyDown(KeyCode.RIGHT_SHIFT))
            toadd = toadd.toUpperCase();
        actualText += toadd;
    }

    @EventHandler
    public void onKeyPress(KeyPressEvent evt) {
        // Open the chat menu.
        if (evt.isKeyPressed(KeyCode.T) && !focus) {
            focus = true;
            wait = true;
            textAreaText.setText("_");
            actualText = "";
            triggerEvent(ChatFocusEvent.class);
            swapHistory(true);
            historyIndex = -1;
            return;
        }
        if (evt.isKeyPressed(KeyCode.LEFT_SHIFT) || evt.isKeyPressed(KeyCode.RIGHT_SHIFT)) return;
        if (!focus) return;

        GameHandler handler = GameHandler.getInstance();
        // Allow the copying and pasting of data.
        if (Input.isKeyDown(KeyCode.LEFT_CONTROL) && Input.isKeyDown(KeyCode.V)) {
            actualText += handler.getClipboard().getClipboard();
            if (actualText.length() > 100)
                actualText = actualText.substring(0, 100);
        }

        //TODO add text copying and highlighting.

        // Show previous history.
        if (Input.isKeyDown(KeyCode.UP_ARROW)) {
            if (historyIndex + 1 < sentHistory.size()) {
                if (historyIndex == -1)
                    tempStorage = actualText;
                ++historyIndex;
                actualText = sentHistory.get(sentHistory.size() - 1 - historyIndex);
            }
        }
        // go back down history.
        if (Input.isKeyDown(KeyCode.DOWN_ARROW)) {
            if (historyIndex - 1 > -2) {
                historyIndex--;
                if (historyIndex == -1) {
                    actualText = tempStorage;
                } else {
                    actualText = sentHistory.get(sentHistory.size() - 1 - historyIndex);
                }
            }
        }

        // Send the text.
        if (evt.isKeyPressed(KeyCode.ENTER)) {
            String t = actualText;
            // Prevent duplicates in the history.
            sentHistory.remove(actualText);
            sentHistory.add(actualText);

            actualText = "";
            triggerEvent(ChatSendEvent.class, t);
        }
        // Close the menu.
        if (evt.isKeyPressed(KeyCode.ESCAPE)) {
            focus = false;
            actualText = "";
            textAreaText.setText("Press T to type...");
            triggerEvent(ChatBlurEvent.class);
            if (!alwaysShowHistory)
                swapHistory(false);
        }


    }

    /**
     * Initalizes the ChatComponent.
     *
     * <p>Called internally by the engine.</p>
     *
     * @param hud         The instance of the UserInterface.
     * @param gameHandler The instance of the GameHandler.
     */
    @Override
    public void init(UserInterface hud, GameHandler gameHandler) {
        pollInit(hud, gameHandler);
    }

    /**
     * Renders the actual chat component.
     *
     * <p>This is called internally by the engine.</p>
     *
     * @param relative The relative position of the component.
     * @param hud      The instance of the UserInterface.
     * @param handler  The instance of the GameHandler.
     */
    @Override
    public void render(Vector2 relative, UserInterface hud, GameHandler handler) {
        super.render(relative, hud, handler);
        timer += Time.getDeltaTime();
        if (wait) wait = false;

        if (focus && timer < 0.75) {
            textAreaText.setText(actualText);
        } else if (focus) {
            textAreaText.setText(actualText + "_");
        }

        if (timer > 1.5) {
            timer = 0;
        }

        if (!focus)
            return;

        historyPanel.clearChildren();
        List<String> stringToRender = history.size() > 19 ? history.subList(history.size() - 19, history.size()) : new ArrayList<>(history);
        ListIterator<String> li = stringToRender.listIterator(Math.min(history.size(), 19));
        // Display the text in ascending order.
        float prevPos = 500;
        while (li.hasPrevious()) {
            BoundedColoredText ex = new BoundedColoredText(li.previous(), font);
            ex.setParentCanvas(getParentCanvas());
            ex.setMaximumBound(new Vector2(350, 55));
            ex.init(hud, handler);
            ex.calculateLineNumbers(hud, handler);
            prevPos -= (23 * ex.getLineNumbers()) - (ex.getLineHeight() * ex.getLineNumbers());
            ex.setPosition(0, prevPos);
            if (prevPos < 10) break;

            ex.setSize(23);
            historyPanel.add(ex);
        }
        if (Input.isKeyDown(KeyCode.BACKSPACE) && actualText.length() > 0 && System.currentTimeMillis() - backspaceLag > 80) {
            backspaceLag = System.currentTimeMillis();
            actualText = actualText.substring(0, actualText.length() - 1);
        }
    }
}
