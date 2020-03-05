package org.kakara.engine.ui.items;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.kakara.engine.ui.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all of the components.
 */
public class ComponentCanvas implements HUDItem {
    private List<Component> components;
    boolean init = false;

    public ComponentCanvas(){
        components = new ArrayList<>();
    }

    /**
     * Add a child component into the canvas
     * @param component The component to add.
     */
    public void add(Component component){
        components.add(component);
        if(init){
            component.init(GameHandler.getInstance().getSceneManager().getCurrentScene().getHUD(), GameHandler.getInstance());
        }
    }

    @Override
    public void init(HUD hud, GameHandler handler) {
        init = true;
        for(Component c : components){
            c.init(hud, handler);
        }
    }

    @Override
    public void render(HUD hud, GameHandler handler) {
        for(Component component : components){
            component.render(new Vector2(0, 0), hud, handler);
        }
    }
}
