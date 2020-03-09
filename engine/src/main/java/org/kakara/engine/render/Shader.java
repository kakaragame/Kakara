package org.kakara.engine.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.kakara.engine.item.Material;
import org.kakara.engine.lighting.DirectionalLight;
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

    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");

//        for(int i = 0; i < 5; i++)
//            createUniform(uniformName + ".overlayTextures[" + i + "]");
//        createUniform(uniformName + ".numberOfOverlays");
    }
    /*
        Lighting Set Uniform
     */

    public void setPointLightsUniform(String uniformName, List<PointLight> pointLights) {
        int numLights = pointLights != null ? pointLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(uniformName, pointLights.get(i), i);
        }
    }
    public void setUniform(String uniformName, PointLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".color", pointLight.getColor().toJoml());
        setUniform(uniformName + ".position", pointLight.getPosition().toJoml());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.getConstant());
        setUniform(uniformName + ".att.linear", att.getLinear());
        setUniform(uniformName + ".att.exponent", att.getExponent());
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColor());
        setUniform(uniformName + ".diffuse", material.getDiffuseColor());
        setUniform(uniformName + ".specular", material.getSpecularColor());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void setUniform(String uniformName, DirectionalLight dirLight){
        setUniform(uniformName + ".color", dirLight.getColor().toJoml());
        setUniform(uniformName + ".direction", dirLight.getDirection().toJoml());
        setUniform(uniformName + ".intensity", dirLight.getIntensity());
    }

    public void setUniform(String uniformName, List<SpotLight> spotLights) {
        int numLights = spotLights != null ? spotLights.size() : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(uniformName, spotLights.get(i), i);
        }
    }
    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pl", spotLight.getPointLight());
        setUniform(uniformName + ".conedir", spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff", spotLight.getCutOff());
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
