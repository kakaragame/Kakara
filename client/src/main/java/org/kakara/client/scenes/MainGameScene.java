package org.kakara.client.scenes;

import org.joml.Vector3f;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.ChunkBase;
import org.kakara.core.world.ChunkGenerator;
import org.kakara.core.world.GameBlock;
import org.kakara.engine.GameHandler;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class MainGameScene extends AbstractGameScene {
    private KakaraGame kakaraGame;
    private PointLight light;
   // private GameItem lightIndication;

    public MainGameScene(GameHandler gameHandler, KakaraGame kakaraGame) {
        super(gameHandler);
        this.kakaraGame = kakaraGame;
        setCurserStatus(false);



        loadMods();
        ChunkGenerator generator = kakaraGame.getKakaraCore().getWorldGenerationManager().getChunkGenerators().get(0);
        ChunkBase base = new ChunkBase(null, 0, 0, new ArrayList<>());
        base = generator.generateChunk(123, base);
        for (GameBlock gameBlock : base.getGameBlocks()) {
            Mesh mesh = new Mesh(CubeData.vertex, CubeData.texture, CubeData.normal, CubeData.indices);
            Resource resource = kakaraGame.getKakaraCore().getResourceManager().getTexture(gameBlock.getItemStack().getItem().getTexture(), TextureResolution._16, gameBlock.getItemStack().getItem().getMod());
            Material mt = new Material(new Texture(MoreUtils.coreResourceToEngineResource(resource, kakaraGame), this));
            mesh.setMaterial(mt);
            MeshGameItem gi = new MeshGameItem(mesh);
            gi.setPosition(MoreUtils.locationToVector3(gameBlock.getLocation()));
            add(gi);

        }

        light = new PointLight(new Vector3f(0, 2, 0));
        //lightIndication = new MeshGameItem(mesh).setScale(0.3f).setPosition(0, 2, 0);
        add(light);
        this.add(new PointLight().setPosition(0, 3, 0).setDiffuse(0.1f, 0, 0).setSpecular(0.5f, 0, 0));
        this.add(new PointLight().setPosition(3, 3, 3).setDiffuse(0f, 0.3f, 0).setSpecular(0, 0, 0.7f));
        this.add(new SpotLight(gameHandler.getCamera().getPosition(), new Vector3(0, 0, 1)));
       // this.add(lightIndication);
        // Allows you to see the light.
        this.getLightHandler().getDirectionalLight().setDirection(-1, -1, 0);

    }

    public void loadMods() {
        kakaraGame.getKakaraCore().getModManager().loadMods(Arrays.stream(getModJars()).collect(Collectors.toList()));
    }

    private File[] getModJars() {
        File dir = new File("test" + File.separator + "mods");

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jar");
            }
        });
    }

    @Override
    public void update() {
        KeyInput ki = kakaraGame.getGameHandler().getKeyInput();


        if (ki.isKeyPressed(GLFW_KEY_W)) {
            kakaraGame.getGameHandler().getCamera().movePosition(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            kakaraGame.getGameHandler().getCamera().movePosition(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            kakaraGame.getGameHandler().getCamera().movePosition(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            kakaraGame.getGameHandler().getCamera().movePosition(1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            kakaraGame.getGameHandler().getCamera().movePosition(0, 1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            kakaraGame.getGameHandler().getCamera().movePosition(0, -1, 0);
        }
        MouseInput mi = kakaraGame.getGameHandler().getMouseInput();
        kakaraGame.getGameHandler().getCamera().moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
        if (kakaraGame.getGameHandler().getSoundManager().getListener() != null)
            kakaraGame.getGameHandler().getSoundManager().getListener().setPosition(gameHandler.getCamera().getPosition());
    }
}
