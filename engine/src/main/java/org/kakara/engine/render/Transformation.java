package org.kakara.engine.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.kakara.engine.Camera;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.Vector3;

public class Transformation {
    private final Matrix4f projectionMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;


    public Transformation() {
        modelViewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    /**
     * Get the project matrix
     *
     * @param fov
     * @param width
     * @param height
     * @param zNear
     * @param zFar
     * @return
     */
    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    /**
     * Get the matrix for the model position, rotation, and scale.
     *
     * @param gameItem The game item to get the matrix for
     * @return The matrix.
     */
    public Matrix4f getModelMatrix(GameItem gameItem) {
        Quaternionf rotation = gameItem.getRotation();
        modelViewMatrix.translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
                gameItem.getScale(), gameItem.getScale());
        return modelViewMatrix;
    }

    /**
     * Get the model * view matrix.
     *
     * @param gameItem   The game item.
     * @param viewMatrix The view matrix
     * @return The model * view matrix.
     * @deprecated This method is outdated. Use getModelMatrix and getViewMatrix instead.
     */
    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Quaternionf rotation = gameItem.getRotation();
        modelViewMatrix.translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
                gameItem.getScale(), gameItem.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    /**
     * Get the view matrix for the camera.
     *
     * @param camera The camera.
     * @return The view matrix.
     */
    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition().toJoml();
        Vector3f rotation = camera.getRotation().toJoml();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }


}
