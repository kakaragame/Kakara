package org.kakara.engine.collision;

import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.Vector3;

public interface Collider {

    void update();
    void onRegister(GameItem item);

    Collider setTrigger(boolean value);
    boolean isTrigger();

    void setGravity(float value);
    float getGravity();

    boolean usesGravity();
    Collider setUseGravity(boolean value);

    Vector3 getRelativePoint1();
    Vector3 getAbsolutePoint1();
    Vector3 getRelativePoint2();
    Vector3 getAbsolutePoint2();

}
