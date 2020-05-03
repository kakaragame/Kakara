package org.kakara.client.game.world;

import org.kakara.core.data.Entry;
import org.kakara.core.data.Serializer;
import org.kakara.core.world.Chunk;

import java.util.List;
import java.util.stream.Collectors;

class ChunkFileSerializer extends Serializer<List<Chunk>> {
    public static final ChunkFileSerializer INSTANCE = new ChunkFileSerializer();

    private ChunkFileSerializer() {}

    @Override
    public Entry disassembleObject(List<Chunk> clientChunks) {
        return new Entry(clientChunks.stream().map(
                ChunkSerializer.INSTANCE::disassembleObject
        ).collect(Collectors.toList()));
    }

    @Override
    public List<Chunk> assembleObject(Entry entry) {
        return entry.list().stream().map(
                ChunkSerializer.INSTANCE::assembleObject
        ).collect(Collectors.toList());
    }
}