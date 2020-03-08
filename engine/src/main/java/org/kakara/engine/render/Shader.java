package org.kakara.engine.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.kakara.engine.item.Material;
import org.kakara.engine.lighting.DirectionalLighting;
import org.kakara.engine.lighting.LightHandler;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.weather.Fog;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public Shader() throws Exception {
        programId = glCreateProgram();
        uniforms = new HashMap<>();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if(uniformLocation < 0){
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value){
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, int value){
        glUniform1i(uniforms.get(uniformName), value);
    }

    /*
     *
     * Brand new lighting junk
     */

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        for(int i = 0; i < 5; i++)
            createUniform(uniformName + ".overlayTextures[" + i + "]");
        createUniform(uniformName + ".numberOfOverlays");
        createUniform(uniformName + ".specularColor");
        createUniform(uniformName + ".hasSpecularText");
        createUniform(uniformName + ".reflectance");
    }

    public void createDirectinalLightUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
    }

    public void createPointLightUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".quadratic");
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
    }

    public void createPointLightsUniform(String uniformName) throws Exception{
        for(int i = 0; i < LightHandler.MAX_POINT_LIGHTS; i++){
            createUniform(uniformName + "[" + i + "].position");
            createUniform(uniformName + "[" + i + "].constant");
            createUniform(uniformName + "[" + i + "].linear");
            createUniform(uniformName + "[" + i + "].quadratic");
            createUniform(uniformName + "[" + i + "].ambient");
            createUniform(uniformName + "[" + i + "].diffuse");
            createUniform(uniformName + "[" + i + "].specular");
        }
    }

    public void createSpotLightsUniform(String uniformName) throws Exception{
        for(int i = 0; i < LightHandler.MAX_SPOT_LIGHTS; i++){
            createUniform(uniformName + "[" + i + "].position");
            createUniform(uniformName + "[" + i + "].direction");
            createUniform(uniformName + "[" + i + "].constant");
            createUniform(uniformName + "[" + i + "].linear");
            createUniform(uniformName + "[" + i + "].quadratic");
            createUniform(uniformName + "[" + i + "].ambient");
            createUniform(uniformName + "[" + i + "].diffuse");
            createUniform(uniformName + "[" + i + "].specular");
            createUniform(uniformName + "[" + i + "].cutOff");
            createUniform(uniformName + "[" + i + "].outerCutOff");
        }
    }


    public void setUniform(String uniformName, Material material){
        setUniform(uniformName + ".diffuse", 0);
        setUniform(uniformName + ".specular", 2);
        setUniform(uniformName + ".overlayTextures[0]", 3);
        setUniform(uniformName + ".overlayTextures[1]", 4);
        setUniform(uniformName + ".overlayTextures[2]", 5);
        setUniform(uniformName + ".overlayTextures[3]", 6);
        setUniform(uniformName + ".overlayTextures[4]", 7);
        setUniform(uniformName + ".numberOfOverlays", material.getOverlayTextures().size());
        setUniform(uniformName + ".specularColor", material.getSpecularColor());
        setUniform(uniformName + ".hasSpecularText", material.getSpecularMap() == null ? 0 : 1);
        setUniform(uniformName + ".reflectance", material.getShininess());
        // TODO add in support for overlay textures.
    }

    public void setUniform(String uniformName, DirectionalLighting dirLight){
        setUniform(uniformName + ".direction", dirLight.getDirection().toJoml());
        setUniform(uniformName + ".ambient", dirLight.getAmbient().toJoml());
        setUniform(uniformName + ".diffuse", dirLight.getDiffuse().toJoml());
        setUniform(uniformName + ".specular", dirLight.getSpecular().toJoml());
    }

    public void setUniform(String uniformName, PointLight pt){
        setUniform(uniformName + ".position", pt.getPosition().toJoml());
        setUniform(uniformName + ".constant", pt.getConstant());
        setUniform(uniformName + ".linear", pt.getLinear());
        setUniform(uniformName + ".quadratic", pt.getQuadratic());
        setUniform(uniformName + ".ambient", pt.getAmbient().toJoml());
        setUniform(uniformName + ".diffuse", pt.getDiffuse().toJoml());
        setUniform(uniformName + ".specular", pt.getSpecular().toJoml());
    }

    public void setPointLightUniform(String uniformName, List<PointLight> pts){
        int i = 0;
        for(PointLight pt : pts){
            setUniform(uniformName + "[" + i + "].position", pt.getPosition().toJoml());
            setUniform(uniformName + "[" + i + "].constant", pt.getConstant());
            setUniform(uniformName + "[" + i + "].linear", pt.getLinear());
            setUniform(uniformName + "[" + i + "].quadratic", pt.getQuadratic());
            setUniform(uniformName + "[" + i + "].ambient", pt.getAmbient().toJoml());
            setUniform(uniformName + "[" + i + "].diffuse", pt.getDiffuse().toJoml());
            setUniform(uniformName + "[" + i + "].specular", pt.getSpecular().toJoml());
            i++;
        }
    }

    public void setSpotLightUniform(String uniformName, List<SpotLight> spt){
        int i = 0;
        for(SpotLight pt : spt){
            setUniform(uniformName + "[" + i + "].position", pt.getPosition().toJoml());
            setUniform(uniformName + "[" + i + "].direction", pt.getDirection().toJoml());
            setUniform(uniformName + "[" + i + "].constant", pt.getConstant());
            setUniform(uniformName + "[" + i + "].linear", pt.getLinear());
            setUniform(uniformName + "[" + i + "].quadratic", pt.getQuadratic());
            setUniform(uniformName + "[" + i + "].ambient", pt.getAmbient().toJoml());
            setUniform(uniformName + "[" + i + "].diffuse", pt.getDiffuse().toJoml());
            setUniform(uniformName + "[" + i + "].specular", pt.getSpecular().toJoml());
            setUniform(uniformName + "[" + i + "].cutOff", pt.getCutOff());
            setUniform(uniformName + "[" + i + "].outerCutOff", pt.getOuterCutOff());
            i++;
        }
    }

    public void createFogUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".activeFog");
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".density");
    }

    public void setUniform(String uniformName, Fog fog) {
        setUniform(uniformName + ".activeFog", fog.isActive() ? 1 : 0);
        setUniform(uniformName + ".color", fog.getColor().toJoml());
        setUniform(uniformName + ".density", fog.getDensity());
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
