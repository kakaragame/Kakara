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

    private List<UActionEvent> events;
    private List<Component> components;

    public Vector2 position;
    public Vector2 scale;

    public GeneralComponent(){
        events = new ArrayList<>();
        components = new ArrayList<>();
        position = new Vector2(0, 0);
        scale = new Vector2(0, 0);
        glfwSetMouseButtonCallback(GameHandler.getInstance().getWindow().getWindowHandler(), (windowHandle, button, action, mode) -> {
            if(action != GLFW_PRESS) return;
            if(isColliding(position, scale, new Vector2(GameHandler.getInstance().getMouseInput().getCurrentPosition()))){
                for(UActionEvent uae : events){
                    uae.onActionEvent(ActionType.CLICK);
                }
            }
        });
    }

    @Override
    public void addUActionEvent(UActionEvent uae){
        events.add(uae);
    }

    @Override
    public void render(Vector2 relative, HUD hud, GameHandler handler){
        for(Component c : components){
            c.render(position, hud, handler);
        }
        Vector2 mouse = new Vector2(handler.getMouseInput().getPosition());

//        System.out.println(position.clone().add(scale) + "  " + mouse);



    }

    @EventHandler
    public void onMousePressEvent(OnMouseClickEvent evt){
        System.out.println("test");
        if(isColliding(position, scale, new Vector2(evt.getMousePosition()))){
            for(UActionEvent uae : events){
                uae.onActionEvent(ActionType.CLICK);
            }
        }
    }


    @Override
    public void add(Component component){
        this.components.add(component);
    }

    private boolean isColliding(Vector2 position, Vector2 scale, Vector2 mouse){
        boolean overx = position.x < mouse.x && mouse.x < position.x + scale.x;
        boolean overy = position.y < mouse.y && mouse.y < position.y + scale.y;
        return overx && overy;
    }

}
