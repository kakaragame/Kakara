package org.kakara.engine.ui.text;

import org.kakara.engine.GameEngine;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.ui.HUD;

import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

public class Font {

    private int font;
    private String name;
    private Resource fileName;

    private ByteBuffer thisNeedsToBeHereSoTheGarbageCollectorDoesNotComeAndGetMe;
    public Font(String name, Resource fileName){
        this.name = name;
        this.fileName = fileName;;
    }

    public void init(HUD hud){
        try{
            ByteBuffer bb = fileName.getByteBuffer();
            font = nvgCreateFontMem(hud.getVG(), name, bb, 1);
            this.thisNeedsToBeHereSoTheGarbageCollectorDoesNotComeAndGetMe = bb;
        }catch(Exception ex){;
            GameEngine.LOGGER.error("Error: Could not load font: " + name);
        }
    }

    public int getFont(){
        return font;
    }

    public ByteBuffer getByteBuffer(){
        return thisNeedsToBeHereSoTheGarbageCollectorDoesNotComeAndGetMe;
    }
}
