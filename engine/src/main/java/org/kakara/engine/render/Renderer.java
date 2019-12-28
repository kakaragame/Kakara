package org.kakara.engine.render;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import org.joml.Vector3f;
import org.kakara.engine.Camera;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.gui.Window;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.lighting.DirectionalLighting;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.utils.Utils;

import java.util.List;

public class Renderer {
    private Transformation transformation;

    public Renderer() {
        transformation = new Transformation();
    }

    private Shader shaderProgram;

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.0f;

    private Matrix4f projectionMatrix;


    public void init(Window window) throws Exception {
        shaderProgram = new Shader();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        float aspectRatio = (float) window.getWidth() / window.getHeight();
        projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
//        shaderProgram.createUniform("texture_sampler");

        /*
         * Setup uniforms for lighting
         */
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createDirectinalLightUniform("dirLight");
        shaderProgram.createPointLightUniform("pointLights[0]");
        shaderProgram.createUniform("viewPos");


    }

    public void render(Window window, List<GameItem> gameObjects, Camera camera){
        clear();

        if(window.isResized()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        shaderProgram.bind();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Set Lighting Uniforms
        LightHandler lh = GameHandler.getInstance().getLightHandler();
        shaderProgram.setUniform("dirLight", lh.getDirectionalLight());
        shaderProgram.setUniform("pointLights[0]", lh.getPointLight(0));
        shaderProgram.setUniform("viewPos", camera.getPosition().toJoml());



//        shaderProgram.setUniform("texture_sampler", 0);

        for(GameItem gameObject : gameObjects) {
            // Set world matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameObject, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            shaderProgram.setUniform("material", gameObject.getMesh().getMaterial());
            // Render the mes for this game item
            gameObject.render(shaderProgram);
            /*
                Below is the code for the debug mode for the box collider.
             */
            if(gameObject.getCollider() instanceof BoxCollider){
                Matrix4f colliderViewMatrix = new Matrix4f().identity().scale(0.3f).translate(gameObject.getCollider().getAbsolutePoint1().subtract(1, 1, 1).divide(1-gameObject.getScale()).toJoml());
                Matrix4f viewCurr = new Matrix4f(viewMatrix);
                Matrix4f curColliderMatrix = viewCurr.mul(colliderViewMatrix);
                shaderProgram.setUniform("modelViewMatrix", curColliderMatrix);
                ((BoxCollider) gameObject.getCollider()).render();
            }
        }

        shaderProgram.unbind();

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
