package org.kakara.client.engine.item;

import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.features.Feature;

public class HorizontalRotationFeature implements Feature {
    @Override
    public void update(GameItem gameItem) {
        gameItem.getRotation().rotateY(0.01f);
    }
}
