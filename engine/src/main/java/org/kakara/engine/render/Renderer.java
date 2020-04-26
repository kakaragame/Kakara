package org.kakara.engine.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.kakara.engine.Camera;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gui.Window;
import org.kakara.engine.item.*;
import org.kakara.engine.item.Particles.ParticleEmitter;
import org.kakara.engine.lighting.*;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.scene.Scene;
import org.kakara.engine.utils.Utils;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {
    private Transformation transformation;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    private Shader shaderProgram;
    private Shader skyBoxShaderProgram;
    private Shader depthShaderProgram;
    private Shader particleShaderProgram;
    private Shader chunkShaderProgram;

    private ShadowMap shadowMap;

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.0f;

    private final float specularPower;


    /**
     * Setup shaders
     *
     * @throws Exception
     */
    public void init() throws Exception {
        shadowMap = new ShadowMap();

        setupSceneShader();
        setupLightingShader();
        setupSkyBoxShader();
        setupDepthShader();
        setupParticleShader();
        setupChunkShader();
    }

    public void render(Window window, Camera camera, Scene scene) {
        clear();

        renderDepthMap(window, camera, scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        scene.getCamera().updateViewMatrix();


        renderScene(window, camera, scene);
        renderChunk(window, camera, scene, false);
        renderParticles(window, camera, scene);

    }

    /**
     * Render a chunk
     * @param window
     * @param camera
     * @param scene
     */
    public void renderChunk(Window window, Camera camera, Scene scene, boolean depthMap){
        if(!(scene instanceof AbstractGameScene)) return;
        AbstractGameScene ags = (AbstractGameScene) scene;
        List<RenderChunk> renderChunks = ags.getChunkHandler().getRenderChunkList();
        if(renderChunks == null) return;
        chunkShaderProgram.bind();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        chunkShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        chunkShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();

        // Render Lighting
        LightHandler lh = GameHandler.getInstance().getSceneManager().getCurrentScene().getLightHandler();
        renderLights(viewMatrix, lh.getAmbientLight().toVector(), lh.getDisplayPointLights(), lh.getDisplaySpotLights(), lh.getDirectionalLight(), chunkShaderProgram);

        chunkShaderProgram.setUniform("fog", scene.getFog());
        chunkShaderProgram.setUniform("shadowMap", 2);
        chunkShaderProgram.setUniform("textureAtlas", 0);
        chunkShaderProgram.setUniform("reflectance", 1f);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, ags.getTextureAtlas().getTexture().getId());
        glDisable(GL_CULL_FACE);
        for(RenderChunk renderChunk : renderChunks){
            Matrix4f modelMatrix = transformation.buildModelMatrix(renderChunk);

            if (!depthMap) {
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                chunkShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            }
            Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
            chunkShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);

            renderChunk.render();
        }
        glEnable(GL_CULL_FACE);


        glBindTexture(GL_TEXTURE_2D, 0);


        chunkShaderProgram.unbind();
    }

    public void renderScene(Window window, Camera camera, Scene scene){
        shaderProgram.bind();
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        shaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();

        // Render Lighting
        LightHandler lh = GameHandler.getInstance().getSceneManager().getCurrentScene().getLightHandler();
        renderLights(viewMatrix, lh.getAmbientLight().toVector(), lh.getDisplayPointLights(), lh.getDisplaySpotLights(), lh.getDirectionalLight(), shaderProgram);

        shaderProgram.setUniform("fog", scene.getFog());
        shaderProgram.setUniform("shadowMap", 2);

        renderNonInstancedMeshes(scene, false, shaderProgram, viewMatrix, lightViewMatrix);

        renderInstancedMeshes(scene, false, shaderProgram, viewMatrix, lightViewMatrix);

        shaderProgram.unbind();
    }

    private void renderNonInstancedMeshes(Scene scene, boolean depthMap, Shader shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        shaderProgram.setUniform("isInstanced", 0);

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getItemHandler().getNonInstancedMeshMap();
        for (Mesh mesh : mapMeshes.keySet()) {
            if (!depthMap) {
                shader.setUniform("material", mesh.getMaterial());
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            }

            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                        shaderProgram.setUniform("selectedNonInstanced", ((MeshGameItem) gameItem).isSelected() ? 1f : 0f);
                        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                        if (!depthMap) {
                            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                            shaderProgram.setUniform("modelViewNonInstancedMatrix", modelViewMatrix);
                        }
                        Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
                        shaderProgram.setUniform("modelLightViewNonInstancedMatrix", modelLightViewMatrix);
                    }
            );
        }
    }

    private void renderInstancedMeshes(Scene scene, boolean depthMap, Shader shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        shaderProgram.setUniform("isInstanced", 1);

        // Render each mesh with the associated game Items
        Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getItemHandler().getInstancedMeshMap();
        for (InstancedMesh mesh : mapMeshes.keySet()) {
            if (!depthMap) {
                shaderProgram.setUniform("material", mesh.getMaterial());
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            }
            mesh.renderListInstanced(mapMeshes.get(mesh), depthMap, transformation, viewMatrix, lightViewMatrix);
        }
    }

    private void renderDepthMap(Window window, Camera camera, Scene scene){
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getLightHandler().getDirectionalLight();
        Vector3f lightDirection = light.getDirection().toJoml();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);
        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);


        renderNonInstancedMeshes(scene, true, depthShaderProgram, null, lightViewMatrix);

        renderInstancedMeshes(scene, true, depthShaderProgram, null, lightViewMatrix);
        renderChunk(window, camera, scene, true);

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Handles the rendering of lights.
     */
    private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, List<PointLight> pointLightList, List<SpotLight> spotLightList, DirectionalLight directionalLight, Shader program){
        program.setUniform("ambientLight", ambientLight);
        program.setUniform("specularPower", specularPower);

        // Process Point Lights
        int numLights = pointLightList != null ? pointLightList.size() : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList.get(i));
            Vector3f lightPos = currPointLight.getPosition().toJoml();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            currPointLight.setPosition(lightPos.x, lightPos.y, lightPos.z);
            program.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        numLights = spotLightList != null ? spotLightList.size() : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList.get(i));
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
            Vector3f lightPos = currSpotLight.getPointLight().getPosition().toJoml();

            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            program.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection().toJoml(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3(dir.x, dir.y, dir.z));
        program.setUniform("directionalLight", currDirLight);

    }

    /**
     * Render the skybox
     * @param window The window
     * @param camera The game
     * @param scene The current scene
     */
    public void renderSkyBox(Window window, Camera camera, Scene scene){
        skyBoxShaderProgram.bind();
        skyBoxShaderProgram.setUniform("texture_sampler", 0);
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        //TODO remove model view matrix
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(scene.getSkyBox(), viewMatrix);
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", scene.getLightHandler().getSkyBoxLight().toVector());

        scene.getSkyBox().getMesh().render();

        skyBoxShaderProgram.unbind();
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        particleShaderProgram.bind();

        particleShaderProgram.setUniform("texture_sampler", 0);
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        particleShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
        List<ParticleEmitter> emitters = scene.getParticleHandler().getParticleEmitters();
        int numEmitters = emitters != null ? emitters.size() : 0;

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            ParticleEmitter emitter = emitters.get(i);
            Mesh mesh = emitter.getBaseParticle().getMesh();

            Texture text = mesh.getMaterial().getTexture();
            particleShaderProgram.setUniform("numCols", text.getNumCols());
            particleShaderProgram.setUniform("numRows", text.getNumRows());

            mesh.renderList((emitter.getParticles()), (GameItem gameItem) -> {
                        int col = gameItem.getTextPos() % text.getNumCols();
                        int row = gameItem.getTextPos() / text.getNumCols();
                        float textXOffset = (float) col / text.getNumCols();
                        float textYOffset = (float) row / text.getNumRows();
                        particleShaderProgram.setUniform("texXOffset", textXOffset);
                        particleShaderProgram.setUniform("texYOffset", textYOffset);

                        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);

                        viewMatrix.transpose3x3(modelMatrix);
                        viewMatrix.scale(gameItem.getScale());

                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                        modelViewMatrix.scale(gameItem.getScale());
                        particleShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                    }
            );
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        particleShaderProgram.unbind();
    }

    private void setupParticleShader() throws Exception {
        particleShaderProgram = new Shader();
        particleShaderProgram.createVertexShader(Utils.loadResource("/shaders/particleVertex.vs"));
        particleShaderProgram.createFragmentShader(Utils.loadResource("/shaders/particleFragment.fs"));
        particleShaderProgram.link();

        particleShaderProgram.createUniform("projectionMatrix");
        particleShaderProgram.createUniform("modelViewMatrix");
        particleShaderProgram.createUniform("texture_sampler");

        particleShaderProgram.createUniform("texXOffset");
        particleShaderProgram.createUniform("texYOffset");
        particleShaderProgram.createUniform("numCols");
        particleShaderProgram.createUniform("numRows");
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

    private void setupSceneShader() throws Exception {
        shaderProgram = new Shader();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/sceneVertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/sceneFragment.fs"));
        shaderProgram.link();
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewNonInstancedMatrix");
        shaderProgram.createUniform("modelLightViewNonInstancedMatrix");
        shaderProgram.createMaterialUniform("material");
        /*
         * Setup uniforms for lighting
         */

        shaderProgram.createFogUniform("fog");

        shaderProgram.createUniform("shadowMap");
        shaderProgram.createUniform("orthoProjectionMatrix");
        shaderProgram.createUniform("selectedNonInstanced");

        shaderProgram.createUniform("isInstanced");
    }

    private void setupChunkShader() throws Exception{
        chunkShaderProgram = new Shader();
        chunkShaderProgram.createVertexShader(Utils.loadResource("/shaders/chunkVertex.vs"));
        chunkShaderProgram.createFragmentShader(Utils.loadResource("/shaders/chunkFragment.fs"));
        chunkShaderProgram.link();

        chunkShaderProgram.createUniform("projectionMatrix");
        chunkShaderProgram.createUniform("modelViewMatrix");
        chunkShaderProgram.createUniform("modelLightViewMatrix");
//        chunkShaderProgram.createMaterialUniform("material");
        /*
         * Setup uniforms for lighting
         */

        chunkShaderProgram.createFogUniform("fog");

        chunkShaderProgram.createUniform("shadowMap");
        chunkShaderProgram.createUniform("orthoProjectionMatrix");

        chunkShaderProgram.createUniform("specularPower");
        chunkShaderProgram.createUniform("ambientLight");
        chunkShaderProgram.createPointLightListUniform("pointLights", LightHandler.MAX_POINT_LIGHTS);
        chunkShaderProgram.createSpotLightListUniform("spotLights", LightHandler.MAX_SPOT_LIGHTS);
        chunkShaderProgram.createDirectionalLightUniform("directionalLight");

        chunkShaderProgram.createUniform("reflectance");

        chunkShaderProgram.createUniform("textureAtlas");
    }

    private void setupLightingShader() throws Exception {
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightListUniform("pointLights", LightHandler.MAX_POINT_LIGHTS);
        shaderProgram.createSpotLightListUniform("spotLights", LightHandler.MAX_SPOT_LIGHTS);
        shaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new Shader();
        depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depthVertex.vs"));
        depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depthFragment.fs"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("orthoProjectionMatrix");
        depthShaderProgram.createUniform("modelLightViewMatrix");
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
        if(depthShaderProgram != null){
            depthShaderProgram.cleanup();
        }
        shadowMap.cleanup();
    }

    public Transformation getTransformation() {
        return transformation;
    }
}
