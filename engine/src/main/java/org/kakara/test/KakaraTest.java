package org.kakara.test;

import org.kakara.engine.GameEngine;
import org.kakara.engine.IGame;
import org.kakara.engine.objects.GameObject;
import org.kakara.engine.render.Mesh;

public class KakaraTest implements IGame {

    private GameObject obj;
    private GameEngine gInst;

    @Override
    public void start(GameEngine eng) {
        gInst = eng;
        float[] positions = new float[]{
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
        };

        Mesh mesh = new Mesh(positions, colours, indices);
        GameObject obj = new GameObject(mesh);
        obj.setPosition(0, 0, -5);
        this.obj = obj;
        gInst.getObjectHandler().addObject(obj);
    }

    @Override
    public void update() {
        float rotation = obj.getRotation().z + 1.5f;
        if ( rotation > 360 ) {
            rotation = 0;
        }
        obj.setRotation(0, 0, rotation);
    }
}
