package org.kakara.engine.render;

import org.kakara.engine.math.Vector3;

import static org.lwjgl.opengl.GL11.glVertex3d;

/**
 * Used to render debug displays.
 * NOT TO BE USED IN PRODUCTION BUILD. REMOVE BEFORE COMPILATION.
 */
public class DebugRender {
    public static float[] getPositions(Vector3 point1, Vector3 point2){
        float[] positions = {

                point1.x, point1.y + point2.y, point1.z,
                point1.x + point2.x, point1.y + point2.y, point1.z,
                point1.x, point1.y + point2.y, point1.z + point2.z,
                point1.x + point2.x, point1.y + point2.y, point1.z + point2.z,

                point1.x, point1.y, point1.z,
                point1.x + point2.x, point1.y, point1.z,
            point1.x, point1.y, point1.z + point2.z,
            point1.x + point2.x, point1.y, point1.z + point2.z,
            // bottom

        };
        return positions;
    }

    public static int[] getIndices(){
        int[] indices = {
            // front
            0, 1, 2,
                    2, 3, 0,
                    // right
                    1, 5, 6,
                    6, 2, 1,
                    // back
                    7, 6, 5,
                    5, 4, 7,
                    // left
                    4, 0, 3,
                    3, 7, 4,
                    // bottom
                    4, 5, 1,
                    1, 0, 4,
                    // top
                    3, 2, 6,
                    6, 7, 3
        };
        return indices;
    }
}
