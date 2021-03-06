package org.kakara.game.items;


import org.kakara.core.common.game.Item;
import org.kakara.core.common.game.ItemStack;
import org.kakara.core.server.game.ServerItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameItemStack implements ServerItemStack {
    private int count;
    private final Item item;
    private String name;
    private List<String> lore;

    public GameItemStack(int count, Item item) {
        this.count = count;
        this.item = item;
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
    public String getName() {
        if (name == null) name = item.getName();
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public void setLore(List<String> list) {
        if (list == null) {
            lore = null;
        } else
            this.lore = Collections.unmodifiableList(list);
    }

    @Override
    public ItemStack clone() {
        GameItemStack gameItemStack = new GameItemStack(count, item);
        gameItemStack.lore = new ArrayList<>(lore);
        return gameItemStack;
    }

    @Override
    public boolean equalsIgnoreCount(ItemStack itemStack) {
        return itemStack.getItem().equals(item) && getName().equals(itemStack.getName()) && Objects.equals(getLore(), itemStack.getLore());
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
                ", item=" + item.getControllerKey().toString() +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                '}';
    }
}
