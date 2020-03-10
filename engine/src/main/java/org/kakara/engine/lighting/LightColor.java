package org.kakara.engine.lighting;

import org.joml.Vector3f;

/**
 * Set the light color.
 */
public class LightColor {
    private int red;
    private int green;
    private int blue;

    public LightColor(int r, int g, int b){
        red = (r > 255 || r < 0) ? 255 : r;
        green = (g > 255 || g < 0) ? 255 : g;
        blue = (b > 255 || b < 0) ? 255 : b;
    }

    public void setRed(int r){
        red = (r > 255 || r < 0) ? 255 : r;
    }

    public void setGreen(int g){
        green = (g > 255 || g < 0) ? 255 : g;
    }

    public void setBlue(int b){
        blue = (b > 255 || b < 0) ? 255 : b;
    }

    public int getRed(){
        return red;
    }

    public int getGreen(){
        return green;
    }

    public int getBlue(){
        return blue;
    }

    public Vector3f toVector(){
        return new Vector3f((float)red/(float)255, (float)green/(float)255, (float)blue/(float)255);
    }
}
