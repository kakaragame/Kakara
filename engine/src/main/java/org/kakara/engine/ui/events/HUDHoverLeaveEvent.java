package org.kakara.engine.ui.events;

import org.kakara.engine.math.Vector2;

public interface HUDHoverLeaveEvent extends UActionEvent {
    void OnHudHoverLeave(Vector2 location);
}
