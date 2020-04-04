package org.kakara.game.items;

import org.kakara.core.charm.Charm;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameItemStack implements ItemStack {
    private int count;
    private Item item;
    private String name;
    private List<String> lore;

    public GameItemStack(int count, Item item) {
        this.count = count;
        this.item = item;
        name = item.getName();
        lore = new ArrayList<>();
    }

    @Override
    public Item getItem() {
        return item;
    }


    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public Map<Charm, Byte> getCharms() {
        return null;
    }

    @Override
    public void addCharm(Charm charm, Byte level) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameItemStack that = (GameItemStack) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }
}
