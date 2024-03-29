package org.kakara.client.local.game;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.kakara.client.KakaraGame;
import org.kakara.client.LocalClient;
import org.kakara.client.MoreUtils;
import org.kakara.client.local.game.commands.GameModeCommand;
import org.kakara.client.local.game.commands.KillCommand;
import org.kakara.client.local.game.commands.StatusCommand;
import org.kakara.client.local.game.commands.TestInventoryCommand;
import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.client.local.game.player.PlayerContentInventory;
import org.kakara.client.local.game.world.ClientWorld;
import org.kakara.client.local.game.world.ClientWorldManager;
import org.kakara.client.scenes.LoadingScene;
import org.kakara.client.scenes.maingamescene.MainGameScene;
import org.kakara.client.utils.IntegratedTime;
import org.kakara.core.client.client.Save;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.Status;
import org.kakara.core.common.Utils;
import org.kakara.core.common.exceptions.WorldLoadException;
import org.kakara.core.common.game.DefaultGameMode;
import org.kakara.core.common.mod.UnModObject;
import org.kakara.core.common.player.OfflinePlayer;
import org.kakara.core.common.player.Player;
import org.kakara.core.common.world.ChunkLocation;
import org.kakara.core.common.world.Location;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.engine.GameHandler;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.ServerController;
import org.kakara.game.ServerLoadException;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.mod.KakaraMod;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.kakara.client.KakaraGame.LOGGER;

public class IntegratedServer extends Thread implements Server {
    //This will later be a Setting.
    public static final int RADIUS = 8;
    @NotNull
    private final Save save;
    @NotNull
    private final UUID playerID;
    private final ExecutorService executorService;
    private final List<Player> players = new ArrayList<>();
    private final File playersFolder;
    private final IntegratedTime time = new IntegratedTime();
    private boolean running = true;
    private final Player player;
    private Runnable sceneTickUpdate;
    private Location lastLocation;
    private volatile Status status = Status.LOADED;
    private final LocalServerController localServerController = new LocalServerController(this);
    private MainGameScene gameScene;

    // The percent for the unloading bar.
    private int unloadedPercent;

    public IntegratedServer(@NotNull Save save, @NotNull UUID playerUUID, Runnable sceneTickUpdate) throws ServerLoadException {
        super("Kakara-IntegratedServer");
        this.save = save;
        this.sceneTickUpdate = sceneTickUpdate;
        this.unloadedPercent = 0;

        if (save instanceof ClientSave) {
            ((ClientSave) save).setServer(this);
        }

        ((LocalClient) Kakara.getGameInstance()).setWorldManager(new ClientWorldManager(this));
        executorService = Executors.newFixedThreadPool(MoreUtils.getPoolSize());
        playersFolder = new File(save.getSaveFolder(), "players");
        if (!playersFolder.exists()) playersFolder.mkdir();
        this.playerID = playerUUID;
        LOGGER.info("Loading Mod Classes");
        System.out.println("save.getModsToLoad().size() = " + save.getModsToLoad().size());
        List<UnModObject> modsToBeLoaded = Kakara.getGameInstance().getModManager().loadModsFile(save.getModsToLoad());
        LOGGER.info("Enabling Mods");
        Kakara.getGameInstance().getModManager().loadMods(modsToBeLoaded);
        Kakara.getGameInstance().getModManager().loadStage(Kakara.getGameInstance().getEventManager());
        Kakara.getGameInstance().getModManager().loadStage(Kakara.getGameInstance().getItemRegistry());
        Kakara.getGameInstance().getModManager().loadStage(Kakara.getGameInstance().getCommandManager());
        Kakara.getGameInstance().getItemRegistry().registerItem(new AirBlock());
        System.out.println("Kakara.getEnvironmentInstance().getType() = " + Kakara.getEnvironmentInstance().getType());
        Kakara.getGameInstance().getModManager().loadStage(((ServerGameInstance) Kakara.getGameInstance()).getWorldGenerationRegistry());
        LOGGER.info("Loading Worlds");
        try {
            save.prepareWorlds();
        } catch (WorldLoadException e) {
            throw new ServerLoadException(e);
        }


        player = getOnlinePlayer(playerUUID);
        //DONT EVER DO THIS
        Kakara.getGameInstance().getCommandManager().registerCommand(new StatusCommand(KakaraMod.getInstance(), this));
        Kakara.getGameInstance().getCommandManager().registerCommand(new KillCommand(KakaraMod.getInstance(), this));
        Kakara.getGameInstance().getCommandManager().registerCommand(new GameModeCommand(KakaraMod.getInstance()));
        Kakara.getGameInstance().getCommandManager().registerCommand(new TestInventoryCommand(KakaraMod.getInstance(), this));

        lastLocation = player.getLocation();
    }

    public Player getOnlinePlayer(UUID uuid) {
        Optional<Player> optional = players.stream().filter(player1 -> player1.getUUID().equals(uuid)).findFirst();
        if (optional.isPresent()) return optional.get();
        File playerFile = new File(playersFolder, uuid.toString() + ".json");
        if (!playerFile.exists()) {
            createNewPlayer(uuid);
        }
        try {
            JsonObject jsonObject = Utils.getGson().fromJson(new FileReader(playerFile), JsonObject.class);
            ClientPlayer player = new ClientPlayer(jsonObject, new Location(save.getDefaultWorld(), 500, 80, 500), this);
            players.add(player);
            return player;
        } catch (FileNotFoundException e) {
            KakaraGame.LOGGER.error("Unable to locate file(I am confused how it go here) ", e);
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        File players = new File(playersFolder, uuid.toString() + ".json");
        if (!players.exists()) {
            createNewPlayer(uuid);
        }
        return null;
    }

    private void createNewPlayer(UUID uuid) {
        File playerFile = new File(playersFolder, uuid.toString() + ".json");
        try {
            playerFile.createNewFile();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", "KingTux");
            jsonObject.addProperty("gamemode", GameUtils.getGameMode(DefaultGameMode.CREATIVE));
            jsonObject.addProperty("uuid", uuid.toString());
            jsonObject.addProperty("lastjointime", System.currentTimeMillis());
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(playerFile));
            bufferedWriter.write(Utils.getGson().toJson(jsonObject));
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPlayer(UUID uuid) {

    }

    @Override
    public Player getPlayerEntity() {

        return player;
    }


    @Override
    public List<Player> getOnlinePlayers() {
        return players;
    }

    @Override
    public void loadMods() {
        List<UnModObject> modsToLoad = Kakara.getGameInstance().getModManager().loadModsFile(save.getModsToLoad());
        Kakara.getGameInstance().getModManager().loadMods(modsToLoad);
    }

    @Override
    public void run() {
        if (((ClientWorld) getPlayerEntity().getLocation().getNullableWorld()).getChunksNow().isEmpty()) {
            Set<ChunkLocation> chunkLocations = calibrateChunks();
            ((ClientWorld) getPlayerEntity().getLocation().getNullableWorld()).initLoad(chunkLocations);
        }
        //Yes I stole this from Engine
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / 20;

        while (running) {
            elapsedTime = time.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval) {
                update();
                accumulator -= interval;
            }

        }
        this.status = Status.UNLOADING;
        KakaraGame.LOGGER.info("Unloading Integrated Server.");
        try {
            ((LoadingScene) GameHandler.getInstance().getCurrentScene()).setText("Stopping Threads... ");
            KakaraGame.LOGGER.debug("Stopping server executor service...");
            // Shut down now as no load operations should be done when the server is unloading.
            executorService.shutdownNow();
            this.unloadedPercent = 0;
            KakaraGame.LOGGER.debug("Unloading worlds...");
            save.getWorlds().forEach(world -> {
                ((LoadingScene) GameHandler.getInstance().getCurrentScene()).setText(String.format("Saving World %s... ", world.getName()));
                ((ClientWorld) world).close();
            });
            this.unloadedPercent = 100;
            ((LoadingScene) GameHandler.getInstance().getCurrentScene()).setText("Unloading mods...");
            KakaraGame.LOGGER.debug("Unloading mods...");
            Kakara.getGameInstance().getModManager().getLoadedMods().forEach(Kakara.getGameInstance().getModManager()::unloadMod);
        } catch (Exception ex) {
            KakaraGame.LOGGER.error("An error has occurred while stopping the server.", ex);
        } finally {
            status = Status.UNLOADED;
            Kakara.coreClose();
        }
    }


    /**
     * Check if a player is nearby dropped items.
     *
     * <p>This is a sever action.</p>
     */
    public void checkForDroppedItems() {
        ClientWorld clientWorld = (ClientWorld) getPlayerEntity().getLocation().getNullableWorld();
        List<DroppedItem> droppedItems = new ArrayList<>(clientWorld.getDroppedItems());
        for (DroppedItem droppedItem : droppedItems) {
            if (droppedItem.getGameID() != null) {
                // Radius needs to be higher than 1 to work properly.
                if (GameUtils.isLocationInsideCurrentLocationRadius(getPlayerEntity().getLocation(), droppedItem.getLocation(), 1.5d)) {
                    //TODO re add the inventory
                    getGameScene().remove(getGameScene().getItemHandler().getItemWithId(droppedItem.getGameID()).get());
                    clientWorld.getDroppedItems().remove(droppedItem);
                    ((PlayerContentInventory) getPlayerEntity().getInventory()).addItemStackForPickup(droppedItem.getItemStack());
                }
            }
        }
    }

    @Override
    public void update() {
        if (getPlayerEntity() == null) return;
        Location start = getPlayerEntity().getLocation();
        if (start.getNullableWorld() == null) return;
        checkForDroppedItems();

        if (getPlayerEntity().getLocation().getNullableWorld().getStatus() != Status.LOADED) return;

        if (sceneTickUpdate != null) {
            sceneTickUpdate.run();
        }
        if (lastLocation.equals(start)) return;
        lastLocation = start;


    }

    public Set<ChunkLocation> calibrateChunks() {
        Location start = getPlayerEntity().getLocation();
        Set<ChunkLocation> locations = new HashSet<>();
        for (int x = (int) (start.getX() - (RADIUS * 16)); x <= (start.getX() + (RADIUS * 16)); x += 16) {
            for (int y = (int) (start.getY() - (RADIUS * 16)); y <= (start.getY() + (RADIUS * 16)); y += 16) {
                for (int z = (int) (start.getZ() - (RADIUS * 16)); z <= (start.getZ() + (RADIUS * 16)); z += 16) {
                    if (GameUtils.isLocationInsideCurrentLocationRadius((int) start.getX(), (int) start.getY(), (int) start.getZ(), x, y, z, RADIUS)) {
                        locations.add(GameUtils.getChunkLocation(new Location(x, y, z)));
                    }
                }
            }
        }
        return locations;
    }

    @NotNull
    public Save getSave() {
        return save;
    }

    @Override
    public void tickUpdate() {

    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Get the loading percent.
     *
     * @return The loading percent.
     */
    @Override
    public int getPercent() {
        return ((ClientWorld) getSave().getDefaultWorld()).getChunkIO().getPercent();
    }

    /**
     * Set the unloaded percentage for the visual loading bar.
     *
     * @param unloadedPercent The unload percent to set.
     */
    public void setUnloadedPercent(int unloadedPercent) {
        this.unloadedPercent = unloadedPercent;
    }

    @Override
    public void close() {
        running = false;
    }


    public void setSceneTickUpdate(Runnable sceneTickUpdate) {
        this.sceneTickUpdate = sceneTickUpdate;
    }

    @Override
    public void errorClose(Exception e) {

    }

    public MainGameScene getGameScene() {
        if (gameScene == null) gameScene = (MainGameScene) GameHandler.getInstance().getCurrentScene();
        return gameScene;
    }

    @Override
    public ServerController getServerController() {
        return localServerController;
    }
}
