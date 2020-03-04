package org.kakara.engine.ui.events;

import org.kakara.engine.math.Vector2;

public interface HUDHoverEnterEvent extends UActionEvent {
    void OnHudHoverEnter(Vector2 location);
}
