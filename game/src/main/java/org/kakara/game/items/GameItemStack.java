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
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    @Override
    public void setLore(List<String> list) {
        this.lore = list;
    }

    @Override
    public ItemStack clone() {
        GameItemStack gameItemStack = new GameItemStack(count, item);
        gameItemStack.lore = new ArrayList<>(lore);
        return gameItemStack;
    }

    @Override
    public boolean equalsIgnoreCount(ItemStack itemStack) {
        //TODO compare other values
        return itemStack.getItem().equals(item);
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

    @Override
    public String toString() {
        return "GameItemStack{" +
                "count=" + count +
                ", item=" + item.getNameKey().toString() +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                '}';
    }
}
