package org.kakara.client.game;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.kakara.client.ChunkCleaner;
import org.kakara.client.KakaraGame;
import org.kakara.client.game.commands.KillCommand;
import org.kakara.client.game.commands.SaveChunk;
import org.kakara.client.game.commands.StatusCommand;
import org.kakara.client.game.player.ClientPlayer;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.client.Save;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.mod.UnModObject;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.Location;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;
import org.kakara.game.items.blocks.AirBlock;
import org.kakara.game.mod.KakaraMod;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.kakara.client.KakaraGame.LOGGER;

public class IntegratedServer implements Server {
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
    private ChunkCleaner chunkCleaner;
    private KakaraMod kakaraMod = new KakaraMod();

    public IntegratedServer(@NotNull Save save, @NotNull UUID playerUUID) {
        this.save = save;
        if (save instanceof ClientSave) {
            ((ClientSave) save).setServer(this);
        }
        executorService = Executors.newFixedThreadPool(5);
        playersFolder = new File(save.getSaveFolder(), "players");
        if (!playersFolder.exists()) playersFolder.mkdir();
        this.playerID = playerUUID;
        LOGGER.info("Loading Mod Classes");
        List<UnModObject> modsToBeLoaded = Kakara.getModManager().loadModsFile(save.getModsToLoad());
        LOGGER.info("Enabling Mods");
        Kakara.getModManager().loadMods(modsToBeLoaded);
        Kakara.getModManager().loadStage(Kakara.getEventManager());
        Kakara.getModManager().loadStage(Kakara.getItemManager());
        Kakara.getItemManager().registerItem(new AirBlock(kakaraMod));
        Kakara.getModManager().loadStage(Kakara.getWorldGenerationManager());
        LOGGER.info("Loading Worlds");
        try {
            save.prepareWorlds();
        } catch (WorldLoadException e) {
            LOGGER.error("Unable to load worlds", e);
            //TODO cancel game load
        }
        chunkCleaner = new ChunkCleaner(this);
        chunkCleaner.start();
        player = getOnlinePlayer(playerUUID);
        //DONT EVER DO THIS
        Kakara.getCommandManager().registerCommand(new StatusCommand(kakaraMod, this));
        Kakara.getCommandManager().registerCommand(new KillCommand(kakaraMod, this));
        Kakara.getCommandManager().registerCommand(new SaveChunk(kakaraMod, this));
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
    public List<Chunk> chunksToRender() {
        //TODO do coolMath
        return null;
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
    public void update() {
        if (!running) {
            if (save instanceof ClientSave) {
                ((ClientSave) save).save();
            }

            return;
        }

        if (getPlayerEntity() == null) return;
        ChunkLocation start = GameUtils.getChunkLocation(getPlayerEntity().getLocation());
        for (int x = (start.getX() - (RADIUS * 16)); x <= (start.getX() + (RADIUS * 16)); x += 16) {
            for (int y = (start.getY() - (RADIUS * 16)); y <= (start.getY() + (RADIUS * 16)); y += 16) {
                for (int z = (start.getZ() - (RADIUS * 16)); z <= (start.getZ() + (RADIUS * 16)); z += 16) {
                    ChunkLocation chunkLocation = new ChunkLocation(x, y, z);
                    if (GameUtils.isLocationInsideCurrentLocationRadius(start, chunkLocation, RADIUS)) {
                        getPlayerEntity().getLocation().getWorld().get().getChunkAt(chunkLocation);
                    }
                }
            }
        }


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
}
