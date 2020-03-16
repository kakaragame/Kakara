package org.kakara.client.scenes;

import org.joml.Vector3f;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.core.Kakara;
import org.kakara.core.game.ItemStack;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.UnModObject;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.ChunkBase;
import org.kakara.core.world.ChunkGenerator;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.lighting.DirectionalLight;
import org.kakara.engine.lighting.LightColor;
import org.kakara.engine.lighting.PointLight;
import org.kakara.engine.lighting.SpotLight;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.models.TextureCache;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class MainGameScene extends AbstractGameScene {
    private KakaraGame kakaraGame;
    private PointLight light;
    // private GameItem lightIndication;

    private MeshGameItem player;
    private List<ChunkBase> myChunk = new ArrayList<>();

    public MainGameScene(GameHandler gameHandler, KakaraGame kakaraGame, List<File> modsToLoad) throws Exception {
        super(gameHandler);
        this.kakaraGame = kakaraGame;
        setCurserStatus(false);
        loadMods(modsToLoad);

    }

    public void loadMods(List<File> modFiles) {
        List<UnModObject> modsToLoad = Kakara.getModManager().loadModsFile(modFiles);
        Kakara.getModManager().loadMods(modsToLoad);

    }

    protected static File[] getModJars() {
        File dir = new File("test" + File.separator + "mods");

        return dir.listFiles((dir1, filename) -> filename.endsWith(".jar"));
    }

    @Override
    public void work() {
        Kakara.getModManager().loadStage(Kakara.getEventManager());
        Kakara.getModManager().loadStage(Kakara.getItemManager());
        Kakara.getModManager().loadStage(Kakara.getWorldGenerationManager());

        ((GameModManager) Kakara.getModManager()).postEnable();
        ChunkGenerator generator = Kakara.getWorldGenerationManager().getChunkGenerators().get(0);
        if (generator == null) System.out.println("TELL ME HOW THIS HAPPENED");
        ChunkBase base = null;
        for (int i = -4; i <= 4; i = i + 4) {
            for (int j = -4; j <= 4; j = j + 4) {
                myChunk.add(generator.generateChunk(45, new ChunkBase(null, i, j, new ArrayList<>())));

            }
        }

        //myChunk.add(generator.generateChunk(45, base));
        kakaraGame.getGameHandler().getEventManager().registerHandler(this, this);
    }

    @Override
    public void loadGraphics() {
        long currentTime = System.currentTimeMillis();

        var resourceManager = gameHandler.getResourceManager();
        try {
            setSkyBox(new SkyBox(loadSkyBoxTexture(), true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<ItemStack, List<Location>> gameBlockMutableIntMap = MoreUtils.sortByType(myChunk);

        for (Map.Entry<ItemStack, List<Location>> entry : gameBlockMutableIntMap.entrySet()) {
            InstancedMesh mesh = new InstancedMesh(CubeData.vertex, CubeData.texture, CubeData.normal, CubeData.indices, entry.getValue().size());

            Resource resource = Kakara.getResourceManager().getTexture(entry.getKey().getItem().getTexture(), TextureResolution._16, entry.getKey().getItem().getMod());
            Material mt = null;
            try {
                mt = new Material(TextureCache.getInstance(resourceManager).getTexture(resource.getLocalPath(), this));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            mesh.setMaterial(mt);
            for (Location location : entry.getValue()) {
                MeshGameItem item = new MeshGameItem(mesh);
                item.setPosition(MoreUtils.locationToVector3(location));
                item.setCollider(new ObjectBoxCollider(false, true));
                item.setMesh(mesh);
                add(item);
            }
        }


        PointLight pointLight = new PointLight(new LightColor(255, 255, 0), new Vector3(1, 1, 1), 1);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        this.

                add(pointLight);


        DirectionalLight directionalLight = new DirectionalLight(new LightColor(255, 223, 0), new Vector3(0, 1, 0.5f), 0.3f);
        directionalLight.setShadowPosMult(8);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);

        getLightHandler().

                setDirectionalLight(directionalLight);


        //Load Player


        Mesh[] mainPlayer = new Mesh[0];
        try {
            mainPlayer = StaticModelLoader.load(resourceManager.getResource("player/steve.obj"), "/player", this, resourceManager);
        } catch (
                Exception e) {
            e.printStackTrace();
            return;
        }

        player = new

                MeshGameItem(mainPlayer);
        player.setPosition(12, 1025, 12);
        player.setScale(0.3f);
        player.setCollider(new

                BoxCollider(new Vector3(0, 0, 0), new

                Vector3(1, 1.5f, 1)));
        player.getCollider().

                setUseGravity(false).

                setTrigger(false);
        ((BoxCollider) player.getCollider()).

                setOffset(new Vector3(0, 0.7f, 0));

        add(player);
        System.out.println(System.currentTimeMillis() - currentTime);

    }

    private Texture loadSkyBoxTexture() {
        try {
            return TextureCache.getInstance(gameHandler.getResourceManager()).getTexture("skybox/general.png", this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void update(float s) {
        if (player == null) return;

        KeyInput ki = kakaraGame.getGameHandler().getKeyInput();
        if (ki.isKeyPressed(GLFW_KEY_W)) {
            player.movePosition(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            player.movePosition(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            player.movePosition(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            player.movePosition(1, 0, 0);
        }

        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            player.movePosition(0, -1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            player.movePosition(0, 1.1F, 0);
        }
        MouseInput mi = kakaraGame.getGameHandler().getMouseInput();
        player.moveRotation((float) (mi.getDeltaPosition().y), (float) mi.getDeltaPosition().x, 0);
        if (kakaraGame.getGameHandler().getSoundManager().getListener() != null)
            kakaraGame.getGameHandler().getSoundManager().getListener().setPosition(gameHandler.getCamera().getPosition());
        getLightHandler().getDisplayPointLights().get(0).setPosition(player.getPosition());
        gameHandler.getCamera().setPosition(player.getPosition().add(0, 2, 0));
        gameHandler.getCamera().setRotation(player.getRotationAsVector3());
    }

    @EventHandler
    public void onKeyPress(KeyPressEvent e) {

        if (e.isKeyPressed(GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }
    }
}
