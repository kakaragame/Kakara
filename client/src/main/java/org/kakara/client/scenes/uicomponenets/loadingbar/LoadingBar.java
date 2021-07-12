package org.kakara.client.scenes.uicomponenets.loadingbar;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.UserInterface;
import org.kakara.engine.ui.components.GeneralUIComponent;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.components.text.Text;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.utils.RGBA;

/**
 * A loading bar UI Component.
 */
public class LoadingBar extends GeneralUIComponent {
    private final Text percentText;
    private final Rectangle outer;
    private final Rectangle inner;
    private float percent;

    /**
     * Construct the Loading Bar.
     *
     * @param font The font for the loading bar to use.
     */
    public LoadingBar(Font font) {
        super();
        this.position = new Vector2(0, 0);
        this.scale = new Vector2(300, 30);

        RGBA foregroundColor = new RGBA(0, 150, 150, 1);
        RGBA backgroundColor = new RGBA(255, 10, 10, 1);
        percentText = new Text("0.00%", font);
        percentText.setPosition(scale.x / 2, scale.y / 2 + 10);
        outer = new Rectangle(new Vector2(0, 0), new Vector2(scale.x, scale.y), backgroundColor);
        inner = new Rectangle(new Vector2(0, 0), new Vector2(0, scale.y - 5), foregroundColor);

        this.add(outer);
        this.add(inner);
        this.add(percentText);
    }

    /**
     * Get the percent for the loading bar.
     *
     * @return The percent. (0 - 1)
     */
    public float getPercent() {
        return percent;
    }

    /**
     * Set the percent for the loading bar.
     *
     * @param percent The percent for the loading bar. (0 - 1)
     */
    public void setPercent(float percent) {
        this.percent = percent;
    }

    /**
     * Set the foreground color for the Loading Bar.
     *
     * @param color The foreground color.
     */
    public void setForegroundColor(RGBA color) {
        inner.setColor(color);
    }

    /**
     * Set the background color.
     *
     * @param color The background color.
     */
    public void setBackgroundColor(RGBA color) {
        outer.setColor(color);
    }

    /**
     * Set the color of the text.
     *
     * @param color The color of the text.
     */
    public void setTextColor(RGBA color) {
        percentText.setColor(color);
    }

    @Override
    public void init(UserInterface userInterface, GameHandler handler) {
        pollInit(userInterface, handler);
    }

    @Override
    public void render(Vector2 relative, UserInterface userInterface, GameHandler handler) {
        super.render(relative, userInterface, handler);
        if (percent <= 1)
            inner.setScale(outer.scale.x * percent, scale.y);
        percentText.setText(Math.round(percent * 100) + "%");

        if (percent >= 1) {
            triggerEvent(LoadingBarCompleteEvent.class, percent);
            percent = 0;
        }
    }
}
