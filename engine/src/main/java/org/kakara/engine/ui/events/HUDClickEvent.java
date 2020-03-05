package org.kakara.engine.ui.events;

import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.math.Vector2;

public interface HUDClickEvent extends UActionEvent {
    void OnHUDClick(Vector2 location, MouseClickType clickType);
}
