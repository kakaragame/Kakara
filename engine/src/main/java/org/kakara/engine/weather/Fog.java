package org.kakara.engine.weather;

import org.kakara.engine.math.Vector3;

public class Fog {

    private boolean active;

    private Vector3 color;

    private float density;

    public static Fog NOFOG = new Fog();

    public Fog() {
        active = false;
        this.color = new Vector3(0, 0, 0);
        this.density = 0;
    }

    public Fog(boolean active, Vector3 color, float density) {
        this.color = color;
        this.density = density;
        this.active = active;
    }

    public void setColor(Vector3 color){
        this.color = color;
    }

    public Vector3 getColor(){
        return color;
    }

    public void setDensity(float density){
        this.density = density;
    }

    public float getDensity(){
        return density;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean isActive(){
        return active;
    }
}
