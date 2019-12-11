package org.kakara.engine.collision;

import org.kakara.engine.item.GameItem;

public interface Collider {

    void update();
    void onRegister(GameItem item);

}
