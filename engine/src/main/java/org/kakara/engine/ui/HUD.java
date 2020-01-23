package org.kakara.engine.ui;

import org.kakara.engine.GameHandler;
import org.kakara.engine.gui.Window;
import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HUD {

    private long vg;

    private NVGColor color;

    private List<HUDItem> hudItems;

    public HUD(){
        hudItems = new ArrayList<>();
    }

    public void init(Window window) throws Exception{
        this.vg = window.getOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);

        if(this.vg == NULL){
            throw new Exception("Could not init hud");
        }

        color = NVGColor.create();
    }

    public void render(Window window){
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
        for(HUDItem it : hudItems){
            it.render(this, GameHandler.getInstance());
        }

        nvgEndFrame(vg);
        window.restoreState();
    }

    public long getVG(){
        return vg;
    }

    public NVGColor getColor(){
        return color;
    }

    public void addItem(HUDItem item){
        hudItems.add(item);
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }
}
