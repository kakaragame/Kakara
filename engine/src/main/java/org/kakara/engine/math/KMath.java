package org.kakara.engine.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class KMath {

    /**
     * The maximum error a float could reasonably have. (Not to be used for exact calculations.) <br>
     * Use the following instead:
     * @see java.math.BigDecimal
     */
    public static final float FLOAT_MAX_ERROR = 0.00001f;
    /**
     * The minimum error a float could reasonably have. (Not to be used for exact calculations.) <br>
     * Use the following instead:
     * @see java.math.BigDecimal
     */
    public static final float FLOAT_MIN_ERROR = -0.00001f;

    public static float distance(Vector3 point1, Vector3 point2){
        return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2) + Math.pow(point2.z - point1.z, 2));
    }

    public static float distance(float x1, float y1, float z1, float x2, float y2, float z2){
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    public static float truncate(float input, int places){
        BigDecimal bd = new BigDecimal(input).setScale(places, RoundingMode.DOWN).stripTrailingZeros();
        return bd.floatValue();
    }
}
