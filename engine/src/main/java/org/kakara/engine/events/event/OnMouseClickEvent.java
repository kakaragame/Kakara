package org.kakara.engine.events.event;

import org.joml.Vector2d;
import org.kakara.engine.input.MouseClickType;

public class OnMouseClickEvent {
    private Vector2d position;
    private MouseClickType mouseClickType;

    public OnMouseClickEvent(Vector2d position, MouseClickType mouseClickType){
        this.position = position;
    }

    /**
     * Get the position of the mouse.
     * @return The position of the mouse.
     */
    public Vector2d getMousePosition(){
        return this.position;
    }

    /**
     * Get type of mouse click.
     * @return The type of mouse click.
     */
    public MouseClickType getMouseClickType(){
        return mouseClickType;
    }
}
