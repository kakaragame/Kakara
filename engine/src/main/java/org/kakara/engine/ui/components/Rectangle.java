package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.events.ActionType;
import org.kakara.engine.ui.events.UActionEvent;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Base Rectangle Component
 */
public class Rectangle extends GeneralComponent {
    private RGBA color;
    private NVGColor colorz;

    public Rectangle(){
        this(new Vector2(0, 0), new Vector2(0, 0), new RGBA());
    }

    public Rectangle(Vector2 position, Vector2 scale, RGBA color){
        this.position = position;
        this.scale = scale;
        this.color = color;
        this.colorz = NVGColor.create();
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
        if(HUD.isColliding(getTruePosition(), scale, new Vector2(evt.getMousePosition()))){
            for(UActionEvent uae : events){
                uae.onActionEvent(ActionType.CLICK);
            }
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


        Vector2 truePos = position.clone().add(relative);
        nvgBeginPath(hud.getVG());
        nvgRect(hud.getVG(), truePos.x, truePos.y, scale.x, scale.y);
        nvgRGBA((byte) color.r, (byte) color.g, (byte) color.b, (byte) color.aToNano(), colorz);
        nvgFillColor(hud.getVG(), colorz);
        nvgFill(hud.getVG());
    }
}
