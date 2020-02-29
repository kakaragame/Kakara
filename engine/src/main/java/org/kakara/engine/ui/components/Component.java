package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.events.UActionEvent;

public interface Component {
    void addUActionEvent(UActionEvent uae);
    void add(Component component);
    void render(Vector2 relativePosition, HUD hud, GameHandler handler);
    void init(HUD hud, GameHandler handler);
    void setPosition(float x, float y);
    void setPosition(Vector2 pos);
    Vector2 getPosition();
    void setScale(float x, float y);
    void setScale(Vector2 scale);
    Vector2 getScale();

}
