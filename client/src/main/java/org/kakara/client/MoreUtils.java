package org.kakara.client;

import org.kakara.core.game.ItemStack;
import org.kakara.core.resources.Resource;
import org.kakara.core.world.ChunkBase;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;
import org.kakara.core.world.World;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.resources.ResourceManager;

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


}
