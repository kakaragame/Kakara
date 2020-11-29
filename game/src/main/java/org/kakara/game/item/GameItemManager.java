package org.kakara.game.item;


import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.GameInstance;
import org.kakara.core.common.game.Item;
import org.kakara.core.common.game.ItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO Validate Mods provided
public class GameItemManager implements ItemManager {
    private final Map<Integer, Item> items = new ConcurrentHashMap<>();
    private GameInstance kakaraCore;

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
        return items.values().stream().filter(item -> item.getControllerKey().getKey().equals(key)).collect(Collectors.toList());
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
    public Item getItem(ControllerKey item) {
        for (Item item1 : items.values()) {
            if (item1.getControllerKey().equals(item)) {
                return item1;
            }
        }
        return null;
    }

    @Override
    public Item getItem(int id) {
        return (items.get(id));
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
