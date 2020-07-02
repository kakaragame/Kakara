package org.kakara.client.scenes;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.client.game.player.PlayerContentInventory;
import org.kakara.client.game.world.ClientChunk;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.client.scenes.canvases.DebugModeCanvas;
import org.kakara.client.scenes.canvases.HotBarCanvas;
import org.kakara.client.scenes.canvases.PauseMenuCanvas;
import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.client.scenes.uicomponenets.events.ChatBlurEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatFocusEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.core.Kakara;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.engine.Camera;
import org.kakara.engine.GameHandler;
import org.kakara.engine.physics.collision.BoxCollider;
import org.kakara.engine.physics.collision.Collidable;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.events.event.MouseClickEvent;
import org.kakara.engine.input.KeyInput;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.input.MouseInput;
import org.kakara.engine.item.*;
import org.kakara.engine.item.mesh.Mesh;
import org.kakara.engine.math.Intersection;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.StaticModelLoader;
import org.kakara.engine.models.TextureCache;
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.mesh.MeshType;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.RGBA;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.items.ComponentCanvas;

import org.kakara.engine.ui.text.Font;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameScene extends AbstractGameScene {
    private KakaraGame kakaraGame;
    private Server server;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ChatComponent chatComponent;
    private LoadingCache<String, RenderTexture> renderTextureCache;

    // TODO improve this
    private HotBarCanvas hotBarCanvas;
    private MeshGameItem blockSelector;

    public MainGameScene(GameHandler gameHandler, Server server, KakaraGame kakaraGame) {
        super(gameHandler);
        setCurserStatus(false);
        this.server = server;
        this.kakaraGame = kakaraGame;
        renderTextureCache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<>() {
            @Override
            public RenderTexture load(String s) throws Exception {
                return getResource(s);
            }
        });
    }


    @EventHandler
    public void onKeyPress(KeyPressEvent e) {
        if (e.isKeyPressed(GLFW_KEY_F3)) {
            DebugModeCanvas.getInstance(kakaraGame, this).switchStatus();
        }
        if (e.isKeyPressed(GLFW_KEY_ESCAPE) && !chatComponent.isFocused()) {
            PauseMenuCanvas.getInstance(kakaraGame, this).switchStatus();
        }
    }

    @Override
    public void work() {

    }

    MeshGameItem test;

    @Override
    public void loadGraphics(GameHandler handler) {
        //getHUD().addFont(kakaraGame.getFont());
        getHUD().addItem(DebugModeCanvas.getInstance(kakaraGame, this));
        getHUD().addItem(PauseMenuCanvas.getInstance(kakaraGame, this));

        var resourceManager = gameHandler.getResourceManager();
        List<RenderTexture> textures = new ArrayList<>();

        for (Resource resource : Kakara.getResourceManager().getAllTextures(TextureResolution._16)) {

            RenderTexture txt1 = new RenderTexture(resourceManager.getResource(resource.getLocalPath()));
            textures.add(txt1);

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
        Font roboto = new Font("Roboto-Regular", resourceManager.getResource("Roboto-Regular.ttf"), this);

        hotBarCanvas = new HotBarCanvas(this, getTextureAtlas(), renderTextureCache, (PlayerContentInventory) server.getPlayerEntity().getInventory());
        hotBarCanvas.show();

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

        Rectangle indicator = new Rectangle(new Vector2(0, 0), new Vector2(5, 5), new RGBA(0, 255, 0, 1));
        indicator.addConstraint(new HorizontalCenterConstraint());
        indicator.addConstraint(new VerticalCenterConstraint());
        main.add(indicator);

        add(main);
        add(hotBarCanvas);
        add(hotBarCanvas.getObjectCanvas());
        try {
            Mesh[] mainPlayer = StaticModelLoader.load(resourceManager.getResource("player/steve.obj"), "/player", this, resourceManager);
            MeshGameItem object = new MeshGameItem(mainPlayer);
            object.setVisible(false);
            object.setPosition((float) server.getPlayerEntity().getLocation().getX(), (float) server.getPlayerEntity().getLocation().getY(), (float) server.getPlayerEntity().getLocation().getZ());
//            object.setScale(0.3f);
            object.setCollider(new BoxCollider(new Vector3(0, 0, 0), new Vector3(0.99f, 1.99f, 0.99f)));
//            ((BoxCollider) object.getCollider()).setOffset(new Vector3(0, 0.7f, 0));
            add(object);
            test = object;
            ((ClientPlayer) server.getPlayerEntity()).setGameItemID(object.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Mesh m = new Mesh(CubeData.vertex, CubeData.texture, CubeData.normal, CubeData.indices);
        m.setMaterial(new Material(new Texture(resourceManager.getResource("block_select.png").getByteBuffer())));
        m.setWireframe(true);
        this.blockSelector = new MeshGameItem(m);
        this.blockSelector.setScale(1.01f);
        add(this.blockSelector);
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
            for (Chunk loadedChunk : server.getPlayerEntity().getLocation().getWorld().get().getLoadedChunks()) {
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
                                rb = new RenderBlock(new BlockLayout(), renderTextureCache.get(GameResourceManager.correctPath(Kakara.getResourceManager().getTexture(gb.getItemStack().getItem().getTexture(), TextureResolution._16, gb.getItemStack().getItem().getMod()).getLocalPath())), vector3);
                            } catch (RuntimeException | ExecutionException e) {
                                e.printStackTrace();
                                continue;
                            }
                            rc.addBlock(rb);
                        }
                        clientChunk.setUpdatedHappened(false);
                        server.getExecutorService().submit(() -> {
                            rc.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                        });
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

    boolean playerInJump = false;
    float lastYPos = 0;

    private void playerMovement() {

        if (DebugModeCanvas.getInstance(kakaraGame, this).isActivated()) {
            DebugModeCanvas.getInstance(kakaraGame, this).update();
        }
        if (server instanceof IntegratedServer) {
            if (PauseMenuCanvas.getInstance(kakaraGame, this).isActivated()) return;
        }
        PauseMenuCanvas.getInstance(kakaraGame, this).update();

        if (chatComponent.isFocused()) return;

        ClientPlayer player = (ClientPlayer) server.getPlayerEntity();

        player.getGameItemID().ifPresent(uuid -> {
            getItemByID(uuid).ifPresent((gameItem) -> {
                MeshGameItem item = (MeshGameItem) gameItem;
                item.setVelocityX(0);
                item.setVelocityZ(0);

                Camera gameCamera = getCamera();
                KeyInput ki = kakaraGame.getGameHandler().getKeyInput();
                if (ki.isKeyPressed(GLFW_KEY_W)) {
                    item.setVelocityByCamera(new Vector3(0, item.getVelocity().y, -7), gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_S)) {
                    item.setVelocityByCamera(new Vector3(0, item.getVelocity().y, 7), gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_A)) {
                    item.setVelocityByCamera(new Vector3(-7, item.getVelocity().y, 0), gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_D)) {
                    item.setVelocityByCamera(new Vector3(7, item.getVelocity().y, 0), gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                    item.movePositionByCamera(0, -0.3f, 0, gameCamera);
                }
                if (ki.isKeyPressed(GLFW_KEY_SPACE) && !playerInJump) {
                    playerInJump = true;
                    lastYPos = item.getPosition().y;
                    item.setVelocityY(4);
                }
                if(playerInJump){
                    item.movePositionByCamera(0, 0.3F, 0, gameCamera);
                    if(item.getPosition().y > lastYPos + 3){
                        playerInJump = false;
                        item.setVelocityY(-9.18f);
                    }
                }
                if (ki.isKeyPressed(GLFW_KEY_G))
                    item.setVelocityY(-9.18f);
                Location location = player.getLocation();
                location.setX(item.getPosition().x);
                location.setY(item.getPosition().y);
                location.setZ(item.getPosition().z);
                player.setLocation(location);
                //I NEED HELP!
                MouseInput mi = kakaraGame.getGameHandler().getMouseInput();
                player.moveLocation((float) mi.getDeltaPosition().y(), (float) mi.getDeltaPosition().x());
                getCamera().setPosition(MoreUtils.locationToVector3(location).add(0, 1, 0));
                Location l = player.getLocation();
                getCamera().setRotation(new Vector3(l.getPitch(), l.getYaw(), 0));

                // Handle the block selector.
                this.blockSelector.setPosition(item.getPosition().x, -10, item.getPosition().z);
                Collidable objectFound = this.selectGameItems(20, uuid);
                if(objectFound != null)
                    this.blockSelector.setPosition(objectFound.getColPosition());
            });

        });
    }

    /*
        Block breaking code.
        Does not do anything with the core code. (That is half true now)
     */
    @EventHandler
    public void onMousePress(MouseClickEvent evt) {
        UUID playerID = ((ClientPlayer)server.getPlayerEntity()).getGameItemID().get();
        if (evt.getMouseClickType() == MouseClickType.LEFT_CLICK && !chatComponent.isFocused()) {
            Collidable col = this.selectGameItems(20, playerID);
            if (col instanceof RenderBlock) {
                RenderBlock rb = (RenderBlock) col;
                RenderChunk parentChunk = rb.getParentChunk();
                parentChunk.removeBlock(rb);
                parentChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                server.getPlayerEntity().getLocation().getWorld().get().setBlock(Kakara.createItemStack(Kakara.getItemManager().getItem(0).get()), new Location(parentChunk.getPosition().x + rb.getPosition().x, parentChunk.getPosition().y + rb.getPosition().y, parentChunk.getPosition().z + rb.getPosition().z));
            }
        } else if (evt.getMouseClickType() == MouseClickType.RIGHT_CLICK && !chatComponent.isFocused()) {
            Collidable col = this.selectGameItems(20, playerID);
            if (col instanceof RenderBlock) {
                RenderBlock rb = (RenderBlock) col;
                RenderChunk parentChunk = rb.getParentChunk();

                Vector3 absoluteBlockPos = rb.getPosition().add(parentChunk.getPosition());
                Vector2 result = new Vector2(0, 0);

                float closestResult = 20;
                Vector3 closestValue = absoluteBlockPos.clone();

                Vector3 front = absoluteBlockPos.add(1, 0, 0);
                if (Intersection.intersect((int) front.x, (int) front.y, (int) front.z, getCamera(), result) && result.x < closestResult) {
                    closestResult = result.x;
                    closestValue = absoluteBlockPos.add(1, 0, 0);
                }

                Vector3 back = absoluteBlockPos.add(-1, 0, 0);
                if (Intersection.intersect((int) back.x, (int) back.y, (int) back.z, getCamera(), result) && result.x < closestResult) {
                    closestResult = result.x;
                    closestValue = absoluteBlockPos.add(-1, 0, 0);
                }

                Vector3 left = absoluteBlockPos.add(0, 0, 1);
                if (Intersection.intersect((int) left.x, (int) left.y, (int) left.z, getCamera(), result) && result.x < closestResult) {
                    closestResult = result.x;
                    closestValue = absoluteBlockPos.add(0, 0, 1);
                }

                Vector3 right = absoluteBlockPos.add(0, 0, -1);
                if (Intersection.intersect((int) right.x, (int) right.y, (int) right.z, getCamera(), result) && result.x < closestResult) {
                    closestResult = result.x;
                    closestValue = absoluteBlockPos.add(0, 0, -1);
                }

                Vector3 up = absoluteBlockPos.add(0, 1, 0);
                if (Intersection.intersect((int) up.x, (int) up.y, (int) up.z, getCamera(), result) && result.x < closestResult) {
                    closestResult = result.x;
                    closestValue = absoluteBlockPos.add(0, 1, 0);
                }

                Vector3 down = absoluteBlockPos.add(0, -1, 0);
                if (Intersection.intersect((int) down.x, (int) down.y, (int) down.z, getCamera(), result) && result.x < closestResult) {
                    closestResult = result.x;
                    closestValue = absoluteBlockPos.add(0, -1, 0);
                }

                final Vector3 closValue = closestValue;
                ChunkLocation chunkLoc = GameUtils.getChunkLocation(new Location(closestValue.x, closestValue.y, closestValue.z));
                Chunk chunk = ((ClientWorld) server.getPlayerEntity().getLocation().getWorld().get()).getChunkNow(chunkLoc);

                ClientChunk cc = (ClientChunk) chunk;
                List<RenderChunk> rcc = getChunkHandler().getRenderChunkList().stream().filter((rc) -> rc.getId() == cc.getRenderChunkID().get()).collect(Collectors.toList());
                RenderChunk desiredChunk = rcc.get(0);
                Vector3 newBlockLoc = closValue.subtract(desiredChunk.getPosition());
                if (!desiredChunk.getOctChunk().find((int) newBlockLoc.x, (int) newBlockLoc.y, (int) newBlockLoc.z)) {
                    //IGNORE Air
                    if (hotBarCanvas.getCurrentItemStack().getItem() instanceof AirBlock) return;
                    RenderBlock rbs = null;
                    try {
                        rbs = new RenderBlock(new BlockLayout(),
                                renderTextureCache.get(GameResourceManager.correctPath(Kakara.getResourceManager().getTexture(hotBarCanvas.getCurrentItemStack().getItem().getTexture(), TextureResolution._16, hotBarCanvas.getCurrentItemStack().getItem().getMod()).getLocalPath())), newBlockLoc);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    desiredChunk.addBlock(rbs);
                    desiredChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                    //THIS might work?
                    cc.setGameBlock(new GameBlock(MoreUtils.vector3ToLocation(newBlockLoc.add(desiredChunk.getPosition()), server.getPlayerEntity().getLocation().getWorld().get()), hotBarCanvas.getCurrentItemStack()));

                }


            }

        }

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
