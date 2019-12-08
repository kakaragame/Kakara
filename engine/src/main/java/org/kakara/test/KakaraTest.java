package org.kakara.test;

import org.kakara.engine.GameEngine;
import org.kakara.engine.IGame;
import org.kakara.engine.objects.MeshObject;
import org.kakara.engine.render.Mesh;

public class KakaraTest implements IGame {

    private MeshObject obj;
    private GameEngine gInst;

    @Override
    public void start(GameEngine eng) {
        gInst = eng;
        float[] positions = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };

        Mesh mesh = new Mesh(positions, colours, indices);
        MeshObject obj = new MeshObject(mesh);
        obj.setPosition(0, 0, -5);
        this.obj = obj;
        gInst.getObjectHandler().addObject(obj);
    }

    @Override
    public void update() {
        float rotationx = obj.getRotation().x + 1.5f;
        float rotationy = obj.getRotation().y + 1.5f;
        float rotationz = obj.getRotation().z + 1.5f;
        if ( rotationz > 360 ) {
            rotationz = 0;
        }
        if ( rotationx > 360 ) rotationx = 0;
        if ( rotationy > 360 ) rotationy = 0;
        obj.setRotation(rotationx, rotationy, rotationz);
    }
}
