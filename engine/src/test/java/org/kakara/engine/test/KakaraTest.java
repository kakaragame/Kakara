package org.kakara.engine.test;

import org.joml.Quaternionf;
import org.kakara.engine.GameHandler;
import org.kakara.engine.Game;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.OnKeyPressEvent;
import org.kakara.engine.events.event.OnMouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.Material;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.Mesh;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

import static org.lwjgl.glfw.GLFW.*;

public class KakaraTest implements Game {

    private GameHandler gInst;
    private GameItem gi2;
    private GameItem player;

    @Override
    public void start(GameHandler handler) throws Exception {
        gInst = handler;
        Mesh[] houseMesh = StaticModelLoader.load(Utils.getFileFromResource(Main.class.getResource("/player/steve.obj")), Utils.getFileFromResource(Main.class.getResource("/player/")));
        System.out.println("houseMesh = " + houseMesh.length);

        // Steve Creation
        GameItem object = new GameItem(houseMesh);
        object.setPosition(4,4f,4).setScale(0.3f).setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(1, 1.5f, 1), true));
        object.getCollider().setUseGravity(true).setTrigger(false);
        ((BoxCollider) object.getCollider()).setOffset(new Vector3(0, 0.7f, 0));
        System.out.println("Player Game Item UUID: " + object.getUUID());
        this.player = object;

        Mesh[] tree = StaticModelLoader.load(Utils.getFileFromResource(Main.class.getResource("/tree/tree.obj")), Utils.getFileFromResource(Main.class.getResource("/tree/")));
        GameItem treeObject = new GameItem(tree);
        treeObject.setPosition(1,0.7f,1);
        treeObject.setScale(0.05f);
        // Set the rotation of the tree using only quaternions
        treeObject.setRotationAboutAxis((float) Math.toRadians(-90), new Vector3(1, 0, 0));
        /* If you want to use eular angles then do the following:
        treeObject.setRotation(KMath.eularToQuaternion(new Vector3((float) Math.toRadians(-90), 0, 0))); */
        gInst.getItemHandler().addItem(treeObject);


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
                4, 6, 7, 5, 4, 7};
        Mesh mesh = new Mesh(positions, textCoords, new float[0], indices);
        InputStream io = Texture.class.getResourceAsStream("/grassblock.png");
        Texture grass = Utils.inputStreamToTexture(io);
        mesh.setMaterial(new Material(grass));
        GameItem gi = new GameItem(mesh);
        gInst.getItemHandler().addItem(gi);
        gi.setPosition(0, 0, -5);


        for(int x = 5; x > -6; x--){
            for(int z = 5; z > -6; z--){
                GameItem gis = gi.clone(false);
                gis.setPosition(x, 0, z);
                gis.setCollider(new ObjectBoxCollider(false, true));
                gInst.getItemHandler().addItem(gis);
            }
        }

        GameItem gi1 = new GameItem(mesh);
        gi1.setPosition(-4, 3, -4);
        gi1.setCollider(new ObjectBoxCollider(true, false));
        gInst.getItemHandler().addItem(gi1);
        GameItem gi2 = new GameItem(mesh);
        gi2.setPosition(2, 3f, -5);
        gi2.setCollider(new ObjectBoxCollider());
        gInst.getItemHandler().addItem(gi2);

        System.out.println(gInst.getCollisionManager().isColliding(gi1, gi2));



        gInst.getItemHandler().addItem(object);
        gInst.getEventManager().registerHandler(this);
        // Added engine API for the cursor GLFW method.
        gInst.getWindow().setCursorVisibility(false);

        // Sets the default camera position and rotation.
        gInst.getCamera().setPosition(5, 5, 0);
        gInst.getCamera().setRotation(45, 270, 0);

        this.gi2 = gi2;
        this.gi1 = gi1;
    }

    private GameItem gi1;

    @Override
    public void update() {

        KeyInput ki = gInst.getKeyInput();

        if (ki.isKeyPressed(GLFW_KEY_W)) {
            gInst.getCamera().movePosition(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            gInst.getCamera().movePosition(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            gInst.getCamera().movePosition(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            gInst.getCamera().movePosition(1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            gInst.getCamera().movePosition(0, 1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            gInst.getCamera().movePosition(0, -1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }

        Vector3 currentPos = player.getPosition();
//        System.out.println("Test Class: " + currentPos + "  " + player.getUuid());
        if(ki.isKeyPressed(GLFW_KEY_UP)){
//            player.setPosition(currentPos.x + 4.1f, currentPos.y, currentPos.z);
            player.translateBy(0.1f, 0, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_DOWN)){
            player.setPosition(currentPos.x - 0.1f, currentPos.y, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_LEFT)){
            player.setPosition(currentPos.x, currentPos.y, currentPos.z + 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_RIGHT)){
            player.setPosition(currentPos.x, currentPos.y, currentPos.z - 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_N)){
            gi2.translateBy(0, 0.1f, 0);
        }
        if(ki.isKeyPressed(GLFW_KEY_M)){
            gi2.translateBy(0, -0.1f, 0);
        }

        MouseInput mi = gInst.getMouseInput();
        gInst.getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
    }

    public void input(){
        KeyInput ki = gInst.getKeyInput();
        Vector3 currentPos = gi2.getPosition();
        if(ki.isKeyPressed(GLFW_KEY_UP)){
            gi2.setPosition(currentPos.x + 0.1f, currentPos.y, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_DOWN)){
            gi2.setPosition(currentPos.x - 0.1f, currentPos.y, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_LEFT)){
            gi2.setPosition(currentPos.x, currentPos.y, currentPos.z + 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_RIGHT)){
            gi2.setPosition(currentPos.x, currentPos.y, currentPos.z - 0.1f);
        }
        if(ki.isKeyPressed(GLFW_KEY_N)){
            gi2.setPosition(currentPos.x, currentPos.y + 0.1f, currentPos.z);
        }
        if(ki.isKeyPressed(GLFW_KEY_M)){
            gi2.setPosition(currentPos.x, currentPos.y - 0.1f, currentPos.z);
        }
    }

    @EventHandler
    public void onMouseClick(OnMouseClickEvent evt) {
        System.out.println("clicked1");
    }

    @EventHandler
    public void onKeyEvent(OnKeyPressEvent evt) {
        if (evt.isKeyPressed(GLFW_KEY_TAB)) {
            //Engine API replaced GLFW methods.
            gInst.getWindow().setCursorVisibility(!gInst.getWindow().isCursorVisable());
            gInst.getMouseInput().setCursorPosition(gInst.getWindow().getWidth()/2, gInst.getWindow().getHeight()/2);
        }
    }
}
