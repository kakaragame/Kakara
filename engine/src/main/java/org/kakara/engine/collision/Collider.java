package org.kakara.engine.collision;

import org.kakara.engine.item.GameItem;

public interface Collider {

    void update();
    void onRegister(GameItem item);

    Collider setTrigger(boolean value);
    boolean isTrigger();

    void setGravity(float value);
    float getGravity();

    boolean usesGravity();
    Collider setUseGravity(boolean value);

}
