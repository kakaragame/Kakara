package org.kakara.client.game.world;

import org.kakara.core.data.Entry;
import org.kakara.core.data.Serializer;
import org.kakara.core.serializers.messagepack.GameBlockSerializer;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.GameBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ChunkSerializer extends Serializer<Chunk> {
    public static final ChunkSerializer INSTANCE = new ChunkSerializer();
    private static final GameBlockSerializer SERIALIZER = new GameBlockSerializer();

    private ChunkSerializer() {}

    @Override
    public Entry disassembleObject(Chunk chunk) {
        Map<String, Entry> map = new HashMap<>();
        map.put("l", ChunkLocationSerializer.INSTANCE.disassembleObject(chunk.getLocation()));
        map.put("g", new Entry(chunk.getGameBlocks().stream().map(
                SERIALIZER::disassembleObject
        ).collect(Collectors.toList())));

        return new Entry(map);
    }

    @Override
    public Chunk assembleObject(Entry entry) {
        Map<String, Entry> map = entry.map();

        ChunkLocation location = ChunkLocationSerializer.INSTANCE.assembleObject(map.get("l"));
        List<GameBlock> gameBlocks = map.get("g").list().stream().map(
                SERIALIZER::assembleObject
        ).collect(Collectors.toList());

        return new ClientChunk(location, gameBlocks);
    }
}