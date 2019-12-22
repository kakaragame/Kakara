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


    public Transformation() {
        modelViewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Quaternionf rotation = gameItem.getRotation();
//        modelViewMatrix.identity().translate(gameItem.getPosition().toJoml()).
//                rotateX((float)Math.toRadians(-rotation.x)).
//                rotateY((float)Math.toRadians(-rotation.y)).
//                rotateZ((float)Math.toRadians(-rotation.z)).
//                scale(gameItem.getScale());
        // I think this will work for a bit.
        modelViewMatrix.translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
                gameItem.getScale(), gameItem.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition().toJoml();
        Vector3f rotation = camera.getRotation().toJoml();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }
}
