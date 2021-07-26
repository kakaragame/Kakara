package org.kakara.client.scenes.maingamescene;

import org.kakara.client.Client;
import org.kakara.client.ClientServerController;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.engine.item.HorizontalRotationComponent;
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
import org.kakara.client.scenes.maingamescene.components.DroppedItemComponent;
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
import org.kakara.engine.physics.collision.PhysicsComponent;
import org.kakara.engine.physics.collision.VoxelCollider;
import org.kakara.engine.resources.Resource;
import org.kakara.engine.resources.ResourceManager;
import org.kakara.engine.scene.AbstractGameScene;
import org.kakara.engine.ui.canvases.ComponentCanvas;
import org.kakara.engine.ui.components.shapes.Rectangle;
import org.kakara.engine.ui.constraints.HorizontalCenterConstraint;
import org.kakara.engine.ui.constraints.VerticalCenterConstraint;
import org.kakara.engine.ui.font.Font;
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

/**
 * The main scene for the Kakara Video Game.
 */
public class MainGameScene extends AbstractGameScene {
    protected final KakaraGame kakaraGame;
    protected final RenderResourceManager renderResourceManager = new RenderResourceManager(this);
    protected final SceneUtils sceneUtils = new SceneUtils(this);
    protected ChatComponent chatComponent;
    protected BreakingBlock breakingBlock = null;
    protected HotBarCanvas hotBarCanvas;
    protected GameItem blockSelector;
    protected VoxelTexture breakingTexture;

    private final Client client;
    private final Queue<Runnable> updateOnMainThread = new LinkedBlockingQueue<>();
    private boolean hasRun = false;

    /**
     * Construct the Main Game Scene.
     *
     * @param gameHandler The game handler instance.
     * @param client      The instance of the client.
     * @param kakaraGame  The instance to the main KakaraGame class.
     */
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
        getUserInterface().addItem(PauseMenuCanvas.getInstance(this));

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
        main.setTag("main_canvas");
        main.setAutoScale(false);
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
        indicator.setTag("main_canvas_indicator");
        main.add(indicator);
        indicator.setParentCanvas(main);
        indicator.addConstraint(new HorizontalCenterConstraint());
        indicator.addConstraint(new VerticalCenterConstraint());

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

    /**
     * Load the skybox texture.
     *
     * @return The Skybox texture.
     */
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
            PauseMenuCanvas.getInstance(this).switchStatus();
            KakaraGame.LOGGER.error("Game update exception thrown", e);
        }
    }

    /**
     * What should be updated on every frame by the client.
     */
    private void internalUpdate() {
        DebugModeCanvas.getInstance(kakaraGame, this).update();
        hotBarCanvas.update();

        // Render the dropped items.
        renderDroppedItems();

        // Exectue things that need to be done on the main thread.
        synchronized (updateOnMainThread) {
            while (!updateOnMainThread.isEmpty()) {
                updateOnMainThread.poll().run();
            }
        }

        // If left click, break block.
        if (kakaraGame.getGameHandler().getMouseInput().isLeftButtonPressed()) {
            blockBreakHandler();
        }
    }

    /**
     * Handles the breaking of blocks.
     */
    private void blockBreakHandler() {
        ClientPlayer player = (ClientPlayer) getServer().getPlayerEntity();
        if (player.getGameItemID().isEmpty()) return;
        if (kakaraGame.getGameHandler().getMouseInput().isLeftButtonPressed() && !chatComponent.isFocused()) {
            if (player.getGameItemID().isEmpty()) {
                KakaraGame.LOGGER.warn("Player is not initialized.");
                return;
            }

            // If the pause menu is open, do nothing on click.
            if (PauseMenuCanvas.getInstance(this).isActivated()) return;

            // Select items within a radius of 20, ignore the Player's UUID and dropped items.
            ColliderComponent col = this.selectGameItems(20, Collections.singletonList(player.getGameItemID().get()), Collections.singletonList("pickupable"));
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
                            ((ClientServerController) getServer().getServerController()).blockBreak(location);
                        }
                    });
                    if (blockAt.isEmpty()) {
                        KakaraGame.LOGGER.error("Attempting to break an empty block.");
                    }
                }
            }
        }
    }

    /**
     * This method will create GameItems for dropped items.
     *
     * <p>Every update it loops through and checks to see if all dropped items on the server are mapped to a game item.</p>
     * <p>
     * TODO:: Do something better here.
     */
    public void renderDroppedItems() {
        for (DroppedItem droppedItem : ((ClientWorld) getServer().getPlayerEntity().getLocation().getNullableWorld()).getDroppedItems()) {
            if (droppedItem.getGameID() == null) {
                AtlasMesh mesh = new AtlasMesh(getTexture(droppedItem.getItemStack()), getTextureAtlas(), new BlockLayout(), CubeData.vertex, CubeData.normal, CubeData.indices);
                GameItem droppedBlock = new GameItem(mesh);
                BoxCollider collider = droppedBlock.addComponent(BoxCollider.class);
                PhysicsComponent physicsComponent = droppedBlock.addComponent(PhysicsComponent.class);
                physicsComponent.setVelocityY(-9.18f);
                physicsComponent.setResolve(true);
                droppedBlock.addComponent(HorizontalRotationComponent.class);
                // Only collide with Voxel Chunks.
                collider.setPredicate(collidable -> !(collidable instanceof VoxelCollider));
                droppedBlock.transform.setScale(0.3f);
                droppedBlock.transform.setPosition((float) droppedItem.getLocation().getX(), (float) droppedItem.getLocation().getY(), (float) droppedItem.getLocation().getZ());
                droppedBlock.setTag("pickupable");
                droppedBlock.getData().add(droppedItem.getItemStack().getItem().getControllerKey());
                droppedBlock.addComponent(DroppedItemComponent.class).setDroppedItem(droppedItem);
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
            PauseMenuCanvas.getInstance(this).switchStatus();
        }
    }

    @EventHandler
    public void onMousePress(MouseClickEvent evt) {
        // If the pause menu is open, do nothing on click.
        if (PauseMenuCanvas.getInstance(this).isActivated()) return;

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

                // If the height limit is reached.
                if (rcc.isEmpty()) {
                    KakaraGame.LOGGER.debug("Height limit reached.");
                    return;
                }


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

        for (Chunk loadedChunk : new ArrayList<>(((ClientWorld) getServer().getPlayerEntity().getLocation().getNullableWorld()).getChunksNow())) {
            if (loadedChunk.getStatus() != Status.LOADED) continue;
            ClientChunk clientChunk = (ClientChunk) loadedChunk;
            if (!GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(getServer().getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)) {
                if (clientChunk.getVoxelID().isPresent()) {
                    getChunkHandler().removeChunk(clientChunk.getVoxelID().get());
                    // TODO :: Make chunk unloading work.
//                    getServer().getPlayerEntity().getLocation().getNullableWorld().unloadChunk(clientChunk);
                }
//                System.out.println("Chunk Unloaded.");


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

                    if (!nonAirFound) continue;
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

    /**
     * Get the RenderResourceManager.
     *
     * @return The RenderResourceManager.
     */
    public RenderResourceManager getRenderResourceManager() {
        return renderResourceManager;
    }

    /**
     * The Server responsible for game logic.
     * <p>This is usually {@link IntegratedServer}.</p>
     *
     * @return The server responsible for game logic.
     */
    public Server getServer() {
        return client.getServer();
    }

    /**
     * Get the Engine's ResourceManager.
     *
     * @return The Engine's ResourceManager.
     */
    public ResourceManager getResourceManager() {
        return gameHandler.getResourceManager();
    }

    /**
     * Get the HotBar Canvas.
     *
     * @return The HotBar Canvas.
     */
    public HotBarCanvas getHotBar() {
        return hotBarCanvas;
    }

    /**
     * Add a runnable to be executed on the main game thread.
     *
     * @param run The runnable to add.
     */
    public void addQueueRunnable(Runnable run) {
        this.updateOnMainThread.add(run);
    }

    /**
     * Close the Pause and Debug canvases.
     */
    public void close() {
        PauseMenuCanvas.getInstance(this).close();
        DebugModeCanvas.getInstance(kakaraGame, this).close();
    }

    /**
     * Get the chat component.
     *
     * @return The chat component.
     */
    public ChatComponent getChatComponent() {
        return chatComponent;
    }

    /**
     * Get the Client Server Controller.
     *
     * @return The Client Server Controller.
     */
    private ClientServerController getController() {
        return (ClientServerController) getServer().getServerController();
    }

    /**
     * Get the Voxel Texture from and ItemStack.
     *
     * @param is The item stack.
     * @return The Voxel Texture.
     */
    private VoxelTexture getTexture(ItemStack is) {
        return renderResourceManager.get(GameResourceManager.correctPath(Kakara.getGameInstance().getResourceManager().getTexture(is.getItem().getTexture(), TextureResolution._16, is.getItem().getMod()).getLocalPath()));
    }
}
