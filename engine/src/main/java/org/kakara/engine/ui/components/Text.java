package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.text.Font;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

public class Text extends GeneralComponent{
    private String text;
    private Font font;
    private float size;
    private float letterSpacing;
    private float lineHeight;
    private float lineWidth;
    private int textAlign;
    private RGBA color;
    public Text(String text, Font font){
        this.text = text;
        this.font = font;
        size = 30;
        lineWidth = 100;
        this.letterSpacing = 5;
        this.lineHeight = 5;
        this.textAlign = NVG_ALIGN_LEFT;
        this.color = new RGBA(255, 255, 255, 1);
    }

    @Override
    public void init(HUD hud, GameHandler handler) {
        pollInit();
    }

    @Override
    public void render(Vector2 relative, HUD hud, GameHandler handler) {
        pollRender(relative, hud, handler);

        if(!isVisible()) return;

        nvgBeginPath(hud.getVG());
        nvgFontSize(hud.getVG(), size);
        nvgFontFaceId(hud.getVG(), font.getFont());
        nvgTextAlign(hud.getVG(), textAlign);
        NVGColor colorz = NVGColor.create();
        nvgRGBA((byte) color.r, (byte) color.g, (byte) color.b, (byte) color.aToNano(), colorz);
        nvgFillColor(hud.getVG(), colorz);

        nvgTextBox(hud.getVG(), getTruePosition().x, getTruePosition().y, lineWidth,  text);
    }

    public void setFont(Font font){
        this.font = font;
    }

    public void setSize(float size){
        this.size = size;
    }

    public float getSize(){
        return this.size;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public void setLineWidth(float width){
        this.lineWidth = width;
    }

    public float getLineWidth(){
        return this.lineWidth;
    }

    public void setLineHeight(float height){
        this.lineHeight = height;
    }

    public float getLineHeight(){
        return this.lineHeight;
    }

    public void setLetterSpacing(float letterSpacing){
        this.letterSpacing = letterSpacing;
    }

    public float getLetterSpacing(){
        return this.letterSpacing;
    }

    public void setTextAlign(int textAlign){
        this.textAlign = textAlign;
    }

    public void setColor(RGBA color){
        this.color = color;
    }

    public RGBA getColor(){
        return this.color;
    }
}
