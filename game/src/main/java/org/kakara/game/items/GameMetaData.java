package org.kakara.game.items;

import org.kakara.core.game.MetaData;

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
}
