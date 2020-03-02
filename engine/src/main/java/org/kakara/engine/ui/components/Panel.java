package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.kakara.engine.ui.events.UActionEvent;

public class Panel extends GeneralComponent {

    private boolean isHovering;
    public Panel(){
        super();
        isHovering = false;
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

        boolean isColliding = HUD.isColliding(getTruePosition(), getTrueScale(), new Vector2(handler.getMouseInput().getPosition()));
        if(isColliding && !isHovering){
            isHovering = true;
            triggerEvent(HUDHoverEnterEvent.class, handler.getMouseInput().getCurrentPosition());
        }else if(!isColliding && isHovering){
            isHovering = false;
            triggerEvent(HUDHoverLeaveEvent.class, handler.getMouseInput().getCurrentPosition());
        }
    }

    @EventHandler
    public void onClick(OnMouseClickEvent evt){
        if(HUD.isColliding(position, scale, new Vector2(evt.getMousePosition()))){
            triggerEvent(HUDClickEvent.class, position, evt.getMouseClickType());
        }
    }
}
