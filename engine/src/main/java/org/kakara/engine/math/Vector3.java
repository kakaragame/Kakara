package org.kakara.engine.math;

import org.joml.Vector3f;

/**
 * A math class to provide a nice representation of a vector.
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

    /**
     * Subtract a vector
     * @param other The other vector
     * @return The vector after the operation.
     */
    public Vector3 subtract(Vector3 other){
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Subtract a vector
     * @param x x value
     * @param y y value
     * @param z z value
     * @return The vector after the operation.
     */
    public Vector3 subtract(float x, float y, float z){
        return new Vector3(this.x - x, this.y - y, this.z - z);
    }

    /**
     * Add a vector
     * @param other The other vector
     * @return The vector after the operation.
     */
    public Vector3 add(Vector3 other){
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    /**
     * Add a vector
     * @param x x value
     * @param y y value
     * @param z z value
     * @return The vector after the operation.
     */
    public Vector3 add(float x, float y, float z){
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Multiply a vector by a constant
     * @param v The constant
     * @return The vector after the operation.
     */
    public Vector3 multiply(float v){
        return new Vector3(this.x * v, this.y * v, this.z * v);
    }

    /**
     * Divide a vector by a constant
     * @param v The constant
     * @return The vector after the operation.
     */
    public Vector3 divide(float v){
        return new Vector3(this.x / v, this.y / v, this.z / v);
    }

    /**
     * Convert the vector to the JOML version.
     * @return
     */
    public Vector3f toJoml(){
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public String toString(){
        return "{" + this.x + ", " + this.y + ", " + this.z + "}";
    }

    /**
     * Compare one vector to another.
     * @param other The other vector
     * @return If this vector is greater than the other one.
     */
    public boolean greaterThan(Vector3 other){
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2)) > Math.sqrt(Math.pow(other.x, 2) + Math.pow(other.y, 2) + Math.pow(other.z, 2));
    }

    /**
     * Compare one vector to another using the compare point is the point of comparision.
     * @param other The vector to compare
     * @param comparePoint The point at which the vectors will be compared to.
     * @return If this vector is greater than the other one.
     */
    public boolean greaterThan(Vector3 other, Vector3 comparePoint){
        return KMath.distance(this, comparePoint) > KMath.distance(other, comparePoint);
    }
}
