package org.kakara.client.scenes.maingamescene;


import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.client.game.player.PlayerContentInventory;
import org.kakara.client.game.world.ClientChunk;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.client.scenes.BreakingBlock;
import org.kakara.client.scenes.canvases.DebugModeCanvas;
import org.kakara.client.scenes.canvases.HotBarCanvas;
import org.kakara.client.scenes.canvases.PauseMenuCanvas;
import org.kakara.client.scenes.uicomponenets.ChatComponent;
import org.kakara.client.scenes.uicomponenets.events.ChatBlurEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatFocusEvent;
import org.kakara.client.scenes.uicomponenets.events.ChatSendEvent;
import org.kakara.core.Kakara;
import org.kakara.core.Status;
import org.kakara.engine.engine.CubeData;
import org.kakara.engine.events.EventHandler;
import org.kakara.core.resources.TextureResolution;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.engine.GameHandler;
import org.kakara.engine.physics.collision.Collidable;
import org.kakara.engine.events.event.KeyPressEvent;
import org.kakara.engine.events.event.MouseClickEvent;
import org.kakara.engine.input.MouseClickType;
import org.kakara.engine.item.*;
import org.kakara.engine.item.mesh.Mesh;
import org.kakara.engine.math.Vector2;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.models.TextureCache;
import org.kakara.engine.renderobjects.RenderBlock;
import org.kakara.engine.renderobjects.RenderChunk;
import org.kakara.engine.renderobjects.RenderTexture;
import org.kakara.engine.renderobjects.TextureAtlas;
import org.kakara.engine.renderobjects.mesh.MeshType;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.resources.ResourceManager;
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

import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public class MainGameScene extends AbstractGameScene {
    protected final KakaraGame kakaraGame;
    protected final Server server;
    protected ChatComponent chatComponent;
    protected BreakingBlock breakingBlock = null;
    protected HotBarCanvas hotBarCanvas;
    protected MeshGameItem blockSelector;
    protected final RenderResourceManager renderResourceManager = new RenderResourceManager(this);
    protected final PlayerMovement movement = new PlayerMovement(this);
    protected final SceneUtils sceneUtils = new SceneUtils(this);
    protected final GameChunkManager gameChunkManager = new GameChunkManager(this);
    protected RenderTexture breakingTexture;

    public MainGameScene(GameHandler gameHandler, Server server, KakaraGame kakaraGame) {
        super(gameHandler);
        setCurserStatus(false);
        this.server = server;
        this.kakaraGame = kakaraGame;

        if (server instanceof IntegratedServer) {
            ((IntegratedServer) server).setSceneTickUpdate(this::gameSceneUpdate);
        }
    }


    @Override
    public void work() {

    }


    @Override
    public void loadGraphics(GameHandler handler) {
        getHUD().addItem(DebugModeCanvas.getInstance(kakaraGame, this));
        getHUD().addItem(PauseMenuCanvas.getInstance(kakaraGame, this));

        var resourceManager = gameHandler.getResourceManager();
        List<RenderTexture> textures = new ArrayList<>();

        for (org.kakara.core.resources.Texture resource : Kakara.getResourceManager().getAllTextures()) {
            RenderTexture txt1 = new RenderTexture(resourceManager.getResource(resource.get().getLocalPath()));
            textures.add(txt1);

        }
        breakingTexture = new RenderTexture(resourceManager.getResource("breaking/breaking.png"));
        textures.add(breakingTexture);
        File file = new File(Kakara.getWorkingDirectory(), "tmp");
        if (!file.exists()) {
            if (!file.mkdir()) {
                //I am pretty lazy
                Kakara.LOGGER.error("Unable to create tmp folder");
                return;
            }
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

        hotBarCanvas = new HotBarCanvas(this, getTextureAtlas(), renderResourceManager, (PlayerContentInventory) server.getPlayerEntity().getInventory());
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
            ((ClientPlayer) server.getPlayerEntity()).setGameItemID(sceneUtils.createPlayerObject());
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

        if (chatComponent != null) {
            if (server instanceof IntegratedServer) {
                ((IntegratedServer) server).newMessages().forEach(s -> chatComponent.addMessage(s));
            }
        }

//        long l = System.currentTimeMillis();
//        gameChunkManager.update();
//        System.out.println(l - System.currentTimeMillis());
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
        UUID playerID = ((ClientPlayer) server.getPlayerEntity()).getGameItemID().get();
        if (evt.getMouseClickType() == MouseClickType.RIGHT_CLICK && !chatComponent.isFocused()) {
            Collidable col = this.selectGameItems(20, playerID);
            if (col instanceof RenderBlock) {
                RenderBlock rb = (RenderBlock) col;
                RenderChunk parentChunk = rb.getParentChunk();

                Vector3 absoluteBlockPos = rb.getPosition().add(parentChunk.getPosition());

                final Vector3 closestValue = ObjectPickingUtils.closestValue(absoluteBlockPos, getCamera());
                ChunkLocation chunkLoc = GameUtils.getChunkLocation(new Location(server.getPlayerEntity().getLocation().getNullableWorld(), closestValue.x, closestValue.y, closestValue.z));
                if (server.getPlayerEntity().getLocation().getWorld().isEmpty()) return;
                Chunk chunk = (server.getPlayerEntity().getLocation().getWorld().get()).getChunkAt(chunkLoc);
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
                if (!desiredChunk.getOctChunk().find((int) newBlockLoc.x, (int) newBlockLoc.y, (int) newBlockLoc.z)) {
                    //IGNORE Airs
                    if (hotBarCanvas.getCurrentItemStack().getItem() instanceof AirBlock) return;
                    RenderBlock rbs = new RenderBlock(new BlockLayout(),
                            renderResourceManager.get(GameResourceManager.correctPath(Kakara.getResourceManager().getTexture(hotBarCanvas.getCurrentItemStack().getItem().getTexture(), TextureResolution._16, hotBarCanvas.getCurrentItemStack().getItem().getMod()).getLocalPath())), newBlockLoc);

                    desiredChunk.addBlock(rbs);
                    desiredChunk.regenerateChunk(getTextureAtlas(), MeshType.SYNC);
                    //THIS might work?
                    chunkLoc.getNullableWorld().setBlock(hotBarCanvas.getCurrentItemStack(), MoreUtils.vector3ToLocation(newBlockLoc.add(desiredChunk.getPosition()), chunkLoc.getNullableWorld()));

                }


            }

        }

    }

    /**
     * Update from the integrated server
     */
    public void gameSceneUpdate() {
        if (server.getPlayerEntity().getLocation().getWorld().isEmpty()) return;
        if (server.getPlayerEntity() == null || chatComponent == null) return;

        if(server.getPlayerEntity().getLocation().getNullableWorld() == null) return;

        for(Chunk loadedChunk : ((ClientWorld)server.getPlayerEntity().getLocation().getNullableWorld()).getChunksNow()){
            if (loadedChunk.getStatus() != Status.LOADED) continue;
            ClientChunk clientChunk = (ClientChunk) loadedChunk;
            if(!GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS)){
                if (clientChunk.getRenderChunkID().isPresent())
                    getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
//                System.out.println("Chunk Unloaded.");

                // Maybe: TODO The operation should be done below instead of in the ChunkCleaner
//                server.getPlayerEntity().getLocation().getNullableWorld().unloadChunk(clientChunk);
                continue;
            }

            ChunkLocation playerLocation = GameUtils.getChunkLocation(server.getPlayerEntity().getLocation());

            if(GameUtils.isLocationOnPerimeter(playerLocation, clientChunk.getLocation(), IntegratedServer.RADIUS * 16)) continue;

            ChunkLocation nextLocation = loadedChunk.getLocation().add(16, 0, 0);

            if(playerLocation.getNullableWorld() == null){
                System.out.println("WTF>");
                continue;
            }

            if(GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)){
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(-16, 0, 0);
            if(GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)){
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, 16, 0);
            if(GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)){
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, -16, 0);
            if(GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)){
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, 0, 16);
            if(GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS * 16)){
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }
            nextLocation = loadedChunk.getLocation().add(0, 0, -16);
            if(GameUtils.isLocationInsideCurrentLocationRadius(playerLocation, nextLocation, IntegratedServer.RADIUS* 16)){
                playerLocation.getNullableWorld().getChunkAt(nextLocation);
            }

            if (clientChunk.getRenderChunkID().isEmpty() || clientChunk.isUpdatedHappened()) {
                if (clientChunk.getRenderChunkID().isPresent())
                    getChunkHandler().removeChunk(clientChunk.getRenderChunkID().get());
                if (GameUtils.isLocationInsideCurrentLocationRadius(GameUtils.getChunkLocation(server.getPlayerEntity().getLocation()), loadedChunk.getLocation(), IntegratedServer.RADIUS* 16)) {
                    ChunkLocation cb = loadedChunk.getLocation();
                    RenderChunk rc = new RenderChunk(new ArrayList<>(), getTextureAtlas());
                    rc.setPosition(cb.getX(), cb.getY(), cb.getZ());

                    for (GameBlock gb : loadedChunk.getGameBlocks()) {
                        if (gb.getItemStack().getItem().getId() == 0) continue;
                        Vector3 vector3 = MoreUtils.locationToVector3(gb.getLocation());
                        vector3 = vector3.subtract(cb.getX(), cb.getY(), cb.getZ());
                        RenderBlock rb = new RenderBlock(new BlockLayout(),
                                renderResourceManager.get
                                        ((Kakara.getResourceManager().getTexture(gb.getItemStack().getItem().getTexture(), TextureResolution._16, gb.getItemStack().getItem().getMod()).getLocalPath())), vector3);

                        rc.addBlock(rb);
                    }
                    clientChunk.setUpdatedHappened(false);
                    //scene.server.getExecutorService().submit(() -> {
                    rc.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                    //});
                    getChunkHandler().addChunk(rc);
                    clientChunk.setRenderChunkID(rc.getId());
                }
            }

        }


        // Honestly, This shouldn't be here:
        ClientPlayer player = (ClientPlayer) server.getPlayerEntity();
        if (player.getGameItemID().isEmpty()) return;
        if (kakaraGame.getGameHandler().getMouseInput().isLeftButtonPressed() && !chatComponent.isFocused()) {
            Collidable col = this.selectGameItems(20, player.getGameItemID().get());
            if (col instanceof RenderBlock) {
                RenderBlock rb = (RenderBlock) col;
                RenderChunk parentChunk = rb.getParentChunk();
                Location location = new Location(parentChunk.getPosition().x + rb.getPosition().x, parentChunk.getPosition().y + rb.getPosition().y, parentChunk.getPosition().z + rb.getPosition().z);
                if (breakingBlock == null || !breakingBlock.getBlockLocation().equals(location)) {
                    breakingBlock = new BreakingBlock(location);
                    rb.setOverlay(breakingTexture);
                    parentChunk.regenerateOverlayTextures(getTextureAtlas());
                } else {
                    if (breakingBlock.breakBlock(0.005d)) {
                        System.out.println("Breaking");

                        long start = System.nanoTime();
                        parentChunk.removeBlock(rb);
                        parentChunk.regenerateChunk(getTextureAtlas(), MeshType.MULTITHREAD);
                        System.out.println(start - System.nanoTime());
                        //server.getPlayerEntity().getLocation().getNullableWorld().setBlock(Kakara.createItemStack(Kakara.getItemManager().getItem(0).get()), location);
                        breakingBlock = null;
                    }
                }
            }
        }
    }

    public Server getServer() {
        return server;
    }

    public ResourceManager getResourceManager() {
        return gameHandler.getResourceManager();
    }

    public void close() {
        PauseMenuCanvas.getInstance(kakaraGame, this).close();
        DebugModeCanvas.getInstance(kakaraGame, this).close();

    }
}
