package org.kakara.engine.events.event;

public class KeyPressEvent {
    private int keycode;
    public KeyPressEvent(int keycode){
        this.keycode = keycode;
    }

    public boolean isKeyPressed(int keycode){
        return this.keycode == keycode;
    }
}
