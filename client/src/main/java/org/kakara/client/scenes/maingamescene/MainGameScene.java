package org.kakara.client.scenes.maingamescene;


import org.kakara.client.Client;
import org.kakara.client.ClientServerController;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.engine.item.HorizontalRotationFeature;
import org.kakara.client.local.game.DroppedItem;
import org.kakara.client.local.game.IntegratedServer;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.local.game.player.PlayerContentInventory;
import org.kakara.client.local.game.world.ClientChunk;
import org.kakara.client.local.game.world.ClientWorld;
import org.kakara.client.scenes.BreakingBlock;
import org.kakara.client.scenes.canvases.DebugModeCanvas;
import org.kakara.client.scenes.canvases.HotBarCanvas;
import org.kakara.client.scenes.canvases.PauseMenuCanvas;
import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.client.scenes.uicomponenets.events.ChatBlurEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatFocusEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.Status;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.resources.TextureResolution;
import org.kakara.core.common.world.Chunk;
import org.kakara.core.common.world.ChunkLocation;
import org.kakara.core.common.world.GameBlock;
import org.kakara.core.common.world.Location;
import org.kakara.engine.GameHandler;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.events.event.MouseClickEvent;
import org.kakara.engine.gameitems.Material;
import org.kakara.engine.gameitems.MeshGameItem;
import org.kakara.engine.gameitems.SkyBox;
import org.kakara.engine.gameitems.Texture;
import org.kakara.engine.gameitems.mesh.AtlasMesh;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.TextureCache;
import org.kakara.engine.physics.collision.BoxCollider;
import org.kakara.engine.physics.collision.Collidable;
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.mesh.MeshType;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.utils.RGBA;
import org.kakara.engine.utils.Time;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;

public class MainGameScene extends AbstractGameScene {
    protected final KakaraGame kakaraGame;
    private final Client client;
    protected final RenderResourceManager renderResourceManager = new RenderResourceManager(this);
    protected final PlayerMovement movement = new PlayerMovement(this);
    protected final SceneUtils sceneUtils = new SceneUtils(this);
    private final Queue<Runnable> updateOnMainThread = new LinkedBlockingQueue<>();
    protected ChatComponent chatComponent;
    protected BreakingBlock breakingBlock = null;
    protected HotBarCanvas hotBarCanvas;
    protected MeshGameItem blockSelector;
    protected RenderTexture breakingTexture;
    private boolean hasRun = false;

    public MainGameScene(GameHandler gameHandler, Client client, KakaraGame kakaraGame) {
        super(gameHandler);
        setCurserStatus(false);
        this.client = client;
        this.kakaraGame = kakaraGame;

        if (getServer() instanceof IntegratedServer) {
            ((IntegratedServer) getServer()).setSceneTickUpdate(this::gameSceneUpdate);
        }
        if (Kakara.getGameInstance() == null) {
            throw new IllegalStateException("A Kakara game has not been initialized. ");
        }
    }


    @Override
    public void work() {
        //This only exists to make Intellij Happy.
        KakaraGame.LOGGER.info("Loading MainGameScene");
    }


    @Override
    public void loadGraphics(GameHandler handler) {
        getUserInterface().addItem(DebugModeCanvas.getInstance(kakaraGame, this));
        getUserInterface().addItem(PauseMenuCanvas.getInstance(kakaraGame, this));

        var resourceManager = gameHandler.getResourceManager();
        List<RenderTexture> textures = new ArrayList<>();

        for (org.kakara.core.common.resources.Texture resource : Objects.requireNonNull(Kakara.getGameInstance()).getResourceManager().getAllTextures()) {
            //Ignore Textures that shouldn't be added to Texture Atlas
            if (resource.getProperties().contains("NO_TEXTURE_ATLAS")) {
                continue;
            }
            RenderTexture txt1 = new RenderTexture(resourceManager.getResource(resource.get().getLocalPath()));
            textures.add(txt1);

        }
        breakingTexture = new RenderTexture(resourceManager.getResource("breaking/breaking.png"));
        textures.add(breakingTexture);
        File file = new File(Kakara.getGameInstance().getWorkingDirectory(), "tmp");
        if (!file.exists() && !file.mkdir()) {
            throw new IllegalStateException("Unable to create tmp folder.");
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

        hotBarCanvas = new HotBarCanvas(this, getTextureAtlas(), renderResourceManager, (PlayerContentInventory) getServer().getPlayerEntity().getInventory(), roboto);
        hotBarCanvas.show();

        ComponentCanvas main = new ComponentCanvas(this);
        chatComponent = new ChatComponent(roboto, false, this);
        chatComponent.setPosition(0, 170);
        chatComponent.addUActionEvent(ChatSendEvent.class, (ChatSendEvent) message -> {
            if (message.startsWith("/")) {
                Kakara.getGameInstance().getCommandManager().executeCommand(message.substring(1), getServer().getPlayerEntity());
            }
        });
        chatComponent.addUActionEvent(ChatFocusEvent.class, (ChatFocusEvent) () -> setCurserStatus(true));
        chatComponent.addUActionEvent(ChatBlurEvent.class, (ChatBlurEvent) () -> setCurserStatus(false));
        main.add(chatComponent);

        Rectangle indicator = new Rectangle(new Vector2(0, 0), new Vector2(5, 5), new RGBA(0, 255, 0, 1));
        indicator.addConstraint(new HorizontalCenterConstraint());
        indicator.addConstraint(new VerticalCenterConstraint());
        main.add(indicator);

        add(main);
        add(hotBarCanvas);
        try {
            ((ClientPlayer) getServer().getPlayerEntity()).setGameItemID(sceneUtils.createPlayerObject());
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
        DebugModeCanvas.getInstance(kakaraGame, this).update();
        movement.playerMovement();
        hotBarCanvas.update();
        if (chatComponent != null) {
            if (getServer() instanceof IntegratedServer) {
                ((IntegratedServer) getServer()).newMessages().forEach(s -> chatComponent.addMessage(s));
            }
        }
        renderDroppedItems();
        synchronized (updateOnMainThread) {
            while (!updateOnMainThread.isEmpty()) {
                updateOnMainThread.poll().run();
            }
        }
        blockBreakHandler();
    }

    private void blockBreakHandler() {
        ClientPlayer player = (ClientPlayer) getServer().getPlayerEntity();
        if (player.getGameItemID().isEmpty()) return;
        if (kakaraGame.getGameHandler().getMouseInput().isLeftButtonPressed() && !chatComponent.isFocused()) {
            Collidable col = this.selectGameItems(20, player.getGameItemID().get());
            if (col instanceof RenderBlock) {
                RenderBlock rb = (RenderBlock) col;
                RenderChunk parentChunk = rb.getParentChunk();
                Location location = new Location(player.getLocation().getNullableWorld(), parentChunk.getPosition().x + rb.getPosition().x, parentChunk.getPosition().y + rb.getPosition().y, parentChunk.getPosition().z + rb.getPosition().z);
                if (breakingBlock == null || !breakingBlock.getGbLocation().equals(location)) {
                    if (breakingBlock != null) {
                        Optional<RenderChunk> chunk = getChunkHandler().getRenderChunkList().stream().filter(renderChunk -> renderChunk.getPosition().equals(breakingBlock.getChunkLocation())).findFirst();
                        chunk.ifPresent(renderChunk -> {
                            RenderBlock block = renderChunk.getOctChunk()[(int) breakingBlock.getBlockLocation().x][(int) breakingBlock.getBlockLocation().y][(int) breakingBlock.getBlockLocation().z];
                            block.setOverlay(null);
                            renderChunk.regenerateOverlayTextures(getTextureAtlas());
                        });
                    }
                    breakingBlock = new BreakingBlock(location, parentChunk.getPosition(), rb.getPosition());
                    rb.setOverlay(breakingTexture);
                    parentChunk.regenerateOverlayTextures(getTextureAtlas());
                } else {
                    Optional<GameBlock> blockAt = getServer().getPlayerEntity().getLocation().getNullableWorld().getBlockAt(location);
                    blockAt.ifPresent(block -> {
                        if (block.getItemStack().getItem() instanceof AirBlock) return;
                        //hotBarCanvas.getCurrentItemStack()
                        double breakPerFrame = GameUtils.getBreakingTime(blockAt.get(), null, player);
                        if (breakingBlock.breakBlock(breakPerFrame * Time.getDeltaTime())) {
                            //If the block was cancelled. By the server it will require a re-render. This might be changed in the future.
                            parentChunk.removeBlock(rb);
                            parentChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                            //Remove old breakingBlock
                            breakingBlock = null;
                            //call the blockBreak
                            ((ClientServerController) getServer().getServerController()).blockBreak(location);
                        }
                    });
                    if (blockAt.isEmpty()) {
                        System.out.println("OUCH");
                    }
                }
            }
        }
    }

    public void renderDroppedItems() {

        for (DroppedItem droppedItem : ((ClientWorld) getServer().getPlayerEntity().getLocation().getNullableWorld()).getDroppedItems()) {
            if (droppedItem.getGameID() == null) {
                AtlasMesh mesh = new AtlasMesh(getTexture(droppedItem.getItemStack()), getTextureAtlas(), new BlockLayout(), CubeData.vertex, CubeData.normal, CubeData.indices);
                MeshGameItem droppedBlock = new MeshGameItem(mesh);
                BoxCollider collider = new BoxCollider(new Vector3(0f, 0f, 0f), new Vector3(0.8f, 0.9f, 0.8f));
                collider.setPredicate(collidable -> {
                    if (collidable instanceof RenderBlock) return false;
                    return !(collidable instanceof RenderChunk);
                });
                droppedBlock.setCollider(collider);
                droppedBlock.setVelocityY(-9.18f);
                droppedBlock.setScale(0.3f);
                droppedBlock.setPosition((float) droppedItem.getLocation().getX(), (float) droppedItem.getLocation().getY(), (float) droppedItem.getLocation().getZ());
                droppedBlock.setTag("pickupable");
                droppedBlock.getData().add(droppedItem.getItemStack().getItem().getControllerKey());
                droppedBlock.addFeature(new HorizontalRotationFeature());
                droppedItem.setGameID(droppedBlock.getUUID());
                add(droppedBlock);
            }
        }
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

    @EventHandler
    public void onMousePress(MouseClickEvent evt) {

        UUID playerID = ((ClientPlayer) getServer().getPlayerEntity()).getGameItemID().orElseThrow(() -> new IllegalStateException("No Player Entity Found"));
        if (evt.getMouseClickType() == MouseClickType.RIGHT_CLICK && !chatComponent.isFocused()) {
            Collidable col = this.selectGameItems(20, playerID);
            if (col instanceof RenderBlock) {
                RenderBlock rb = (RenderBlock) col;
                RenderChunk parentChunk = rb.getParentChunk();

                Vector3 absoluteBlockPos = rb.getPosition().add(parentChunk.getPosition());

                final Vector3 closestValue = ObjectPickingUtils.closestValue(absoluteBlockPos, getCamera());
                ChunkLocation chunkLoc = GameUtils.getChunkLocation(new Location(getServer().getPlayerEntity().getLocation().getNullableWorld(), closestValue.x, closestValue.y, closestValue.z));
                if (getServer().getPlayerEntity().getLocation().getWorld().isEmpty()) return;
                Chunk chunk = (Objects.requireNonNull(getServer().getPlayerEntity().getLocation().getNullableWorld())).getChunkAt(chunkLoc);
                if (chunk.getStatus() != Status.LOADED) return;
                ClientChunk cc = (ClientChunk) chunk;
                List<RenderChunk> rcc = getChunkHandler().getRenderChunkList().stream().filter((rc) -> {
                    if (cc.getRenderChunkID().isPresent()) {
                        return rc.getId() == cc.getRenderChunkID().get();
                    }
                    return false;
                }).collect(Collectors.toList());
                RenderChunk desiredChunk = rcc.get(0);
                Vector3 newBlockLoc = closestValue.subtract(desiredChunk.getPosition());
                if (desiredChunk.getOctChunk()[(int) newBlockLoc.x][(int) newBlockLoc.y][(int) newBlockLoc.z] == null) {
                    //IGNORE Airs
                    if (hotBarCanvas.getCurrentItemStack().getItem() instanceof AirBlock) return;
                    RenderBlock rbs = new RenderBlock(new BlockLayout(),
                            renderResourceManager.get(GameResourceManager.correctPath(Kakara.getGameInstance().getResourceManager().getTexture(hotBarCanvas.getCurrentItemStack().getItem().getTexture(), TextureResolution._16, hotBarCanvas.getCurrentItemStack().getItem().getMod()).getLocalPath())), newBlockLoc);
                    desiredChunk.addBlock(rbs);
                    desiredChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                    getController().blockPlace(MoreUtils.vector3ToLocation(newBlockLoc.add(desiredChunk.getPosition()), chunkLoc.getNullableWorld()), hotBarCanvas.getCurrentItemStack());
                    //TODO HotBar render should be done by the Inventory
                    //hotBarCanvas.renderItems();
                }


            }

        }

    }

    /**
     * Update from the integrated getServer()
     */
    public void gameSceneUpdate() {
        if (getServer().getPlayerEntity().getLocation().getWorld().isEmpty()) return;
        if (getServer().getPlayerEntity() == null || chatComponent == null) return;

        if (getServer().getPlayerEntity().getLocation().getNullableWorld() == null) return;

        for (Chunk loadedChunk : ((ClientWorld) getServer().getPlayerEntity().getLocation().getNullableWorld()).getChunksNow()) {
            if (loadedChunk.getStatus() != Status.LOADED) continue;
            ClientChunk clientChunk = (ClientChunk) loadedChunk;
            if (!GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(getServer().getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                if (clientChunk.getRenderChunkID().isPresent())
                    getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
//                System.out.println("Chunk Unloaded.");

                // Maybe: TODO The operation should be done below instead of in the ChunkCleaner
//                getServer().getPlayerEntity().getLocation().getNullableWorld().unloadChunk(clientChunk);
                continue;
            }

            ChunkLocation playerLocation = GameUtils.getChunkLocation(getServer().getPlayerEntity().getLocation());

            if (GameUtils.isLocationOnPerimeter(playerLocation, clientChunk.getLocation(), IntegratedServer.RADIUS * 16))
                continue;

            ChunkLocation nextLocation = loadedChunk.getLocation().add(16, 0, 0);

            if (playerLocation.getNullableWorld() == null)
                continue;

            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(-16, 0, 0);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, 16, 0);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, -16, 0);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, 0, 16);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, 0, -16);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }

            if (clientChunk.getRenderChunkID().isEmpty() || clientChunk.isUpdatedHappened()) {
                if (clientChunk.getRenderChunkID().isPresent())
                    getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
                if (GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(getServer().getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS * 16)) {
                    ChunkLocation cb = loadedChunk.getLocation();
                    RenderChunk rc = new RenderChunk(new ArrayList<>(), getTextureAtlas());
                    rc.setPosition(cb.getX(), cb.getY(), cb.getZ());

                    for (GameBlock gb : loadedChunk.getGameBlocks()) {
                        if (gb.getItemStack().getItem().getId() == 0) continue;
                        Vector3 vector3 = MoreUtils.locationToVector3(gb.getLocation());
                        vector3 = vector3.subtract(cb.getX(), cb.getY(), cb.getZ());
                        RenderBlock rb = new RenderBlock(new BlockLayout(),
                                renderResourceManager.get
                                        ((Kakara.getGameInstance().getResourceManager().getTexture(gb.getItemStack().getItem().getTexture(), TextureResolution._16, gb.getItemStack().getItem().getMod()).getLocalPath())), vector3);

                        rc.addBlock(rb);
                    }
                    clientChunk.setUpdatedHappened(false);
                    if (!hasRun) {
                        getServer().getExecutorService().submit(() -> {
                            rc.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                        });
                        hasRun = true;
                    } else {
                        rc.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                    }
                    getChunkHandler().addChunk(rc);
                    clientChunk.setRenderChunkID(rc.getId());
                }
            }

        }
    }

    public RenderResourceManager getRenderResourceManager() {
        return renderResourceManager;
    }

    public Server getServer() {
        return client.getServer();
    }

    public ResourceManager getResourceManager() {
        return gameHandler.getResourceManager();
    }

    public HotBarCanvas getHotBar() {
        return hotBarCanvas;
    }

    public void addQueueRunnable(Runnable run) {
        this.updateOnMainThread.add(run);
    }

    public void close() {
        PauseMenuCanvas.getInstance(kakaraGame, this).close();
        DebugModeCanvas.getInstance(kakaraGame, this).close();

    }

    private ClientServerController getController() {
        return (ClientServerController) getServer().getServerController();
    }

    private RenderTexture getTexture(ItemStack is) {
        return renderResourceManager.get(GameResourceManager.correctPath(Kakara.getGameInstance().getResourceManager().getTexture(is.getItem().getTexture(), TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }
}
