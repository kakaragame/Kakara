package org.kakara.game.item;

import org.kakara.core.GameInstance;
import org.kakara.core.Utils;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemManager;
import org.kakara.core.mod.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO Validate Mods provided
public class GameItemManager implements ItemManager {
    private GameInstance kakaraCore;
    private Map<Item, Mod> items = new HashMap<>();

    @Override
    public void registerItem(Item item, Mod mod) {
        items.putIfAbsent(item, mod);
        //TODO validate Item Resource
    }

    @Override
    public void deregisterItem(Item item, Mod mod) {
        if (items.get(item) == mod) return;
        items.remove(item);
    }

    @Override
    public void deregisterItems(Mod mod) {
        getItemsByMod(mod).forEach(item -> deregisterItem(item, mod));
    }

    public List<Item> getItemsByMod(Mod mod) {
        List<Item> modItems = new ArrayList<>();
        items.entrySet().stream().filter(itemModEntry -> itemModEntry.getValue() == mod).forEach(itemModEntry -> {
            modItems.add(itemModEntry.getKey());
        });
        return modItems;
    }

    @Override
    public Map<Item, Mod> getItems() {
        return new HashMap<>(items);
    }

    @Override
    public Item getItem(String item) {
        if (!Utils.isValidItemPattern(item)) return null;
        String[] itemSplit = item.split(":");
        for (Map.Entry<Item, Mod> o : items.entrySet()) {
            if ((o.getValue().getName().equalsIgnoreCase(itemSplit[0])) && o.getKey().getId().equalsIgnoreCase(itemSplit[1]))
                return o.getKey();
        }
        return null;
    }

    @Override
    public void load(GameInstance kakaraCore) {
        this.kakaraCore = kakaraCore;
    }
}
