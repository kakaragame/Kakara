package org.kakara.client.game;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.kakara.client.Client;
import org.kakara.client.KakaraGame;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.commands.*;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.client.game.world.ClientWorldManager;
import org.kakara.client.utils.IntegratedTime;
import org.kakara.core.Kakara;
import org.kakara.core.Status;
import org.kakara.core.Utils;
import org.kakara.core.client.Save;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.game.DefaultGameMode;
import org.kakara.core.mod.UnModObject;
import org.kakara.core.mod.game.GameMod;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.Location;
import org.kakara.engine.utils.Time;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.ServerLoadException;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.mod.KakaraMod;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.kakara.client.KakaraGame.LOGGER;

public class IntegratedServer extends Thread implements Server {
    @NotNull
    private final Save save;
    @NotNull
    private final UUID playerID;
    //This will later be a Setting.
    public static final int RADIUS = 8;
    private final ExecutorService executorService;
    private final List<Player> players = new ArrayList<>();
    private final File playersFolder;
    private boolean running = true;
    private Player player;
    private List<String> messages = new ArrayList<>();
    private final IntegratedTime time = new IntegratedTime();
    private Runnable sceneTickUpdate;
    private Location lastLocation;
    private Status status = Status.LOADED;

    public IntegratedServer(@NotNull Save save, @NotNull UUID playerUUID, Runnable sceneTickUpdate) throws ServerLoadException {
        super("Kakara-IntegratedServer");
        this.save = save;
        this.sceneTickUpdate = sceneTickUpdate;
        if (save instanceof ClientSave) {
            ((ClientSave) save).setServer(this);
        }
        ((Client) Kakara.getGameInstance()).setWorldManager(new ClientWorldManager(this));
        executorService = Executors.newFixedThreadPool(MoreUtils.getPoolSize());
        playersFolder = new File(save.getSaveFolder(), "players");
        if (!playersFolder.exists()) playersFolder.mkdir();
        this.playerID = playerUUID;
        LOGGER.info("Loading Mod Classes");
        List<UnModObject> modsToBeLoaded = Kakara.getModManager().loadModsFile(save.getModsToLoad());
        LOGGER.info("Enabling Mods");
        Kakara.getModManager().loadMods(modsToBeLoaded);
        Kakara.getModManager().loadStage(Kakara.getEventManager());
        Kakara.getModManager().loadStage(Kakara.getItemManager());
        Kakara.getModManager().loadStage(Kakara.getCommandManager());
        Kakara.getItemManager().registerItem(new AirBlock());
        Kakara.getModManager().loadStage(Kakara.getWorldGenerationManager());
        LOGGER.info("Loading Worlds");
        try {
            save.prepareWorlds();
        } catch (WorldLoadException e) {
            throw new ServerLoadException(e);
        }


        player = getOnlinePlayer(playerUUID);
        //DONT EVER DO THIS
        Kakara.getCommandManager().registerCommand(new StatusCommand(KakaraMod.getInstance(), this));
        Kakara.getCommandManager().registerCommand(new KillCommand(KakaraMod.getInstance(), this));
        Kakara.getCommandManager().registerCommand(new SaveChunk(KakaraMod.getInstance(), this));
        Kakara.getCommandManager().registerCommand(new RegenerateCommand(KakaraMod.getInstance(), this));
        Kakara.getCommandManager().registerCommand(new GameModeCommand(KakaraMod.getInstance()));

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
        List<UnModObject> modsToLoad = Kakara.getModManager().loadModsFile(save.getModsToLoad());
        Kakara.getModManager().loadMods(modsToLoad);
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
        executorService.shutdown();
        save.getWorlds().forEach(world -> ((ClientWorld) world).close());
        Kakara.getModManager().getLoadedMods().forEach(Kakara.getModManager()::unloadMod);

        status = Status.UNLOADED;

    }


    @Override
    public void update() {
        if (getPlayerEntity().getLocation().getNullableWorld().getStatus() != Status.LOADED) return;
        if (sceneTickUpdate != null) sceneTickUpdate.run();
        if (getPlayerEntity() == null) return;
        Location start = getPlayerEntity().getLocation();
        if (lastLocation.equals(start)) return;
        lastLocation = start;
        if (start.getNullableWorld() == null) return;


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

    @Override
    public void close() {
        running = false;
    }

    public List<String> newMessages() {
        List<String> clone = new ArrayList<>(messages);
        messages = new ArrayList<>();
        return clone;
    }

    @Override
    public void renderMessageToConsole(String message) {
        messages.add(message);
    }

    public void setSceneTickUpdate(Runnable sceneTickUpdate) {
        this.sceneTickUpdate = sceneTickUpdate;
    }

    @Override
    public void errorClose(Exception e) {

    }
}
