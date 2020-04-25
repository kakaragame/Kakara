package org.kakara.engine.collision;

import org.kakara.engine.math.Vector3;

/**
 * Handles collision for the entire game.
 * Implemented by RenderBlock and MeshGameItem
 */
public interface Collidable {
    Vector3 getColPosition();
    float getColScale();
    void setCollider(Collider collider);
    void removeCollider();
    void colTranslateBy(Vector3 vec);
    void setColPosition(Vector3 vec);

    /**
     * Get the collider associated with the object
     * @return The collider. (Null if there is none).
     */
    Collider getCollider();

    /**
     * If the collidable is selected.
     * @return If the collidable is selected.
     */
    boolean isSelected();

    /**
     * Set if the collidable is selected.
     * @param selected If the collidable is selected.
     */
    void setSelected(boolean selected);
}
