package org.kakara.engine.ui.text;

import org.kakara.engine.GameEngine;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.ui.HUD;

import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

/**
 * Handles the font for the UI.
 * <p>Remember to add this to the hud using HUD.addFont()</p>
 */
public class Font {

    private int font;
    private String name;
    private Resource fileName;

    private ByteBuffer thisNeedsToBeHereSoTheGarbageCollectorDoesNotComeAndGetMe;
    public Font(String name, Resource fileName){
        this.name = name;
        this.fileName = fileName;;
    }

    /**
     * Internal Use Only
     */
    public void init(HUD hud){
        try{
            ByteBuffer bb = fileName.getByteBuffer();
            font = nvgCreateFontMem(hud.getVG(), name, bb, 1);
            this.thisNeedsToBeHereSoTheGarbageCollectorDoesNotComeAndGetMe = bb;
        }catch(Exception ex){;
            GameEngine.LOGGER.error("Error: Could not load font: " + name);
        }
    }

    /**
     * Get the id of the font for use with NVG static functions.
     * @return
     */
    public int getFont(){
        return font;
    }

    /**
     * Internal Use Only
     */
    public ByteBuffer getByteBuffer(){
        return thisNeedsToBeHereSoTheGarbageCollectorDoesNotComeAndGetMe;
    }
}