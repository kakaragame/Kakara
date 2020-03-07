package org.kakara.game.items;

import org.kakara.core.charm.Charm;
import org.kakara.core.game.Item;
import org.kakara.core.game.ItemStack;
import org.kakara.core.game.MetaData;

import java.util.Map;

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
}