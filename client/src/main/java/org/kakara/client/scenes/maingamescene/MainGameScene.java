package org.kakara.client.scenes.maingamescene;


import org.kakara.client.Client;
import org.kakara.client.ClientServerController;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
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
import org.kakara.engine.gameitems.GameItem;
import org.kakara.engine.gameitems.Material;
import org.kakara.engine.gameitems.SkyBox;
import org.kakara.engine.gameitems.Texture;
import org.kakara.engine.gameitems.mesh.AtlasMesh;
import org.kakara.engine.gameitems.mesh.Mesh;
import org.kakara.engine.input.mouse.MouseClickType;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.physics.collision.BoxCollider;
import org.kakara.engine.physics.collision.ColliderComponent;

import org.kakara.engine.physics.collision.VoxelCollider;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.font.Font;
import org.kakara.engine.ui.items.ComponentCanvas;
import org.kakara.engine.utils.RGBA;
import org.kakara.engine.utils.Time;
import org.kakara.engine.voxels.TextureAtlas;
import org.kakara.engine.voxels.Voxel;
import org.kakara.engine.voxels.VoxelChunk;
import org.kakara.engine.voxels.VoxelTexture;
import org.kakara.engine.voxels.layouts.BlockLayout;
import org.kakara.engine.voxels.mesh.MeshType;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.resources.GameResourceManager;

import java.io.File;
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
    protected GameItem blockSelector;
    protected VoxelTexture breakingTexture;
    private boolean hasRun = false;

    public MainGameScene(GameHandler gameHandler, Client client, KakaraGame kakaraGame) {
        super(gameHandler);
        setCursorStatus(false);
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
        List<VoxelTexture> textures = new ArrayList<>();

        for (org.kakara.core.common.resources.Texture resource : Objects.requireNonNull(Kakara.getGameInstance()).getResourceManager().getAllTextures()) {
            //Ignore Textures that shouldn't be added to Texture Atlas
            if (resource.getProperties().contains("NO_TEXTURE_ATLAS")) {
                continue;
            }
            VoxelTexture txt1 = new VoxelTexture(resourceManager.getResource(resource.get().getLocalPath()));
            textures.add(txt1);

        }
        textures.add(new VoxelTexture(resourceManager.getResource("block_select.png")));
        breakingTexture = new VoxelTexture(resourceManager.getResource("breaking/breaking.png"));
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
//            if (message.startsWith("/")) {
//                Kakara.getGameInstance().getCommandManager().executeCommand(message.substring(1), getServer().getPlayerEntity());
//            }
            getController().messageSend(message.getBytes());
        });
        chatComponent.addUActionEvent(ChatFocusEvent.class, (ChatFocusEvent) () -> setCursorStatus(true));
        chatComponent.addUActionEvent(ChatBlurEvent.class, (ChatBlurEvent) () -> setCursorStatus(false));
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
        m.setMaterial(new Material(new Texture(resourceManager.getResource("block_select.png"), this)));
        m.setWireframe(true);
        this.blockSelector = new GameItem(m);
        this.blockSelector.transform.setScale(1.01f);
        add(this.blockSelector);
    }

    private Texture loadSkyBoxTexture() {
        System.out.println("gameHandler.getResourceManager().getResource(\"skybox.obj\").getURL().toString() = " + gameHandler.getResourceManager().getResource("skybox.obj").getURL().toString());
        Resource resource = gameHandler.getResourceManager().getResource("skybox/daytime2.png");
        System.out.println("resource.getURL().toString() = " + resource.getURL().toString());
        System.out.println("resource.getClass().getName() = " + resource.getClass().getName());
        return new Texture(resource, this);
    }


    @Override
    public void update(float interval) {
        try {
            internalUpdate();
        } catch (Exception e) {
            //I am so tired of the game freezing up and locking the mouse every time the game fails
            //This should catch it and print it out in a way that I can read the error.
            //TODO handle more serious exceptions with game closes.
            PauseMenuCanvas.getInstance(kakaraGame, this).switchStatus();
            KakaraGame.LOGGER.error("Game update exception thrown", e);
        }
    }

    private void internalUpdate() {
        DebugModeCanvas.getInstance(kakaraGame, this).update();
        movement.playerMovement();
        hotBarCanvas.update();

        renderDroppedItems();
        synchronized (updateOnMainThread) {
            while (!updateOnMainThread.isEmpty()) {
                updateOnMainThread.poll().run();
            }
        }
        if (kakaraGame.getGameHandler().getMouseInput().isLeftButtonPressed()) {
            blockBreakHandler();
        }
    }

    private void blockBreakHandler() {
        ClientPlayer player = (ClientPlayer) getServer().getPlayerEntity();
        if (player.getGameItemID().isEmpty()) return;
        if (kakaraGame.getGameHandler().getMouseInput().isLeftButtonPressed() && !chatComponent.isFocused()) {
            if (player.getGameItemID().isEmpty()) {
                KakaraGame.LOGGER.warn("Player is not initialized.");
                return;
            }
            ColliderComponent col = this.selectGameItems(20, player.getGameItemID().get());
            if (col instanceof VoxelCollider) {
                VoxelCollider voxelCollider = (VoxelCollider) col;
                Voxel rb = voxelCollider.getVoxel();
                VoxelChunk parentChunk = rb.getParentChunk();
                Location location = new Location(player.getLocation().getNullableWorld(), parentChunk.transform.getPosition().x + rb.getPosition().x, parentChunk.transform.getPosition().y + rb.getPosition().y, parentChunk.transform.getPosition().z + rb.getPosition().z);
                if (breakingBlock == null || !breakingBlock.getGbLocation().equals(location)) {
                    if (breakingBlock != null) {
                        Optional<VoxelChunk> chunk = getChunkHandler().getVoxelChunkList().stream().filter(VoxelChunk -> VoxelChunk.transform.getPosition().equals(breakingBlock.getChunkLocation())).findFirst();
                        chunk.ifPresent(voxelChunk -> {
                            Voxel block = voxelChunk.getVoxelArray()[(int) breakingBlock.getBlockLocation().x][(int) breakingBlock.getBlockLocation().y][(int) breakingBlock.getBlockLocation().z];
                            block.setOverlay(null);
                            voxelChunk.regenerateOverlayTextures(getTextureAtlas());
                        });
                    }
                    breakingBlock = new BreakingBlock(location, parentChunk.transform.getPosition(), rb.getPosition());
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
                            parentChunk.removeVoxel(rb);
                            parentChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                            //Remove old breakingBlock
                            breakingBlock = null;
                            //call the blockBreak
                            System.out.println("BREAKING BLOCK");
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
                GameItem droppedBlock = new GameItem(mesh);
                BoxCollider collider = droppedBlock.addComponent(BoxCollider.class);
                collider.setPredicate(collidable -> {
                    if (collidable instanceof VoxelCollider) return false;
                    //TODO correct this
                    //return !(collidable instanceof VoxelChunk);
                    return true;
                });
                //droppedBlock.setCollider(collider);
                //droppedBlock.transform.setVelocityY(-9.18f);
                droppedBlock.transform.setScale(0.3f);
                droppedBlock.transform.setPosition((float) droppedItem.getLocation().getX(), (float) droppedItem.getLocation().getY(), (float) droppedItem.getLocation().getZ());
                droppedBlock.setTag("pickupable");
                droppedBlock.getData().add(droppedItem.getItemStack().getItem().getControllerKey());
                //droppedBlock.addFeature(new HorizontalRotationFeature());
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
            ColliderComponent col = this.selectGameItems(20, playerID);
            if (col instanceof VoxelCollider) {
                VoxelCollider voxelCollider = (VoxelCollider) col;
                Voxel rb = voxelCollider.getVoxel();
                VoxelChunk parentChunk = rb.getParentChunk();

                Vector3 absoluteBlockPos = rb.getPosition().add(parentChunk.transform.getPosition());

                final Vector3 closestValue = ObjectPickingUtils.closestValue(absoluteBlockPos, getCamera());
                ChunkLocation chunkLoc = GameUtils.getChunkLocation(new Location(getServer().getPlayerEntity().getLocation().getNullableWorld(), closestValue.x, closestValue.y, closestValue.z));
                if (getServer().getPlayerEntity().getLocation().getWorld().isEmpty()) return;
                Chunk chunk = (Objects.requireNonNull(getServer().getPlayerEntity().getLocation().getNullableWorld())).getChunkAt(chunkLoc);
                if (chunk.getStatus() != Status.LOADED) return;
                ClientChunk cc = (ClientChunk) chunk;
                List<VoxelChunk> rcc = getChunkHandler().getVoxelChunkList().stream().filter((rc) -> {
                    if (cc.getVoxelID().isPresent()) {
                        return rc.getId() == cc.getVoxelID().get();
                    }
                    return false;
                }).collect(Collectors.toList());
                VoxelChunk desiredChunk = rcc.get(0);
                Vector3 newBlockLoc = closestValue.subtract(desiredChunk.transform.getPosition());
                if (desiredChunk.getVoxelArray()[(int) newBlockLoc.x][(int) newBlockLoc.y][(int) newBlockLoc.z] == null) {
                    //IGNORE Airs
                    if (hotBarCanvas.getCurrentItemStack().getItem() instanceof AirBlock) return;
                    Voxel rbs = new Voxel(new BlockLayout(),
                            renderResourceManager.get(GameResourceManager.correctPath(Objects.requireNonNull(Kakara.getGameInstance()).getResourceManager().getTexture(hotBarCanvas.getCurrentItemStack().getItem().getTexture(), TextureResolution._16, hotBarCanvas.getCurrentItemStack().getItem().getMod()).getLocalPath())), newBlockLoc);
                    desiredChunk.addVoxel(rbs);
                    desiredChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                    getController().blockPlace(MoreUtils.vector3ToLocation(newBlockLoc.add(desiredChunk.transform.getPosition()), chunkLoc.getNullableWorld()), hotBarCanvas.getCurrentItemStack());
                    //TODO HotBar render should be done by the Inventory
                    //hotBarCanvas.renderItems();
                }


            }

        } /*else if (evt.getMouseClickType() == MouseClickType.LEFT_CLICK && !chatComponent.isFocused()) {
            blockBreakHandler();
        }
*/
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
                if (clientChunk.getVoxelID().isPresent())
                    getChunkHandler().removeChunk(clientChunk.getVoxelID().get());
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
            nextLocation = nextLocation.subtractMut(16, 0, 0).addMut(-16, 0, 0);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = nextLocation.addMut(16, 0, 0).addMut(0, 16, 0);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = nextLocation.subtractMut(0, 16, 0).addMut(0, -16, 0);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = nextLocation.addMut(0, 16, 0).addMut(0, 0, 16);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = nextLocation.subtractMut(0, 0, 16).addMut(0, 0, -16);
            if (GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)) {
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }

            if (clientChunk.getVoxelID().isEmpty() || clientChunk.isUpdatedHappened()) {
                if (clientChunk.getVoxelID().isPresent())
                    getChunkHandler().removeChunk(clientChunk.getVoxelID().get());
                if (GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(getServer().getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS * 16)) {
                    ChunkLocation cb = loadedChunk.getLocation();
                    VoxelChunk rc = new VoxelChunk(new ArrayList<>(), getTextureAtlas());
                    rc.transform.setPosition(cb.getX(), cb.getY(), cb.getZ());
                    boolean nonAirFound = false;
                    for (GameBlock gb : loadedChunk.getGameBlocks()) {
                        if (gb.getItemStack().getItem().getId() == 0) continue;
                        if (!nonAirFound) nonAirFound = true;
                        Vector3 vector3 = MoreUtils.locationToVector3(gb.getLocation());
                        vector3 = vector3.subtract(cb.getX(), cb.getY(), cb.getZ());
                        Voxel rb = new Voxel(new BlockLayout(),
                                renderResourceManager.get
                                        ((Kakara.getGameInstance().getResourceManager().getTexture(gb.getItemStack().getItem().getTexture(), TextureResolution._16, gb.getItemStack().getItem().getMod()).getLocalPath())), vector3);

                        rc.addVoxel(rb);
                    }
                    clientChunk.setUpdatedHappened(false);

                    if(!nonAirFound) continue;
                    if (!hasRun) {
                        getServer().getExecutorService().submit(() -> {
                            rc.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                        });
                        hasRun = true;
                    } else {
                        rc.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                    }
                    getChunkHandler().addChunk(rc);
                    clientChunk.setVoxelID(rc.getId());
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

    public ChatComponent getChatComponent() {
        return chatComponent;
    }

    private ClientServerController getController() {
        return (ClientServerController) getServer().getServerController();
    }

    private VoxelTexture getTexture(ItemStack is) {
        return renderResourceManager.get(GameResourceManager.correctPath(Kakara.getGameInstance().getResourceManager().getTexture(is.getItem().getTexture(), TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }
}
