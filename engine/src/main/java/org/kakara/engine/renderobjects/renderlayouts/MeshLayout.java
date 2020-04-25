package org.kakara.engine.renderobjects.renderlayouts;

public interface MeshLayout {
    float[] getVertex();
    float[] getTextCoords();
    float[] getNormals();
    int[] getIndices();
}
