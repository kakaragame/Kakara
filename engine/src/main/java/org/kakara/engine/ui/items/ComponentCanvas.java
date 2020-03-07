package org.kakara.engine.ui.items;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.kakara.engine.ui.components.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds all of the components.
 */
public class ComponentCanvas implements HUDItem {
    private List<Component> components;
    boolean init = false;

    private Scene scene;

    public ComponentCanvas(Scene scene){
        components = new ArrayList<>();
        this.scene = scene;
    }

    /**
     * Add a child component into the canvas
     * @param component The component to add.
     */
    public void add(Component component){
        components.add(component);
        if(init){
            component.init(scene.getHUD(), GameHandler.getInstance());
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

    /**
     * Get a list of the child components
     * @return The child components
     */
    public List<Component> getComponents(){
        return components;
    }

    /**
     * Clear all of the components
     */
    public void clearComponents(){
        components.clear();
    }

    /**
     * Remove a component from the list.
     * @param c The component
     */
    public void removeComponent(Component c){
        components.remove(c);
    }
}
