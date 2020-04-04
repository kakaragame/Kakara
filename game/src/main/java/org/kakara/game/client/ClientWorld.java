package org.kakara.game.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.client.Save;
import org.kakara.core.exceptions.WorldLoadException;
import org.kakara.core.game.Block;
import org.kakara.core.game.ItemStack;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.core.world.World;

import java.io.File;
import java.util.UUID;

public class ClientWorld implements World {
    private File file;
    private JsonObject worldSettings;
    private Location worldSpawn;
    private ClientChunkWriter clientChunkWriter;

    public ClientWorld(JsonElement element, GameSave save) throws WorldLoadException {
        file = new File(save.getSaveFolder(), element.getAsString());
        clientChunkWriter = new ClientChunkWriter(this);
    }

    @Override
    public Chunk[] getChunks() {
        return new Chunk[0];
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public GameBlock getBlockAt(int i, int i1, int i2) {
        return null;
    }

    @Override
    public GameBlock getBlockAt(Location location) {
        return null;
    }

    @Override
    public GameBlock setBlock(ItemStack itemStack, Location location) {
        return null;
    }

    @Override
    public GameBlock setBlock(Block block, Location location) {
        return null;
    }

    @Override
    public Location getWorldSpawn() {
        return worldSpawn;
    }

    @Override
    public void setWorldSpawn(Location location) {
        worldSpawn = location;
    }

    @Override
    public Chunk getChunkAt(int i, int i1) {
        return null;
    }

    @Override
    public Chunk getChunkAt(Location location) {
        return null;
    }

    @Override
    public void loadChunk(Chunk chunk) {

    }

    @Override
    public boolean isChunkLoaded(int i, int i1) {
        return false;
    }

    @Override
    public Chunk[] getLoadedChunks() {
        return new Chunk[0];
    }

    public File getFile() {
        return file;
    }

    public ClientChunkWriter getClientChunkWriter() {
        return clientChunkWriter;
    }
}
