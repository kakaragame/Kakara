package org.kakara.engine.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.kakara.engine.Camera;
import org.kakara.engine.item.GameItem;

public class Transformation {
    private final Matrix4f projectionMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;

    private final Matrix4f modelMatrix;

    private final Matrix4f modelLightMatrix;
    private final Matrix4f modelLightViewMatrix;
    private final Matrix4f lightViewMatrix;
    private final Matrix4f orthoProjMatrix;
    private final Matrix4f ortho2DMatrix;
    private final Matrix4f orthoModelMatrix;


    public Transformation() {
        modelViewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelLightMatrix = new Matrix4f();
        modelLightViewMatrix = new Matrix4f();
        orthoProjMatrix = new Matrix4f();
        ortho2DMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();
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
//    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
//        float aspectRatio = width / height;
//        projectionMatrix.identity();
//        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
//        return projectionMatrix;
//    }

    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f updateViewMatrix(Camera camera) {
        return updateGenericViewMatrix(camera.getPosition().toJoml(), camera.getRotation().toJoml(), viewMatrix);
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

    public final Matrix4f getOrthoProjectionMatrix() {
        return orthoProjMatrix;
    }

    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
        orthoProjMatrix.identity();
        orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
        return orthoProjMatrix;
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public void setLightViewMatrix(Matrix4f lightViewMatrix) {
        this.lightViewMatrix.set(lightViewMatrix);
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // First do the rotation so camera rotates over its position
        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public final Matrix4f getOrtho2DProjectionMatrix(float left, float right, float bottom, float top) {
        ortho2DMatrix.identity();
        ortho2DMatrix.setOrtho2D(left, right, bottom, top);
        return ortho2DMatrix;
    }
    public Matrix4f buildModelViewMatrix(GameItem gameItem, Matrix4f matrix) {
        Quaternionf rotation = gameItem.getRotation();
        modelMatrix.translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
                gameItem.getScale(), gameItem.getScale());
        modelViewMatrix.set(matrix);
        return modelViewMatrix.mul(modelMatrix);
    }


    public Matrix4f buildModelLightViewMatrix(GameItem gameItem, Matrix4f matrix) {
        Quaternionf rotation = gameItem.getRotation();
        modelLightMatrix.translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
                gameItem.getScale(), gameItem.getScale());
        modelLightViewMatrix.set(matrix);
        return modelLightViewMatrix.mul(modelLightMatrix);
    }

    public Matrix4f buildOrthoProjModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        Quaternionf rotation = gameItem.getRotation();
        modelMatrix.identity().translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
                gameItem.getScale(), gameItem.getScale());
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }


}
