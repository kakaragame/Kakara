package org.kakara.engine.test;

import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.Panel;
import org.kakara.engine.ui.components.Rectangle;
import org.kakara.engine.ui.components.Sprite;
import org.kakara.engine.ui.components.Text;
import org.kakara.engine.ui.events.HUDClickEvent;
import org.kakara.engine.ui.events.HUDHoverEnterEvent;
import org.kakara.engine.ui.events.HUDHoverLeaveEvent;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.text.Font;
import org.kakara.engine.ui.text.TextAlign;
import org.kakara.engine.utils.Time;
import org.kakara.engine.utils.Utils;

import java.io.InputStream;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameScene extends AbstractGameScene {
    private GameItem player;
    private GameHandler handler;
    private PointLight light;
    private GameItem lightIndication;
    private KakaraTest test;

    private Text fps;

    public MainGameScene(GameHandler gameHandler, KakaraTest test) throws Exception {
        super(gameHandler);
        this.test = test;
        setCurserStatus(false);
        gameHandler.getCamera().setPosition(0, 3, 0);
        var resourceManager = gameHandler.getResourceManager();
        Mesh[] mainPlayer = StaticModelLoader.load(resourceManager.getResource("player/steve.obj"), "/player",this,resourceManager);
        MeshGameItem object = new MeshGameItem(mainPlayer);
        object.setPosition(4, 3f, 4);
        object.setScale(0.3f);
        object.setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(1, 1.5f, 1)));
        object.getCollider().setUseGravity(true).setTrigger(false);
        ((BoxCollider) object.getCollider()).setOffset(new Vector3(0, 0.7f, 0));

        add(object);
        player = object;
        //Load Blocks

        Mesh mesh = new Mesh(CubeData.vertex, CubeData.texture, CubeData.normal, CubeData.indices);
        InputStream io = Texture.class.getResourceAsStream("/example_texture.png");
        Texture grass = Utils.inputStreamToTexture(io);
        Material mt = new Material(grass);

        mt.addOverlayTexture(Utils.inputStreamToTexture(Texture.class.getResourceAsStream("/oa.png")));
        mt.addOverlayTexture(Utils.inputStreamToTexture(Texture.class.getResourceAsStream("/ovly2.png")));

        mesh.setMaterial(mt);
        MeshGameItem gi = new MeshGameItem(mesh);
        add(gi);
        gi.setPosition(0, 0, -5);
        Texture skyb = Utils.inputStreamToTexture(Texture.class.getResourceAsStream("/skybox.png"));
        SkyBox skyBox = new SkyBox(skyb, true);
        setSkyBox(skyBox);


        for (int x = 5; x > -12; x--) {
            for (int z = 5; z > -12; z--) {
                MeshGameItem gis = (MeshGameItem) gi.clone(false);
                gis.setPosition(x, 0, z);
                gis.setCollider(new ObjectBoxCollider(false, true));
                getItemHandler().addItem(gis);
            }
        }

        light = new PointLight(new Vector3f(0, 2, 0));
        lightIndication = new MeshGameItem(mesh).setScale(0.3f).setPosition(0, 2, 0);
        add(light);
        this.add(new PointLight().setPosition(0, 3, 0).setDiffuse(0.1f, 0, 0).setSpecular(0.5f, 0, 0));
        this.add(new PointLight().setPosition(3, 3, 3).setDiffuse(0f, 0.3f, 0).setSpecular(0, 0, 0.7f));
        this.add(new SpotLight(gameHandler.getCamera().getPosition(), new Vector3(0, 0, 1)));
        this.add(lightIndication);
        // Allows you to see the light.
        this.getLightHandler().getDirectionalLight().setDirection(-1, -1, 0);

        /*

            The brand new canvas code.

         */

        ComponentCanvas cc = new ComponentCanvas(this);

//        Panel pnl = new Panel();
//        Sprite sprite = new Sprite(grass, new Vector2(0, 0), new Vector2(grass.getWidth(), grass.getHeight()));
//        sprite.setScale(100, 100);
//        pnl.add(sprite);
//        pnl.setPosition(100, 100);
//        cc.add(pnl);
//
//
//        Rectangle rect = new Rectangle();
//        rect.setScale(50f, 50f);
//        rect.setPosition((float) gameHandler.getWindow().getWidth() / 2 - pnl.position.x, (float) gameHandler.getWindow().getHeight() / 2 - pnl.position.y);
//        pnl.add(rect);
//
//        rect.addUActionEvent(new HUDClickEvent() {
//            @Override
//            public void OnHUDClick(Vector2 location, MouseClickType clickType) {
//                System.out.println("test : " + clickType);
//                rect.setColor(new RGBA(0, 255, 0, 1));
//            }
//        }, HUDClickEvent.class);
//
//        rect.addUActionEvent(new HUDHoverEnterEvent() {
//            @Override
//            public void OnHudHoverEnter(Vector2 location) {
//                rect.setColor(new RGBA(255, 0, 0, 1));
//            }
//        }, HUDHoverEnterEvent.class);
//
//        rect.addUActionEvent(new HUDHoverLeaveEvent() {
//            @Override
//            public void OnHudHoverLeave(Vector2 location) {
//                rect.setColor(new RGBA(255, 255, 255, 1));
//            }
//        }, HUDHoverLeaveEvent.class);

        Font font = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"));
        hud.addFont(font);

        Text fps = new Text("FPS: 000", font);
        fps.setColor(new RGBA(0,0,0,1));

        fps.setPosition(20, 20);
        cc.add(fps);
        this.fps = fps;
        add(cc);


        this.handler = gameHandler;
    }

    public GameItem getPlayer() {
        return player;
    }

    @Override
    public void update() {
        KeyInput ki = handler.getKeyInput();

        fps.setText("FPS: " + Math.round(1/ Time.deltaTime));

        if (ki.isKeyPressed(GLFW_KEY_W)) {
            handler.getCamera().movePosition(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            handler.getCamera().movePosition(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            handler.getCamera().movePosition(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            handler.getCamera().movePosition(1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            handler.getCamera().movePosition(0, 1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            handler.getCamera().movePosition(0, -1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_ESCAPE)) {
            test.exit();
        }
        if (ki.isKeyPressed(GLFW_KEY_TAB)) {
            this.setCurserStatus(!this.getCurserStatus());
        }

        Vector3 currentPos = player.getPosition();
        if (ki.isKeyPressed(GLFW_KEY_UP)) {
            player.translateBy(0.1f, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_DOWN)) {
            player.setPosition(currentPos.x - 0.1f, currentPos.y, currentPos.z);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT)) {
            player.setPosition(currentPos.x, currentPos.y, currentPos.z + 0.1f);
        }
        if (ki.isKeyPressed(GLFW_KEY_RIGHT)) {
            player.setPosition(currentPos.x, currentPos.y, currentPos.z - 0.1f);
        }

        if (ki.isKeyPressed(GLFW_KEY_I)) {
            lightIndication.translateBy(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_K)) {
            lightIndication.translateBy(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_J)) {
            lightIndication.translateBy(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_L)) {
            lightIndication.translateBy(1, 0, 0);
        }

        light.setPosition(lightIndication.getPosition());

        getLightHandler().getSpotLight(0).setPosition(handler.getCamera().getPosition());

        MouseInput mi = handler.getMouseInput();
        handler.getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
        if (handler.getSoundManager().getListener() != null)
            handler.getSoundManager().getListener().setPosition(gameHandler.getCamera().getPosition());
    }


}