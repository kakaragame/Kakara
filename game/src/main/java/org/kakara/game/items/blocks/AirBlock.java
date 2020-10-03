package org.kakara.game.items.blocks;

import org.kakara.core.ControllerKey;
import org.kakara.core.events.entity.StepOnEvent;
import org.kakara.core.events.player.PlaceEvent;
import org.kakara.core.events.player.click.ClickEvent;
import org.kakara.core.game.Block;
import org.kakara.core.mod.Mod;
import org.kakara.game.NameKeyUtils;
import org.kakara.game.mod.KakaraMod;

//Not to be rendered. It exists solely. So you can set a block to air.
public class AirBlock implements Block {
    public static final String KEY = "AIR";

    @Override
    public void onStep(StepOnEvent event) {

    }

    @Override
    public void onPlace(PlaceEvent event) {

    }

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
    public void onClick(ClickEvent clickEvent) {

    }
}
