package org.kakara.engine.ui.components;

import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Base Rectangle Component
 */
public class Rectangle extends GeneralComponent {
    private RGBA color;
    private NVGColor colorz;

    private boolean isHovering;

    public Rectangle(){
        this(new Vector2(0, 0), new Vector2(0, 0), new RGBA());
    }

    public Rectangle(Vector2 position, Vector2 scale, RGBA color){
        this.position = position;
        this.scale = scale;
        this.color = color;
        this.colorz = NVGColor.create();
        this.isHovering = false;
    }

    public Rectangle(Vector2 position, Vector2 scale){
        this(position, scale, new RGBA());
    }


    public Rectangle setColor(RGBA color){
        this.color = color;
        return this;
    }

    public RGBA getColor(){
        return color;
    }

    @EventHandler
    public void onClick(OnMouseClickEvent evt){
        if(HUD.isColliding(getTruePosition(), getTrueScale(), new Vector2(evt.getMousePosition()))){
            triggerEvent(HUDClickEvent.class, new Vector2(evt.getMousePosition()), evt.getMouseClickType());
        }
    }


    @Override
    public void init(HUD hud, GameHandler handler) {
        pollInit();
        for(Component cc : components){
            cc.init(hud, handler);
        }
        handler.getEventManager().registerHandler(this);
    }

    @Override
    public void render(Vector2 relative, HUD hud, GameHandler handler) {
        pollRender(relative, hud, handler);
        if(!isVisible()) return;
        boolean isColliding = HUD.isColliding(getTruePosition(), getTrueScale(), new Vector2(handler.getMouseInput().getPosition()));
        if(isColliding && !isHovering){
            isHovering = true;
            triggerEvent(HUDHoverEnterEvent.class, handler.getMouseInput().getCurrentPosition());
        }else if(!isColliding && isHovering){
            isHovering = false;
            triggerEvent(HUDHoverLeaveEvent.class, handler.getMouseInput().getCurrentPosition());
        }

        nvgBeginPath(hud.getVG());
        nvgRect(hud.getVG(), getTruePosition().x, getTruePosition().y, getTrueScale().x, getTrueScale().y);

        nvgRGBA((byte) color.r, (byte) color.g, (byte) color.b, (byte) color.aToNano(), colorz);
        nvgFillColor(hud.getVG(), colorz);
        nvgFill(hud.getVG());

        for(Component cc: components){
            cc.render(relative.clone().add(position), hud, handler);
        }
    }
}
