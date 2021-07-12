package org.kakara.client;


import org.kakara.core.common.game.ItemStack;
import org.kakara.core.common.resources.Resource;
import org.kakara.core.common.world.*;
import org.kakara.engine.GameHandler;
import org.kakara.engine.gameitems.Texture;
import org.kakara.engine.math.Vector3;

import java.io.File;
import java.util.*;

/**
 * A Standard Utility Class used throughout the Client.
 */
public final class MoreUtils {

    private MoreUtils() {
    }

    /**
     * Convert a String to a String Array.
     *
     * @param property The input list as a string separated by commas (,).
     * @return The String Array.
     */
    public static String[] stringToStringArray(String property) {
        return property.split(",");
    }

    /**
     * Convert a location to a Vector 3.
     *
     * @param loc The location.
     * @return The Vector3.
     */
    public static Vector3 locationToVector3(Location loc) {
        return new Vector3(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Convert a Vector3 to a Location.
     *
     * @param vec   The Vector
     * @param world The World
     * @return The Location
     */
    public static Location vector3ToLocation(Vector3 vec, World world) {
        return new Location(world, vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Convert a Vector3 to a Location without a World.
     *
     * @param vec The Vector.
     * @return The Location.
     */
    public static Location vector3ToLocation(Vector3 vec) {
        return new Location(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Convert a Core Resource to an engine resource.
     *
     * @param resource   The Core Resource to convert.
     * @param kakaraGame The instance of KakaraGame.
     * @return The Game Engine Resource.
     */
    public static org.kakara.engine.resources.Resource coreResourceToEngineResource(Resource resource, KakaraGame kakaraGame) {
        return kakaraGame.getGameHandler().getResourceManager().getResource(resource.getLocalPath());

    }

    public static Map<ItemStack, List<Location>> sortByType(List<ChunkBase> mehChunks) {
        Map<ItemStack, List<Location>> map = new HashMap<>();
        for (ChunkBase chunkBase : mehChunks) {
            for (GameBlock gameBlock : chunkBase.getGameBlocks()) {
                map.computeIfAbsent(gameBlock.getItemStack(), gameBlock1 -> new ArrayList<>()).add(gameBlock.getLocation());
            }
        }

        return map;
    }

    /**
     * Remove the first item in an array.
     *
     * @param strings The array of strings.
     * @return The array without the first index.
     */
    public static String[] removeFirst(String[] strings) {
        return Arrays.asList(strings).subList(1, strings.length).toArray(String[]::new);
    }

    /**
     * Get the number of blocks within the gameBlockMap
     *
     * @param gameBlockMutableIntMap The game block map.
     * @return The number of blocks within the gameBlockMap.
     */
    public static int calculateSize(Map<ItemStack, List<Location>> gameBlockMutableIntMap) {
        int i = 0;
        for (Map.Entry<ItemStack, List<Location>> itemStackListEntry : gameBlockMutableIntMap.entrySet()) {
            i += itemStackListEntry.getValue().size();
        }
        return i;
    }


    /**
     * Get the number of thread pools from the system properties.
     *
     * @return The number of thread pools.
     */
    public static int getPoolSize() {
        if (!System.getProperties().containsKey("kakara.pool.size")) {
            int suggested = Runtime.getRuntime().availableProcessors() / 2;
            return Math.max(suggested, 4);
        }
        return Integer.parseInt(System.getProperty("kakara.pool.size"));
    }

    /**
     * Convert a chunk location to Vector3.
     *
     * @param c The chunk location.
     * @return The Vector3.
     */
    public static Vector3 chunkLocationToVector3(ChunkLocation c) {

        return new Vector3(c.getX(), c.getY(), c.getZ());
    }

    /**
     * Convert the Location of a GameBlock to the location of a RenderBlock.
     *
     * @param location       The gameblock rotation.
     * @param renderChunkVec The vector with the location of the RenderChunk.
     * @return The location of the RenderBlock.
     */
    public static Vector3 gbLocationToRBLocation(Location location, Vector3 renderChunkVec) {
        Vector3 vector3 = locationToVector3(location);
        vector3 = vector3.subtract(renderChunkVec.getX(), renderChunkVec.getY(), renderChunkVec.getZ());
        return vector3;
    }

    public static void getFiles(File directory, List<File> files) {
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null)
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    getFiles(file.getAbsoluteFile(), files);
                }
            }
    }

    /**
     * Convert a Core Texture to an Engine Texture.
     *
     * <p>Note: Textures are per scene. You must reconvert textures for each scene.</p>
     * <p>Note: Textures should only be converted once. Please cache this result.</p>
     *
     * @param coreTexture The core texture.
     * @return The Engine Texture.
     */
    public static Texture coreTextureToEngineTexture(org.kakara.core.common.resources.Texture coreTexture) {
        return new Texture(coreResourceToEngineResource(coreTexture.get(), KakaraGame.getInstance()), GameHandler.getInstance().getCurrentScene());
    }
}

