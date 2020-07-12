package org.kakara.client.scenes;

import com.google.common.cache.CacheLoader;

import java.util.HashMap;

/**
 * This is a Hashmap with a loading feature.
 *
 * @param <K>
 * @param <V>
 */
public class CachingHashMap<K, V> extends HashMap<K, V> {
    private final CacheLoader<K, V> cacheLoader;

    public CachingHashMap(CacheLoader<K, V> cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    @Override
    public V get(Object key) {
        V v = super.get(key);
        if (v == null) {
            try {
                v = cacheLoader.load((K) key);
                put((K) key, v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return v;
    }
}
