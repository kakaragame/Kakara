package org.kakara.engine.ui;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;

public interface HUDItem {
    void init(HUD hud, GameHandler handler);
    void render(HUD hud, GameHandler handler);
}
