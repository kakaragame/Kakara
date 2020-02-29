package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.events.ActionType;
import org.kakara.engine.ui.events.UActionEvent;

public class Panel extends GeneralComponent {

    public Panel(){
        super();
    }

    @Override
    public void init(HUD hud, GameHandler handler) {
        pollInit();
        for(Component cc : components){
            cc.init(hud, handler);
        }
        handler.getEventManager().registerHandler(this);
    }

    @Override
    public void render(Vector2 relative, HUD hud, GameHandler handler){
        pollRender(relative, hud, handler);
        for(Component cc : components){
            cc.render(position.clone().add(relative.clone()), hud, handler);
        }
    }

    @EventHandler
    public void onClick(OnMouseClickEvent evt){
        if(HUD.isColliding(position, scale, new Vector2(evt.getMousePosition()))){
            for(UActionEvent uae : events){
                uae.onActionEvent(ActionType.CLICK);
            }
        }
    }
}
