package org.kakara.engine.ui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gui.Window;
import org.kakara.engine.math.Vector2;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL11.glViewport;

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
        for(HUDItem it : hudItems){
            it.init(this, GameHandler.getInstance());
        }
    }

    public void render(Window window){
//        IntBuffer fW = BufferUtils.createIntBuffer(1);
//        IntBuffer fH = BufferUtils.createIntBuffer(1);
//        glfwGetFramebufferSize(window.getWindowHandler(), fW, fH);
//        glViewport(0, 0, fW.get(0), fH.get(0));
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
        Long check = this.vg;
        if(check != null){
            item.init(this, GameHandler.getInstance());
        }
        hudItems.add(item);
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
