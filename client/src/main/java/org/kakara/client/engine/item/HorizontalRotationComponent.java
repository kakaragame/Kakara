package org.kakara.client.engine.item;

import org.kakara.engine.components.Component;
import org.kakara.engine.physics.collision.BoxCollider;

public class HorizontalRotationComponent extends Component {
    @Override
    public void start() {

    }

    @Override
    public void update() {
        getGameItem().transform.getRotation().rotateY(0.01f);

    }
}
