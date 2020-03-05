package org.kakara.game.items;

import org.kakara.core.game.MetaData;

import java.util.List;
import java.util.Map;

public class GameMetaData implements MetaData {
    private String name;

    public GameMetaData(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setLore(List<String> list) {

    }

    @Override
    public List<String> getLore() {
        return null;
    }

    @Override
    public Map<String, String> getItemData() {
        return null;
    }

    @Override
    public void setItemData(String s, String s1) {

    }
}
