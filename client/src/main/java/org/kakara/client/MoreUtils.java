package org.kakara.client;

import org.kakara.core.Kakara;
import org.kakara.core.game.ItemStack;
import org.kakara.core.resources.Resource;
import org.kakara.core.world.*;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.resources.ResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreUtils {
    public static String[] stringArrayToStringArray(String property) {
        return property.split(",");
    }

    public static Vector3 locationToVector3(Location c) {
        return new Vector3(c.getX(), c.getY(), c.getZ());
    }

    public static Location vector3ToLocation(Vector3 c, World world) {
        return new Location(world, c.getX(), c.getY(), c.getZ());
    }

    public static Location vector3ToLocation(Vector3 c) {
        return new Location(c.getX(), c.getY(), c.getZ());
    }

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

    public static String[] removeFirst(String[] strings) {
        List<String> strings1 = new ArrayList<>();
        for (int i = 1; i < strings.length; i++) {
            strings1.add(strings[i]);
        }
        return strings1.toArray(String[]::new);
    }

    public static int calculateSize(Map<ItemStack, List<Location>> gameBlockMutableIntMap) {
        int i = 0;
        for (Map.Entry<ItemStack, List<Location>> itemStackListEntry : gameBlockMutableIntMap.entrySet()) {
            i += itemStackListEntry.getValue().size();
        }
        return i;
    }


    public static List<ItemStack> listWithAir(int capacity) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            itemStacks.add(Kakara.createItemStack(Kakara.getItemManager().getItem(0).get()));
        }
        return itemStacks;
    }

    public static int getPoolSize() {
        if (System.getProperty("kakara.pool.size", "0").equals("0")) {
            int suggested = Runtime.getRuntime().availableProcessors() / 2;
            if (suggested < 4) {
                return 4;
            }
        }
        return Integer.parseInt(System.getProperty("kakara.pool.size"));
    }

    public static Vector3 chunkLocationToVector3(ChunkLocation c) {

        return new Vector3(c.getX(), c.getY(), c.getZ());
    }

    public static Vector3 gbLocationToRBLocation(Location location, Vector3 vector) {
        Vector3 vector3 = locationToVector3(location);
        vector3 = vector3.subtract(vector.getX(), vector.getY(), vector.getZ());
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
}

