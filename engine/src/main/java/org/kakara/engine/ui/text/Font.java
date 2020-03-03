package org.kakara.engine.ui.text;

import org.kakara.engine.GameEngine;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.ui.HUD;

import java.io.InputStream;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

public class Font {

    private int font;
    private String name;
    private Resource fileName;
    private InputStream file;
    public Font(String name, InputStream file){
        this.name = name;
        this.file = file;
    }

    public Font(String name, Resource fileName){
        this.name = name;
        this.fileName = fileName;;
    }

    public void init(HUD hud){
        try{
            font = nvgCreateFontMem(hud.getVG(), name, fileName.getByteBuffer(), 1);
//            font = nvgCreateFont(hud.getVG(), name, fileName.getPath());
        }catch(Exception ex){;
            GameEngine.LOGGER.error("Error: Could not load font: " + name);
        }
    }

    public int getFont(){
        return font;
    }
}
