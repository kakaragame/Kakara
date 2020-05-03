package org.kakara.client.game.world;

import org.kakara.core.Kakara;
import org.kakara.core.data.Entry;
import org.kakara.core.data.Serializer;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.World;

import java.util.HashMap;
import java.util.Map;

class ChunkLocationSerializer extends Serializer<ChunkLocation> {
    public static final ChunkLocationSerializer INSTANCE = new ChunkLocationSerializer();

    private ChunkLocationSerializer() {}

    public Entry disassembleObject(ChunkLocation l) {
        Map<String, Entry> map = new HashMap<>();
        map.put("x", new Entry(l.getX()));
        map.put("y", new Entry(l.getY()));
        map.put("z", new Entry(l.getZ()));
        if (l.getWorld() != null) {
            map.put("w", new Entry(l.getWorld().getUUID()));
        }

        return new Entry(map);
    }

    public ChunkLocation assembleObject(Entry entry) {
        Map<String, Entry> map = entry.map();
        int x = map.get("x").integer();
        int y = map.get("y").integer();
        int z = map.get("z").integer();
        World world = null;
        if (map.containsKey("w")) {
            world = Kakara.getWorldManager().getWorldByUUID(map.get("w").uuid());
        }

        return new ChunkLocation(x, y, z, world);
    }
}
