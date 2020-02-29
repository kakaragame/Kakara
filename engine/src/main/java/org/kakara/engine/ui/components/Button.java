package org.kakara.engine.ui.components;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.ui.HUD;
import org.kakara.engine.ui.HUDItem;
import org.kakara.engine.ui.items.Rectangle;
import org.kakara.engine.ui.items.Sprite;

public class Button extends GeneralComponent {

    private Sprite sprite;
    private Rectangle rectangle;

    public Button(Sprite sprite, Vector2 position, Vector2 scale){
        super();
        GameHandler.getInstance().getEventManager().registerHandler(this);
        this.sprite = sprite;
        this.rectangle = null;
        this.position = position;
        this.scale = scale;
        sprite.setPosition(position);
        sprite.setScale(scale);
    }

    public Button(Rectangle rectangle, Vector2 position, Vector2 scale){
        super();
        this.rectangle = rectangle;
        this.sprite = null;
        this.position = position;
        this.scale = scale;

        rectangle.setPosition(position);
        rectangle.setScale(scale);
    }

//    @Override
//    public void render(Vector2 relativePosition, HUD hud, GameHandler handler) {
//        if(this.rectangle != null)
//            this.rectangle.render(hud, handler);
//        else
//            this.sprite.render(hud, handler);
//    }

    @Override
    public void init(HUD hud, GameHandler handler) {
        sprite.init(hud, handler);
    }
}
