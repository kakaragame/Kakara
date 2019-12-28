package org.kakara.engine.item;

import org.kakara.engine.collision.Collider;

/**
 * GameItems that can collide with other GameItems.
 */
public interface Collidable {

    /**
     * Set the collider for a game item
     *
     * @param collider The instance of the collider.
     * @return The instance of the game item.
     */
    GameItem setCollider(Collider collider);

    /**
     * Remove the currently active collider.
     */
    void removeCollider();

    /**
     * Get the currently active collider
     *
     * @return The collider. (Null if none applied)
     */
    Collider getCollider();
}
