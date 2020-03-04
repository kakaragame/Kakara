package org.kakara.engine.ui;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.nvgDeleteImage;

/**
 * Caches the images that way memory can be cleared up when the image is no longer being used.
 */
public class HUDImageCache {
    private List<Integer> image;
    private HUD hud;
    public HUDImageCache(HUD hud){
        image = new ArrayList<>();
        this.hud = hud;
    }

    public void addImage(int id){
        this.image.add(id);
    }

    public void removeImage(int id){
        this.image.remove(id);
    }

    public void cleanup(){
        for(int i : image){
            nvgDeleteImage(hud.getVG(), i);
        }
    }
}
