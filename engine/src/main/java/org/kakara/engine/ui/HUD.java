package org.kakara.engine.ui;

import org.kakara.engine.GameHandler;
import org.kakara.engine.gui.Window;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.text.Font;
import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HUD {

    private long vg;

    private NVGColor color;

    private List<HUDItem> hudItems;
    private List<Font> fonts;

    private HUDImageCache imageCache;
    private Scene scene;

    public HUD(Scene scene){
        hudItems = new ArrayList<>();
        fonts = new ArrayList<>();
        imageCache = new HUDImageCache(this);
        this.scene = scene;
    }

    public void init(Window window) throws Exception{
        this.vg = window.getOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);

        if(this.vg == NULL){
            throw new Exception("Could not init hud");
        }

        color = NVGColor.create();
        for(HUDItem it : hudItems){
            it.init(this, GameHandler.getInstance());
        }
        for(Font ft : fonts){
            ft.init(this);
        }
    }

    public void render(Window window){
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
        for(HUDItem it : hudItems){
            it.render(this, GameHandler.getInstance());
        }
        nvgEndFrame(vg);
        window.restoreState();
    }

    public void cleanup(){

    }

    public long getVG(){
        return vg;
    }

    public NVGColor getColor(){
        return color;
    }

    public void addItem(HUDItem item){
        Long check = this.vg;
        if(check != null){
            item.init(this, GameHandler.getInstance());
        }
        hudItems.add(item);
    }

    public void addFont(Font font){
        Long check = this.vg;
        if(check != null){
            font.init(this);
        }
        fonts.add(font);
    }

    public HUDImageCache getImageCache(){
        return imageCache;
    }

    public Scene getScene(){
        return scene;
    }

    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public static boolean isColliding(Vector2 position, Vector2 scale, Vector2 mouse){
        boolean overx = position.x < mouse.x && mouse.x < position.x + scale.x;
        boolean overy = position.y < mouse.y && mouse.y < position.y + scale.y;
        return overx && overy;
    }
}
