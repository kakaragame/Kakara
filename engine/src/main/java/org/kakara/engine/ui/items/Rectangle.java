package org.kakara.engine.ui.items;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.kakara.engine.ui.RGBA;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;

public class Rectangle implements HUDItem {
    private Vector2 position;
    private Vector2 scale;
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

    public Rectangle setPosition(Vector2 position){
        this.position = position;
        return this;
    }

    public Rectangle setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
        return this;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Rectangle setScale(Vector2 scale){
        this.scale = scale;
        return this;
    }

    public Rectangle setScale(float x, float y){
        this.scale.x = x;
        this.scale.y = y;
        return this;
    }

    public Vector2 getScale(){
        return this.scale;
    }

    public Rectangle setColor(RGBA color){
        this.color = color;
        return this;
    }

    public RGBA getColor(){
        return color;
    }


    @Override
    public void init(HUD hud, GameHandler handler) {

    }

    @Override
    public void render(HUD hud, GameHandler handler) {
        nvgBeginPath(hud.getVG());
        nvgRect(hud.getVG(), this.position.x, this.position.y, this.scale.x, this.scale.y);
        nvgRGBA((byte) color.r, (byte) color.g, (byte) color.b, (byte) color.aToNano(), colorz);
        nvgFillColor(hud.getVG(), colorz);
        nvgFill(hud.getVG());
    }
}
