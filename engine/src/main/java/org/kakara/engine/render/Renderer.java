package org.kakara.engine.render;

import org.joml.Matrix4f;
import org.kakara.engine.Camera;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.gui.Window;
import org.kakara.engine.item.Collidable;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.MeshGameItem;
import org.kakara.engine.item.SkyBox;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.utils.Utils;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private Transformation transformation;

    public Renderer() {
        transformation = new Transformation();
    }

    private Shader shaderProgram;
    private Shader skyBoxShaderProgram;

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.0f;


    /**
     * Setup shaders
     *
     * @throws Exception
     */
    public void init() throws Exception {
        shaderProgram = new Shader();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelMatrix");
        shaderProgram.createUniform("viewMatrix");
        /*
         * Setup uniforms for lighting
         */
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createDirectinalLightUniform("dirLight");
        shaderProgram.createPointLightsUniform("pointLights");
        shaderProgram.createSpotLightsUniform("spotLights");
        shaderProgram.createUniform("viewPos");

        setupSkyBoxShader();
    }

    public void render(Window window, List<GameItem> gameObjects, Camera camera) {
        System.out.println("Start Render");
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        if(gameObjects.size() < 1)
            return;

        shaderProgram.bind();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        shaderProgram.setUniform("viewMatrix", viewMatrix);

        // Set Lighting Uniforms
        LightHandler lh = GameHandler.getInstance().getSceneManager().getCurrentScene().getLightHandler();
        shaderProgram.setUniform("dirLight", lh.getDirectionalLight());
        shaderProgram.setPointLightUniform("pointLights", lh.getDisplayPointLights());
        shaderProgram.setSpotLightUniform("spotLights", lh.getDisplaySpotLights());
        shaderProgram.setUniform("viewPos", camera.getPosition().toJoml());

        for (GameItem gameObject : gameObjects) {
            // Set world matrix for this item
            Matrix4f pureModelMatrix = transformation.getModelMatrix(gameObject);
            shaderProgram.setUniform("modelMatrix", pureModelMatrix);
            if (gameObject instanceof MeshGameItem) {
                shaderProgram.setUniform("material", ((MeshGameItem) gameObject).getMesh().getMaterial());

            }
            // Render the meshes for this game item
            gameObject.render();
        }

        shaderProgram.unbind();
        System.out.println("end renderer");

    }

    /**
     * Render the skybox
     * @param window The window
     * @param camera The game
     * @param scene The current scene
     */
    public void renderSkyBox(Window window, Camera camera, Scene scene){
        System.out.println("start skybox");
        skyBoxShaderProgram.bind();
        skyBoxShaderProgram.setUniform("texture_sampler", 0);
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        //TODO remove model view matrix
        Matrix4f modelViewMatrix = transformation.getModelViewMatrix(scene.getSkyBox(), viewMatrix);
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", scene.getLightHandler().getDirectionalLight().getAmbient().toJoml());

        scene.getSkyBox().getMesh().render();

        skyBoxShaderProgram.unbind();
        System.out.println("end skybox");

    }

    /**
     * Setup the skybox shader.
     * @throws Exception
     */
    private void setupSkyBoxShader() throws Exception{
        skyBoxShaderProgram = new Shader();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/skyboxVertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/skyboxFragment.fs"));
        skyBoxShaderProgram.link();

        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }


    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        if(skyBoxShaderProgram != null){
            skyBoxShaderProgram.cleanup();
        }
    }

    public Transformation getTransformation() {
        return transformation;
    }
}
