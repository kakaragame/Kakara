package org.kakara.game;

import com.google.gson.JsonObject;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.client.Save;
import org.kakara.core.mod.UnModObject;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameEntity;
import org.kakara.core.world.Location;
import org.kakara.game.client.GameSave;
import org.kakara.game.client.TestSave;
import org.kakara.game.client.player.ClientPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IntegratedServer implements Server {
    private LocalDataWatcher dataWatcher;
    private Save save;
    private Player player;
    private int radius = 8;
    private ExecutorService executorService;
    private List<Player> players = new ArrayList<>();
    private File playersFolder;

    public IntegratedServer(LocalDataWatcher dataWatcher, Save save) {
        this.dataWatcher = dataWatcher;
        this.save = save;
        executorService = Executors.newFixedThreadPool(2);

        if (save instanceof GameSave) {
            ((GameSave) save).setServer(this);
        } else if (save instanceof TestSave) {
            ((TestSave) save).setServer(this);
        }
    }

    private Player getOnlinePlayer(UUID uuid) {
        File playerFile = new File(playersFolder, uuid.toString() + ".json");
        if (!playerFile.exists()) {
            createNewPlayer(uuid);
        }
        try {
            JsonObject jsonObject = Utils.getGson().fromJson(new FileReader(playerFile), JsonObject.class);
            return new ClientPlayer(jsonObject, new Location(save.getDefaultWorld(), 0, 0, 0), this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

    @Override
    public void loadPlayer(UUID uuid) {
        playersFolder = new File(save.getSaveFolder(), "players");
        if (!playersFolder.exists()) playersFolder.mkdir();
        this.player = getOnlinePlayer(uuid);
        players.add(player);

    }

    @Override
    public void loadWorld() {
        save.prepareWorlds();
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
        if (player == null) return;
        ChunkLocation start = GameUtils.getChunkLocation(player.getLocation());
        for (int x = start.getX(); x <= (start.getX() + (radius * 16)); x = x + 16) {
            for (int y = start.getY(); y <= (start.getY() + (radius * 16)); y = y + 16) {
                for (int z = start.getZ(); z <= (start.getZ() + (radius * 16)); z = z + 16) {
                    ChunkLocation chunkLocation = new ChunkLocation(x, y, z);
                    if (GameUtils.isLocationInsideCurrentLocationRadius(start, chunkLocation, radius)) {
                       player.getLocation().getWorld().getChunkAt(chunkLocation);
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
}
