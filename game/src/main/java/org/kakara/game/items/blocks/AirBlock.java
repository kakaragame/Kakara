package org.kakara.game.items.blocks;

import org.kakara.core.annotations.Hardness;
import org.kakara.core.annotations.Key;
import org.kakara.core.annotations.Name;
import org.kakara.core.annotations.Texture;
import org.kakara.core.events.entity.StepOnEvent;
import org.kakara.core.events.player.PlaceEvent;
import org.kakara.core.events.player.click.ClickEvent;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModBlock;

@Name("Air")
@Key("air")
//Not to be rendered. It exists solely. So you can set a block to air.
public class AirBlock extends ModBlock {
    public AirBlock(Mod mod) {
        super(mod);
    }

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
    public String getTexture() {
        return "";
    }

    @Override
    public void onClick(ClickEvent clickEvent) {

    }
}
