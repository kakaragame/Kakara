package org.kakara.engine.math;

import org.joml.Vector3f;

/**
 * A nice vector3 library.
 */
public class Vector3 {

    public float x, y, z;
    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3f vec){
        this(vec.x, vec.y, vec.z);
    }

    /**
     * Clone the vector
     * @return The cloned vector.
     */
    public Vector3 clone(){
        return new Vector3(x, y, z);
    }

    public Vector3 subtract(Vector3 other){
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    public Vector3 subtract(float x, float y, float z){
        return new Vector3(this.x - x, this.y - y, this.z - z);
    }

    public Vector3 add(Vector3 other){
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 add(float x, float y, float z){
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Convert the vector to the JOML version.
     * @return
     */
    public Vector3f toJoml(){
        return new Vector3f(this.x, this.y, this.z);
    }
}
