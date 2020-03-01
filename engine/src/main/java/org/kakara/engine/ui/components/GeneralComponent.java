package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.kakara.engine.ui.events.ActionType;
import org.kakara.engine.ui.events.UActionEvent;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public abstract class GeneralComponent implements Component {

    protected List<UActionEvent> events;
    protected List<Component> components;

    boolean init = false;

    private Vector2 truePosition;
    private Vector2 trueScale;

    public Vector2 position;
    public Vector2 scale;

    public GeneralComponent(){
        events = new ArrayList<>();
        components = new ArrayList<>();
        position = new Vector2(0, 0);
        scale = new Vector2(0, 0);
    }

    @Override
    public void addUActionEvent(UActionEvent uae){
        events.add(uae);
    }

    @Override
    public void render(Vector2 relative, HUD hud, GameHandler handler){
        for(Component c : components){
            c.render(relative.add(position), hud, handler);
        }
    }

    @Override
    public void add(Component component){
        this.components.add(component);
        if(init)
            component.init(GameHandler.getInstance().getSceneManager().getCurrentScene().getHUD(), GameHandler.getInstance());
    }

    @Override
    public void setPosition(float x, float y){
        this.position.x = x;
        this.position.y = y;
    }

    @Override
    public void setPosition(Vector2 pos){
        setPosition(pos.x, pos.y);
    }

    @Override
    public Vector2 getPosition(){
        return position.clone();
    }

    @Override
    public void setScale(float x, float y){
        this.scale.x = x;
        this.scale.y = y;
    }

    @Override
    public void setScale(Vector2 scale){
        setScale(scale.x, scale.y);
    }

    @Override
    public Vector2 getScale(){
        return scale;
    }

    /**
     * Tells the engine to update crucial information of the object for you.
     * Not calling this means certain things, like events, won't work.
     * Call this in the render method first.
     * @param relative
     * @param hud
     * @param handler
     */
    public void pollRender(Vector2 relative, HUD hud, GameHandler handler){
        this.truePosition = position.clone().add(relative);
        this.truePosition =  new Vector2(truePosition.x * ((float) handler.getWindow().getWidth()/ (float)handler.getWindow().initalWidth),
                truePosition.y * ((float) handler.getWindow().getHeight()/(float)handler.getWindow().initalHeight));
        this.trueScale = new Vector2(scale.x * ((float) handler.getWindow().getWidth()/ (float)handler.getWindow().initalWidth),
                scale.y * ((float) handler.getWindow().getHeight()/(float)handler.getWindow().initalHeight));
    }

    /**
     * Tells the engine that the object was inited.
     * This allows the engine to handle a lot of the component hassle for you.
     */
    public void pollInit(){
        init = true;
    }

    /**
     * Get the true position of the object.
     * @return
     */
    public Vector2 getTruePosition(){
        return this.truePosition;
    }

    /**
     * Get the true scale of an object.
     * @return
     */
    public Vector2 getTrueScale(){
        return this.trueScale;
    }

}
