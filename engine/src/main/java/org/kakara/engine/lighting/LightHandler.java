package org.kakara.engine.lighting;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the lighting for the engine.
 * (Planned replacement by the scene api).
 */
public class LightHandler {

    private List<PointLight> pointLights;
    private DirectionalLighting directionalLight;
    public LightHandler(){
        this.pointLights = new ArrayList<>();
        this.directionalLight = new DirectionalLighting();
    }

    public void addPointLight(PointLight pl){
        pointLights.add(pl);
    }

    public void removePointLight(PointLight pl){
        pointLights.remove(pl);
    }

    public PointLight getPointLight(int id){
        return pointLights.get(id);
    }

    public List<PointLight> getPointLights(){
        return pointLights;
    }

    public DirectionalLighting getDirectionalLight(){
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLighting dl){
        this.directionalLight = dl;
    }

}
