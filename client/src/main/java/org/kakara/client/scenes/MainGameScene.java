package org.kakara.client.scenes;

import org.joml.Vector3f;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.scenes.canvases.DebugModeCanvas;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.client.Save;
import org.kakara.core.game.ItemStack;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.UnModObject;
import org.kakara.core.mod.game.GameModManager;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.*;
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
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.game.Server;
import org.kakara.game.client.ClientChunk;
import org.kakara.game.client.ClientChunkWriter;
import org.kakara.game.client.player.ClientPlayer;
import org.kakara.game.items.blocks.AirBlock;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class MainGameScene extends AbstractGameScene {
    private boolean debugMode = false;
    private KakaraGame kakaraGame;
    private Server server;

    public MainGameScene(GameHandler gameHandler, Server server, KakaraGame kakaraGame) {
        super(gameHandler);
        this.server = server;
        this.kakaraGame = kakaraGame;
    }


    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        if (e.isKeyPressed(GLFW_KEY_F3)) {
            if (debugMode) {
                debugMode = false;
                DebugModeCanvas.getInstance(kakaraGame, this).remove();
            } else {
                debugMode = true;
                DebugModeCanvas.getInstance(kakaraGame, this).add();
            }
        }
        if (e.isKeyPressed(GLFW_KEY_ESCAPE)) {
            System.exit(1);
        }
    }

    @Override
    public void work() {
        server.loadMods();
    }

    @Override
    public void loadGraphics() {
        getHUD().addFont(kakaraGame.getFont());
        getHUD().addItem(DebugModeCanvas.getInstance(kakaraGame, this));

        var resourceManager = gameHandler.getResourceManager();

        List<RenderTexture> textures = new ArrayList<>();

        for (Resource resource : Kakara.getResourceManager().getAllTextures(TextureResolution._16)) {
            try {
                RenderTexture txt1 = new RenderTexture(resourceManager.getResource(resource.getLocalPath()));
                textures.add(txt1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        File file = new File(Kakara.getWorkingDirectory(), "tmp");
        if (!file.exists()) {
            file.mkdir();
        }
        TextureAtlas atlas = new TextureAtlas(textures, file.getAbsolutePath(), this);
        setTextureAtlas(atlas);
        try {
            setSkyBox(new SkyBox(loadSkyBoxTexture(), true));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public void update(float interval) {
        server.update();
        playerMovement();
        for (Chunk loadedChunk : server.getPlayerEntity().getLocation().getWorld().getLoadedChunks()) {
//CHeck if Chunk Needs to be re rendered.
        }
    }

    private void playerMovement() {

        if (debugMode) {
            DebugModeCanvas.getInstance(kakaraGame, this).update();
        }
        ClientPlayer player = (ClientPlayer) server.getPlayerEntity();
        KeyInput ki = kakaraGame.getGameHandler().getKeyInput();
        if (ki.isKeyPressed(GLFW_KEY_W)) {
            player.moveLocation(0, 0, -1);
        }
        if (ki.isKeyPressed(GLFW_KEY_S)) {
            player.moveLocation(0, 0, 1);
        }
        if (ki.isKeyPressed(GLFW_KEY_A)) {
            player.moveLocation(-1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_D)) {
            player.moveLocation(1, 0, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            player.moveLocation(0, -1, 0);
        }
        if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
            player.moveLocation(0, 1.1F, 0);
        }
        //I NEED HELP!
        MouseInput mi = kakaraGame.getGameHandler().getMouseInput();
        player.moveLocation((float) mi.getDeltaPosition().y(), (float) mi.getDeltaPosition().x());
        Location l = player.getLocation();
        gameHandler.getCamera().setPosition(MoreUtils.locationToVector3(l).add(0, 2, 0));
        gameHandler.getCamera().setRotation(new Vector3(l.getPitch(), l.getYaw(), 0));
    }
}
