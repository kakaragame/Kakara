package org.kakara.engine.ui.items;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVGGL3;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Display an image onto the HUD.
 */
public class Sprite implements HUDItem {
    private Vector2 position;
    private Vector2 scale;
    private float rotation;
    private byte alpha;
    private int image;
    private Texture texture;

    private NVGPaint paint;

    public Sprite (Texture texture, Vector2 position, Vector2 scale){
        paint = NVGPaint.create();
        this.alpha = (byte) 255;
        this.rotation = 0;
        this.position = position;
        this.scale = scale;
        this.texture = texture;
    }

    @Override
    public void init(HUD hud, GameHandler handler) {
        this.image = NanoVGGL3.nvglCreateImageFromHandle(hud.getVG(), texture.getId(), texture.getWidth() ,texture.getHeight(), 0);
    }

    public Sprite(Texture tex){
        this(tex, new Vector2(0, 0), new Vector2(1, 1));
    }

    public void setImage(Texture tex){
        this.texture = tex;
        nvgDeleteImage(GameHandler.getInstance().getSceneManager().getCurrentScene().getHUD().getVG(),
                image);
        this.image = NanoVGGL3.nvglCreateImageFromHandle(
                GameHandler.getInstance().getSceneManager().getCurrentScene().getHUD().getVG(),
                tex.getId(), texture.getWidth(),tex.getHeight(), 0);
    }

    public Sprite setPosition(Vector2 pos){
        this.position = pos;
        return this;
    }

    public Vector2 getPosition(){
        return this.position;
    }

    public Sprite setScale(Vector2 scale){
        this.scale = scale;
        return this;
    }

    public Vector2 getScale(){
        return this.scale;
    }

    public Sprite setAlpha(byte b){
        this.alpha = b;
        return this;
    }

    public int getAlpha(){
        return this.alpha & 0xFF;
    }

    public Sprite setRotation(float rotation){
        this.rotation = rotation;
        return this;
    }

    public float getRotation(){
        return this.rotation;
    }

    @Override
    public void render(HUD hud, GameHandler handler) {
        NVGPaint imagePaint = nvgImagePattern(hud.getVG(), position.x, position.y, scale.x, scale.y, rotation, image, 1.0f, NVGPaint.calloc());
        nvgBeginPath(hud.getVG());
        nvgRect(hud.getVG(), position.x, position.y, position.x + scale.x, position.y + scale.y);
        nvgFillPaint(hud.getVG(), imagePaint);
        nvgFill(hud.getVG());
        imagePaint.free();

    }
}
