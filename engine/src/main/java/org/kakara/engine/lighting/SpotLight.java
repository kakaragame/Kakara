package org.kakara.engine.lighting;

import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;

/**
 * Spot / Beam based lighting.
 */
public class SpotLight implements Comparable<SpotLight> {
    private PointLight pointLight;

    private Vector3f coneDirection;

    private float cutOff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        setCutOffAngle(cutOffAngle);
    }

    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.getPointLight()),
                new Vector3f(spotLight.getConeDirection()),
                0);
        setCutOff(spotLight.getCutOff());
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public final void setCutOffAngle(float cutOffAngle) {
        this.setCutOff((float)Math.cos(Math.toRadians(cutOffAngle)));
    }

    @Override
    public int compareTo(SpotLight o) {
        Vector3 cameraPos = GameHandler.getInstance().getCamera().getPosition();
        return Math.round(KMath.distance(cameraPos, getPointLight().getPosition()) - KMath.distance(cameraPos, o.getPointLight().getPosition()));
    }

}
