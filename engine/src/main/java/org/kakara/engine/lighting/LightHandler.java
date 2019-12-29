package org.kakara.engine.lighting;

import org.kakara.engine.GameHandler;
import org.lwjgl.vulkan.AMDRasterizationOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles the lighting for the engine.
 */
public class LightHandler {

    public static final int MAX_POINT_LIGHTS = 20;
    public static final int MAX_SPOT_LIGHTS = 10;

    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;
    private DirectionalLighting directionalLight;
    public LightHandler(){
        this.pointLights = new ArrayList<>();
        this.spotLights = new ArrayList<>();
        this.directionalLight = new DirectionalLighting();
    }

    public void addPointLight(PointLight pl){
        pointLights.add(pl);
    }

    public void removePointLight(PointLight pl){
        pointLights.remove(pl);
    }

    public void addSpotLight(SpotLight sl){
        spotLights.add(sl);
    }

    public void removeSpotLight(SpotLight sl){
        spotLights.remove(sl);
    }

    /**
     * Gets the list of point lights that are going to be displayed.
     * @return The list of displayed point lights.
     */
    public List<PointLight> getDisplayPointLights(){
        if(pointLights.size() <= MAX_POINT_LIGHTS){
            return pointLights;
        }
        List<PointLight> lights = new ArrayList<>(pointLights);
        Collections.sort(lights);
        lights.subList(MAX_POINT_LIGHTS, lights.size()).clear();
        return lights;
    }

    /**
     * Gets the list of point lights that are going to be displayed.
     * @return The list of displayed point lights.
     */
    public List<SpotLight> getDisplaySpotLights(){
        if(spotLights.size() <= MAX_SPOT_LIGHTS){
            return spotLights;
        }
        List<SpotLight> lights = new ArrayList<>(spotLights);
        Collections.sort(lights);
        lights.subList(MAX_SPOT_LIGHTS, lights.size()).clear();
        return lights;
    }

    public PointLight getPointLight(int id){
        return pointLights.get(id);
    }

    public List<PointLight> getPointLights(){
        return pointLights;
    }

    public SpotLight getSpotLight(int id){
        return spotLights.get(id);
    }

    public List<SpotLight> getSpotLights(){
        return spotLights;
    }

    public DirectionalLighting getDirectionalLight(){
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLighting dl){
        this.directionalLight = dl;
    }

}
