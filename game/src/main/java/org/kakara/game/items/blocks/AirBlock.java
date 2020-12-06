package org.kakara.game.items.blocks;


import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.events.Event;
import org.kakara.core.common.events.RegisteredListener;
import org.kakara.core.common.game.Block;
import org.kakara.core.common.mod.Mod;
import org.kakara.game.NameKeyUtils;
import org.kakara.game.mod.KakaraMod;

import java.util.Collections;
import java.util.Map;

//Not to be rendered. It exists solely. So you can set a block to air.
public class AirBlock implements Block {
    public static final String KEY = "AIR";

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public String getName() {
        return "air";
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public String getModel() {
        return null;
    }

    @Override
    public ControllerKey getControllerKey() {
        return NameKeyUtils.newKakaraNameKey(KEY);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Mod getMod() {
        return KakaraMod.getInstance();
    }


    @Override
    public Map<Class<? extends Event>, RegisteredListener> getRegisteredListeners() {
        return Collections.emptyMap();
    }
}
