package org.kakara.client.scenes.maingamescene.components;

import org.kakara.client.local.game.DroppedItem;
import org.kakara.engine.components.Component;

/**
 * Handles the syncing of the core and the engine for dropped items.
 * <p>Syncs the Dropped Item's GameItem with the its {@link DroppedItem} object.</p>
 */
public class DroppedItemComponent extends Component {
    private DroppedItem droppedItem;

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    @Override
    public void physicsUpdate(float deltaTime) {
        if (droppedItem == null) return;
        // Update the location to match the one from the engine.
        droppedItem.getLocation().set(getTransform().getPosition().x, getTransform().getPosition().y, getTransform().getPosition().z);
    }

    /**
     * Set the dropped item.
     * <p>This must be set.</p>
     *
     * @param droppedItem The dropped item.
     */
    public void setDroppedItem(DroppedItem droppedItem) {
        this.droppedItem = droppedItem;
    }
}
