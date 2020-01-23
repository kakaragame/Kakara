package org.kakara.engine.ui.items;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;

public class Rectangle implements HUDItem {
    private Vector2 position;
    private Vector2 scale;

    public Rectangle(){
        this(new Vector2(0, 0), new Vector2(0, 0));
    }

    public Rectangle(Vector2 position, Vector2 scale){
        this.position = position;
        this.scale = scale;
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

    @Override
    public void render(HUD hud, GameHandler handler) {
        nvgBeginPath(hud.getVG());
        nvgRect(hud.getVG(), this.position.x, this.position.y, this.scale.x, this.scale.y);
        nvgFillColor(hud.getVG(), rgba(0x23, 0xa1, 0xf1, 200, hud.getColor()));
        nvgFill(hud.getVG());
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }
}
