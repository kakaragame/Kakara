package org.kakara.game.item;

import org.kakara.core.GameInstance;
import org.kakara.core.NameKey;
import org.kakara.core.Utils;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemManager;
import org.kakara.core.mod.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

//TODO Validate Mods provided
public class GameItemManager implements ItemManager {
    private GameInstance kakaraCore;
    private final Map<Integer, Item> items = new ConcurrentHashMap<>();

    @Override
    public void registerItem(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public void deregisterItem(Item item) {
        items.remove(item.getId());
    }


    @Override
    public List<Item> getItemsByName(String key) {
        return items.values().stream().filter(item -> item.getNameKey().getName().equals(key)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void deregisterItems(String mod) {
        for (Item item : getItemsByName(mod)) {
            deregisterItem(item);
        }
    }

    @Override
    public Optional<Item> getItem(NameKey item) {
        for (Item item1 : items.values()) {
            if (item1.getNameKey().equals(item)) {
                return Optional.of(item1);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> getItem(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void load(GameInstance kakaraCore) {
        this.kakaraCore = kakaraCore;
    }

    @Override
    public Class<?> getStageClass() {
        return ItemManager.class;
    }
}
