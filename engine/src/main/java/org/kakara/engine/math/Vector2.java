package org.kakara.engine.math;

public class Vector2 {
    public float x, y;
    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Vector2(Vector2 vec){
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vector2 add(float x, float y){
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 vec){
        return add(vec.x, vec.y);
    }

    public Vector2 subtract(float x, float y){
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 subtract(Vector2 vec){
        return subtract(vec.x, vec.y);
    }

    public Vector2 clone(){
        return new Vector2(this);
    }
}
