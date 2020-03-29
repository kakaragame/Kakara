package org.kakara.engine.renderobjects.renderlayouts;

public interface Layout {
    float[] getVertex();
    float[] getTextureCords();
    float[] getNormal();
    int[] getIndices();
}
