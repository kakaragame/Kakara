package org.kakara.engine.renderobjects.renderlayouts.types;

public interface Texture {
    float[] getFront(float xOffset, float yOffset, int rows);
    float[] getBack(float xOffset, float yOffset, int rows);
    float[] getTop(float xOffset, float yOffset, int rows);
    float[] getBottom(float xOffset, float yOffset, int rows);
    float[] getRight(float xOffset, float yOffset, int rows);
    float[] getLeft(float xOffset, float yOffset, int rows);
}
