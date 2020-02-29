package org.kakara.engine.ui;

/**
 * A class to store color values.
 */
public class RGBA {
    public int r;
    public int g;
    public int b;
    public float a;

    public RGBA(int r, int g, int b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public RGBA(){
        this(255, 255, 255, 1);
    }

    public RGBA setRGBA(int r, int g, int b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    /**
     * Convert Alpha value to one that will be accepted by nanovg.
     * @return
     */
    public int aToNano(){
        return (int) Math.floor(a * 255);
    }
}
