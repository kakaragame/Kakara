package org.kakara.engine.renderobjects.renderlayouts.types;

public interface Indices {
    int[] getFront(int startingIndex);
    int[] getBack(int startingIndex);
    int[] getTop(int startingIndex);
    int[] getBottom(int startingIndex);
    int[] getRight(int startingIndex);
    int[] getLeft(int startingIndex);
}
