package org.kakara.client.scenes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.client.game.world.ClientChunk;
import org.kakara.client.scenes.canvases.DebugModeCanvas;
import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.client.scenes.uicomponenets.events.ChatBlurEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatFocusEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.core.Kakara;
import org.kakara.core.events.annotations.EventHandler;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.engine.Camera;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.BoxCollider;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.models.TextureCache;
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.ui.properties.HorizontalCenterProperty;
import org.kakara.engine.ui.properties.VerticalCenterProperty;
import org.kakara.engine.ui.text.Font;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameScene extends AbstractGameScene {
    private boolean debugMode = true;
    private KakaraGame kakaraGame;
    private Server server;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ChatComponent chatComponent;
    private LoadingCache<ResourceObject, RenderTexture> renderTextureCache;

    public MainGameScene(GameHandler gameHandler, Server server, KakaraGame kakaraGame) {
        super(gameHandler);
        setCurserStatus(false);
        this.server = server;
        this.kakaraGame = kakaraGame;
        renderTextureCache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<ResourceObject, RenderTexture>() {
            @Override
            public RenderTexture load(ResourceObject s) throws Exception {
                return getResource(Kakara.getResourceManager().getTexture(s.getResource(), TextureResolution._16, s.getMod()).getLocalPath());
            }
        });
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
            setSkyBox(new SkyBox(loadSkyBoxTexture(), true));
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
        chatComponent.setPosition(0, 170);
        chatComponent.addUActionEvent((ChatSendEvent) message -> {
            if (message.startsWith("/")) {
                Kakara.getCommandManager().executeCommand(message.substring(1), server.getPlayerEntity());
            }
        }, ChatSendEvent.class);
        chatComponent.addUActionEvent((ChatFocusEvent) () -> setCurserStatus(true), ChatFocusEvent.class);
        chatComponent.addUActionEvent((ChatBlurEvent) () -> setCurserStatus(false), ChatBlurEvent.class);
        main.add(chatComponent);
        add(main);
        try {
            Mesh[] mainPlayer = StaticModelLoader.load(resourceManager.getResource("player/steve.obj"), "/player", this, resourceManager);
            MeshGameItem object = new MeshGameItem(mainPlayer);
            object.setPosition((float) server.getPlayerEntity().getLocation().getX(), (float) server.getPlayerEntity().getLocation().getY(), (float) server.getPlayerEntity().getLocation().getZ());
            object.setScale(0.3f);
            object.setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(1, 1.5f, 1)));
            object.getCollider().setUseGravity(false).setTrigger(false);
            ((BoxCollider) object.getCollider()).setOffset(new Vector3(0, 0.7f, 0));
            getItemHandler().addItem(object);
            ((ClientPlayer) server.getPlayerEntity()).setGameItemID(object.getId());
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
        if (chatComponent != null) {
            if (server instanceof IntegratedServer) {
                ((IntegratedServer) server).newMessages().forEach(s -> {
                    chatComponent.addMessage(s);
                });
            }
        }
        executorService.submit(() -> {
            for (Chunk loadedChunk : server.getPlayerEntity().getLocation().getWorld().getLoadedChunks()) {
                ClientChunk clientChunk = (ClientChunk) loadedChunk;

                if (!GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                    if (clientChunk.getRenderChunkID().isPresent())
                        getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
                }
                if (clientChunk.getRenderChunkID().isEmpty() || clientChunk.isUpdatedHappened()) {
                    if (clientChunk.getRenderChunkID().isPresent())
                        getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
                    if (GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                        ChunkLocation cb = loadedChunk.getLocation();
                        RenderChunk rc = new RenderChunk(new ArrayList<>(), getTextureAtlas());
                        rc.setPosition(cb.getX(), cb.getY(), cb.getZ());

                        for (GameBlock gb : loadedChunk.getGameBlocks()) {
                            if (gb.getItemStack().getItem() instanceof AirBlock) continue;
                            Vector3 vector3 = MoreUtils.locationToVector3(gb.getLocation());
                            vector3 = vector3.subtract(cb.getX(), cb.getY(), cb.getZ());
                            RenderBlock rb = null;
                            try {
                                rb = new RenderBlock(new BlockLayout(), renderTextureCache.get(new ResourceObject(gb.getItemStack().getItem().getMod(), gb.getItemStack().getItem().getTexture())), vector3);
                            } catch (RuntimeException | ExecutionException e) {
                                e.printStackTrace();
                                continue;
                            }
                            rc.addBlock(rb);
                        }
                        clientChunk.setUpdatedHappened(false);
                        rc.regenerateChunkAsync(getTextureAtlas());
                        getChunkHandler().addChunk(rc);
                        clientChunk.setRenderChunkID(rc.getId());
                    }
                }

            }
        });

    }

    private RenderTexture getResource(String texture) {
        return getTextureAtlas().getTextures().stream().filter(renderTexture -> GameResourceManager.correctPath(renderTexture.getResource().getOriginalPath()).equals(GameResourceManager.correctPath(texture))).findFirst().orElseThrow(() -> {
            return new RuntimeException("Unable to find " + GameResourceManager.correctPath(texture));
        });
    }

    private void playerMovement() {

        if (debugMode) {
            DebugModeCanvas.getInstance(kakaraGame, this).update();
        }

        if (chatComponent.isFocused()) return;

        ClientPlayer player = (ClientPlayer) server.getPlayerEntity();

        player.getGameItemID().ifPresent(uuid -> {
            getItemByID(uuid).ifPresent((gameItem) -> {
                MeshGameItem item = (MeshGameItem) gameItem;
                Camera gameCamera = gameHandler.getCamera();
                KeyInput ki = kakaraGame.getGameHandler().getKeyInput();
                if (ki.isKeyPressed(GLFW_KEY_W)) {
                    item.movePositionByCamera(0, 0, -1, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_S)) {
                    item.movePositionByCamera(0, 0, 1, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_A)) {
                    item.movePositionByCamera(-1, 0, 0, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_D)) {
                    item.movePositionByCamera(1, 0, 0, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                    item.movePositionByCamera(0, -1, 0, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_SPACE)) {
                    item.movePositionByCamera(0, 1.1F, 0, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_G))
                    item.getCollider().setUseGravity(true);
                Location location = player.getLocation();
                location.setX(item.getPosition().x);
                location.setY(item.getPosition().y);
                location.setZ(item.getPosition().z);
                player.setLocation(location);
                //I NEED HELP!
                MouseInput mi = kakaraGame.getGameHandler().getMouseInput();
                player.moveLocation((float) mi.getDeltaPosition().y(), (float) mi.getDeltaPosition().x());
                gameHandler.getCamera().setPosition(MoreUtils.locationToVector3(location).add(0, 2, 0));
                Location l = player.getLocation();
                gameHandler.getCamera().setRotation(new Vector3(l.getPitch(), l.getYaw(), 0));

            });

        });
    }

    private Optional<GameItem> getItemByID(UUID uuid) {
        AtomicReference<GameItem> gameItemA = new AtomicReference<>();
        getItemHandler().getNonInstancedMeshMap().forEach((mesh, gameItems) -> gameItems.stream().filter(gameItem -> gameItem.getId().equals(uuid)).findFirst().ifPresent(gameItemA::set));
        return Optional.ofNullable(gameItemA.get());
    }

    public Server getServer() {
        return server;
    }

}
