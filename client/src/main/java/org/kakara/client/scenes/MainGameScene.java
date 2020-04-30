package org.kakara.client.scenes;

import me.ryandw11.octree.Octree;
import me.ryandw11.octree.OutOfBoundsException;
import org.apache.commons.lang3.Validate;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.RenderedChunk;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.client.game.world.ClientChunk;
import org.kakara.client.scenes.canvases.DebugModeCanvas;
import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.core.Kakara;
import org.kakara.core.events.annotations.EventHandler;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.engine.GameHandler;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.Texture;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.TextureCache;
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.properties.HorizontalCenterProperty;
import org.kakara.engine.ui.properties.VerticalCenterProperty;
import org.kakara.engine.ui.text.Font;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.items.blocks.AirBlock;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameScene extends AbstractGameScene {
    private boolean debugMode = false;
    private KakaraGame kakaraGame;
    private Server server;
    private Octree<RenderedChunk> renderedChunks;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ChatComponent chatComponent;

    public MainGameScene(GameHandler gameHandler, Server server, KakaraGame kakaraGame) {
        super(gameHandler);
        setCurserStatus(false);
        this.server = server;
        this.kakaraGame = kakaraGame;
        try {
            renderedChunks = new Octree<>(-30000000, -100, -30000000, 30000000, 10000, 30000000);
        } catch (OutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        System.out.println("PUSH");

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

    }

    @Override
    public void loadGraphics(GameHandler handler) {
        //getHUD().addFont(kakaraGame.getFont());
        getHUD().addItem(DebugModeCanvas.getInstance(kakaraGame, this));

        var resourceManager = gameHandler.getResourceManager();
        kakaraGame.getGameHandler().getEventManager().registerHandler(this, this);
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
        file.deleteOnExit();
        TextureAtlas atlas = new TextureAtlas(textures, file.getAbsolutePath(), this);
        setTextureAtlas(atlas);
        try {
            //setSkyBox(new SkyBox(loadSkyBoxTexture(), true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Font roboto = null;
        try {
            roboto = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"), this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ComponentCanvas main = new ComponentCanvas(this);
        chatComponent = new ChatComponent(roboto, false, this);
        chatComponent.addProperty(new HorizontalCenterProperty());
        chatComponent.addProperty(new VerticalCenterProperty());
        chatComponent.addUActionEvent(new ChatSendEvent() {
            @Override
            public void onChatSend(String message) {
                if (message.startsWith("/")) {
                    Kakara.getCommandManager().executeCommand(message.substring(1), server.getPlayerEntity());
                }
            }
        }, ChatSendEvent.class);
        main.add(chatComponent);
        add(main);

    }

    private Texture loadSkyBoxTexture() {
        try {
            return TextureCache.getInstance(gameHandler.getResourceManager()).getTexture("skybox/general.png", this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public RenderedChunk getChunk(Chunk chunk) {
        Validate.notNull(chunk, "Chunk is null");
        return renderedChunks.get(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ());
    }

    @Override
    public void update(float interval) {
        server.update();
        playerMovement();
        if (chatComponent != null) {
            if (server instanceof IntegratedServer) {
                ((IntegratedServer) server).newMessages().forEach(s -> {
                    chatComponent.addMessage(s);
                });
            }
        }
        executorService.submit(() -> {
            for (Chunk loadedChunk : server.getPlayerEntity().getLocation().getWorld().getLoadedChunks()) {
                RenderedChunk renderedChunk = getChunk(loadedChunk);
                ClientChunk clientChunk = (ClientChunk) loadedChunk;

                if (!GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                    if (renderedChunk != null) getChunkHandler().removeChunk(renderedChunk.getRenderChunkID());
                    renderedChunks.remove(loadedChunk.getLocation().getX(), loadedChunk.getLocation().getY(), loadedChunk.getLocation().getZ());
                }
                if (renderedChunk == null || clientChunk.isUpdatedHappened()) {
                    if (renderedChunk != null) getChunkHandler().removeChunk(renderedChunk.getRenderChunkID());
                    renderedChunks.remove(loadedChunk.getLocation().getX(), loadedChunk.getLocation().getY(), loadedChunk.getLocation().getZ());
                    if (GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                        ChunkLocation cb = loadedChunk.getLocation();
                        RenderChunk rc = new RenderChunk(new ArrayList<>(), getTextureAtlas());
                        rc.setPosition(cb.getX(), cb.getY(), cb.getZ());

                        for (GameBlock gb : loadedChunk.getGameBlocks()) {
                            if (gb.getItemStack().getItem() instanceof AirBlock) continue;
                            Vector3 vector3 = MoreUtils.locationToVector3(gb.getLocation());
                            vector3 = vector3.subtract(cb.getX(), cb.getY(), cb.getZ());
                            RenderBlock rb = new RenderBlock(new BlockLayout(), getTextureAtlas().getTextures().get(ThreadLocalRandom.current().nextInt(0, 3)), vector3);
                            rc.addBlock(rb);
                        }
                        clientChunk.setUpdatedHappened(false);
                        rc.regenerateChunkAsync(getTextureAtlas());
                        getChunkHandler().addChunk(rc);
                        try {
                            if (!renderedChunks.find(loadedChunk.getLocation().getX(), loadedChunk.getLocation().getY(), loadedChunk.getLocation().getZ()))
                                renderedChunks.insert(loadedChunk.getLocation().getX(), loadedChunk.getLocation().getY(), loadedChunk.getLocation().getZ(), new RenderedChunk(rc.getId(), loadedChunk.getLocation()));
                        } catch (OutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });


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

    public Server getServer() {
        return server;
    }

}
