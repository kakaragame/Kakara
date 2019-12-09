package org.kakara.engine.test;

import org.kakara.engine.GameEngine;
import org.kakara.engine.GameHandler;
import org.kakara.engine.IGame;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.objects.MeshObject;
import org.kakara.engine.render.Mesh;
import org.kakara.engine.render.Texture;
import static org.lwjgl.glfw.GLFW.*;

public class KakaraTest implements IGame {

    private MeshObject obj;
    private GameHandler gInst;
    private boolean isCursorShowing;

    @Override
    public void start(GameHandler handler) throws Exception {
        gInst = handler;
        float[] positions = new float[] {
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};

        Texture txt = new Texture(Texture.class.getResourceAsStream("/grassblock.png"));
        Mesh mesh = new Mesh(positions, textCoords, indices, txt);
        MeshObject obj = new MeshObject(mesh);
        obj.setPosition(0, 0, -5);
        this.obj = obj;
        gInst.getObjectHandler().addObject(obj);

        for(int x = 4; x > -5; x--){
            for(int z = 4; z > -5; z--){
                MeshObject obj2 = new MeshObject(mesh);
                obj2.setPosition(x, 0, z);
                gInst.getObjectHandler().addObject(obj2);
            }
        }

        gInst.getEventManager().registerHandler(this);
        glfwSetInputMode(gInst.getWindow().getWindowHandler(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        isCursorShowing = false;
        gInst.getCamera().setPosition(0, 5, 0);
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

        KeyInput ki = gInst.getKeyInput();

        if(ki.isKeyPressed(GLFW_KEY_W)){
            gInst.getCamera().movePosition(0, 0, -1);
        }
        if(ki.isKeyPressed(GLFW_KEY_S)){
            gInst.getCamera().movePosition(0, 0, 1);
        }
        if(ki.isKeyPressed(GLFW_KEY_A)){
            gInst.getCamera().movePosition(-1, 0, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_D)){
            gInst.getCamera().movePosition(1, 0, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_SPACE)){
            gInst.getCamera().movePosition(0, 1, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)){
            gInst.getCamera().movePosition(0, -1, 0);
        }

        MouseInput mi = gInst.getMouseInput();
        // Gimmicky camera movements.
//        System.out.println(mi.getDeltaPosition());
        gInst.getCamera().moveRotation((float)(mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
//        gInst.getCamera().setRotation(-(float) (mi.getPosition().y - gInst.getWindow().getHeight()/2) * 0.3f, (float) mi.getPosition().x * 0.5f, 0);
    }

    @EventHandler
    public void onMouseClick(OnMouseClickEvent evt){
        System.out.println("clicked1");
    }

    @EventHandler
    public void onKeyEvent(OnKeyPressEvent evt){
        if(evt.isKeyPressed(GLFW_KEY_TAB)){
            if(isCursorShowing){
                glfwSetInputMode(gInst.getWindow().getWindowHandler(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                isCursorShowing = false;
            }else{
                glfwSetInputMode(gInst.getWindow().getWindowHandler(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                isCursorShowing = true;
            }
        }
    }
}
