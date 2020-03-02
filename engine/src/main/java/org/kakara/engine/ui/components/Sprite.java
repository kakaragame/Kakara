package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVGGL3;

import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Display an image onto the HUD.
 */
public class Sprite extends GeneralComponent {
    private float rotation;
    private byte alpha;
    private int image;
    private Texture texture;

    private NVGPaint paint;

    private boolean isHovering;

    public Sprite(Texture texture, Vector2 position, Vector2 scale){
        paint = NVGPaint.create();
        this.alpha = (byte) 255;
        this.rotation = 0;
        this.position = position;
        this.scale = scale;
        this.texture = texture;

        isHovering = false;
    }

    @Override
    public void init(HUD hud, GameHandler handler) {
        pollInit();
        for(Component c : components){
            c.init(hud, handler);
        }
        this.image = NanoVGGL3.nvglCreateImageFromHandle(hud.getVG(), texture.getId(), texture.getWidth(), texture.getHeight(), 0);
        handler.getEventManager().registerHandler(this);
    }

    @EventHandler
    public void onClick(OnMouseClickEvent evt){
        if(HUD.isColliding(getTruePosition(), scale, new Vector2(evt.getMousePosition()))){
            triggerEvent(HUDClickEvent.class, position, evt.getMouseClickType());
        }
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
    public void render(Vector2 relative, HUD hud, GameHandler handler) {
        pollRender(relative, hud, handler);

        boolean isColliding = HUD.isColliding(getTruePosition(), getTrueScale(), new Vector2(handler.getMouseInput().getPosition()));
        if(isColliding && !isHovering){
            isHovering = true;
            triggerEvent(HUDHoverEnterEvent.class, handler.getMouseInput().getCurrentPosition());
        }else if(!isColliding && isHovering){
            isHovering = false;
            triggerEvent(HUDHoverLeaveEvent.class, handler.getMouseInput().getCurrentPosition());
        }

//        Vector2 truePos = position.clone().add(relative);
        NVGPaint imagePaint = nvgImagePattern(hud.getVG(), getTruePosition().x, getTruePosition().y, getTrueScale().x, getTrueScale().y, rotation, image, 1.0f, NVGPaint.calloc());
        nvgBeginPath(hud.getVG());
        nvgRect(hud.getVG(), getTruePosition().x, getTruePosition().y, getTruePosition().x + getTrueScale().x, getTruePosition().y + getTrueScale().y);
        nvgFillPaint(hud.getVG(), imagePaint);
        nvgFill(hud.getVG());
        imagePaint.free();

    }
}
