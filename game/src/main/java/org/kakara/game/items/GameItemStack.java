package org.kakara.game.items;

import org.kakara.core.charm.Charm;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;
import org.kakara.core.game.MetaData;

import java.util.Map;
import java.util.Objects;

public class GameItemStack implements ItemStack {
    private int count;
    private Item item;
    private MetaData metaData;

    public GameItemStack(int count, Item item, MetaData metaData) {
        this.count = count;
        this.item = item;
        this.metaData = metaData;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
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
