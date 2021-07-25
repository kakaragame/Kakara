package org.kakara.client.scenes.maingamescene;

import org.kakara.engine.Camera;
import org.kakara.engine.math.Intersection;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;

/**
 * A utility class for ObjectPicking.
 */
public final class ObjectPickingUtils {
    private ObjectPickingUtils() {}

    /**
     * Send a raycast in the direction the camera is facing from the provided position to get the
     * closest object that it intersects with.
     *
     * <p>This is mainly used for Voxels.</p>
     *
     * @param absoluteBlockPos The absolute position to check from.
     * @param camera           The camera to use.
     * @return The closest value. (Will be whole numbers as it is designed for Voxels).
     */
    public static Vector3 closestValue(Vector3 absoluteBlockPos, Camera camera) {
        Vector2 result = new Vector2(0, 0);

        float closestResult = 20;
        Vector3 closestValue = absoluteBlockPos.clone();

        Vector3 front = absoluteBlockPos.add(1, 0, 0);
        if (Intersection.intersect((int) front.x, (int) front.y, (int) front.z, camera, result) && result.x < closestResult) {
            closestResult = result.x;
            closestValue = absoluteBlockPos.add(1, 0, 0);
        }

        Vector3 back = absoluteBlockPos.add(-1, 0, 0);
        if (Intersection.intersect((int) back.x, (int) back.y, (int) back.z, camera, result) && result.x < closestResult) {
            closestResult = result.x;
            closestValue = absoluteBlockPos.add(-1, 0, 0);
        }

        Vector3 left = absoluteBlockPos.add(0, 0, 1);
        if (Intersection.intersect((int) left.x, (int) left.y, (int) left.z, camera, result) && result.x < closestResult) {
            closestResult = result.x;
            closestValue = absoluteBlockPos.add(0, 0, 1);
        }

        Vector3 right = absoluteBlockPos.add(0, 0, -1);
        if (Intersection.intersect((int) right.x, (int) right.y, (int) right.z, camera, result) && result.x < closestResult) {
            closestResult = result.x;
            closestValue = absoluteBlockPos.add(0, 0, -1);
        }

        Vector3 up = absoluteBlockPos.add(0, 1, 0);
        if (Intersection.intersect((int) up.x, (int) up.y, (int) up.z, camera, result) && result.x < closestResult) {
            closestResult = result.x;
            closestValue = absoluteBlockPos.add(0, 1, 0);
        }

        Vector3 down = absoluteBlockPos.add(0, -1, 0);
        if (Intersection.intersect((int) down.x, (int) down.y, (int) down.z, camera, result) && result.x < closestResult) {
            closestResult = result.x;
            closestValue = absoluteBlockPos.add(0, -1, 0);
        }
        return closestValue;
    }
}
