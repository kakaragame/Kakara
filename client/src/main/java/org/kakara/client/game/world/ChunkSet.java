package org.kakara.client.game.world;

import me.ryandw11.octree.Octree;
import me.ryandw11.octree.PointExistsException;
import org.jetbrains.annotations.NotNull;
import org.kakara.core.Kakara;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkLocation;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ChunkSet implements Set<Chunk> {
    private final Octree<Chunk> chunkOctree;
    private final List<ChunkLocation> locations = new CopyOnWriteArrayList<>();

    public ChunkSet(int x1, int y1, int z1, int x2, int y2, int z2) {
        chunkOctree = new Octree<>(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public int size() {
        return locations.size();
    }

    @Override
    public boolean isEmpty() {
        return locations.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return locations.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Chunk> iterator() {
        return getChunks().iterator();
    }

    public Set<Chunk> getChunks() {
        Set<Chunk> chunks = new HashSet<>();
        for (ChunkLocation location : locations) {
            try {
                Chunk chunk = chunkOctree.get(location.getX(), location.getY(), location.getZ());
                if (chunk == null) continue;
                chunks.add(chunk);
            } catch (PointExistsException | NullPointerException ignored) {
                ignored.printStackTrace();
            }
        }

        return chunks;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return getChunks().toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return getChunks().toArray(a);
    }

    @Override
    public boolean add(Chunk chunk) {
        try {
            chunkOctree.insert(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), chunk);
            locations.add(chunk.getLocation());
        } catch (StackOverflowError e) {
            System.out.println("Here " + chunk.getLocation().toString());
        }
        return true;
    }


    @Override
    public boolean remove(Object o) {
        chunkRemove(((ChunkLocation) o));
        return true;
    }

    public void chunkRemove(ChunkLocation o) {
        locations.remove(o);
        chunkOctree.remove(o.getX(), o.getY(), o.getZ());
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return locations.containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends Chunk> c) {
        c.forEach(chunk -> {

            chunkOctree.insert(chunk.getLocation().getX(), chunk.getLocation().getY(), chunk.getLocation().getZ(), chunk);
            locations.add(chunk.getLocation());
        });
        return true;
    }


    @Override
    public boolean removeAll(@NotNull Collection<?> c) {

        return locations.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return locations.retainAll(c);
    }


    @Override
    public void clear() {
        locations.forEach(chunkLocation -> {
            chunkOctree.remove(chunkLocation.getX(), chunkLocation.getY(), chunkLocation.getZ());
        });
        locations.clear();
    }


    @Override
    public Spliterator<Chunk> spliterator() {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        throw new UnsupportedOperationException();
    }

    public boolean removeIf(Predicate<? super Chunk> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<Chunk> stream() {
        return getChunks().stream();
    }

    @Override
    public Stream<Chunk> parallelStream() {
        return getChunks().parallelStream();
    }

    public Chunk get(int x, int y, int z) {
        return chunkOctree.get(x, y, z);
    }

    public boolean containsChunk(ChunkLocation location) {
        return containsChunk(location.getX(), location.getY(), location.getZ());
    }

    public boolean containsChunk(int x, int y, int z) {
        try {
            return chunkOctree.find(x, y, z);
        } catch (Exception e) {
            return false;
        }
    }

    public Chunk get(ChunkLocation location) {
        return get(location.getX(), location.getY(), location.getZ());
    }
}
