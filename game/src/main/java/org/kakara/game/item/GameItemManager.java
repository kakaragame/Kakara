package org.kakara.game.item;

import org.kakara.core.GameInstance;
import org.kakara.core.NameKey;
import org.kakara.core.Utils;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemManager;
import org.kakara.core.mod.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

//TODO Validate Mods provided
public class GameItemManager implements ItemManager {
    private GameInstance kakaraCore;
    private List<Item> items = new CopyOnWriteArrayList<>();

    @Override
    public void registerItem(Item item) {
        items.add(item);
    }

    @Override
    public void deregisterItem(Item item) {
        items.remove(item);
    }

    @Override
    public List<Item> getItemsByKey(String key) {
        return items.stream().filter(item -> item.getNameKey().getName().equals(key)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public void deregisterItems(String mod) {
        for (Item item : getItemsByKey(mod)) {
            deregisterItem(item);
        }
    }

    @Override
    public Item getItem(NameKey item) {
        for (Item item1 : items) {
            if (item1.getNameKey().equals(item)){
                return item1;
            }
        }
        return null;
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
