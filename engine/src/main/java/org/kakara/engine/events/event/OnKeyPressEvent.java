package org.kakara.engine.events.event;

public class OnKeyPressEvent {
    private int keycode;
    public OnKeyPressEvent(int keycode){
        this.keycode = keycode;
    }

    public boolean isKeyPressed(int keycode){
        return this.keycode == keycode;
    }
}
