package org.kakara.engine.renderobjects.renderlayouts;

import org.kakara.engine.engine.CubeData;

public class BlockLayout implements Layout {

    @Override
    public float[] getVertex() {
        return CubeData.vertex;
    }

    @Override
    public float[] getTextureCords() {
        return CubeData.texture;
    }

    @Override
    public float[] getNormal() {
        return CubeData.normal;
    }

    @Override
    public int[] getIndices() {
        return CubeData.indices;
    }
}
