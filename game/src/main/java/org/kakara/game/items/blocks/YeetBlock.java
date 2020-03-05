package org.kakara.game.items.blocks;

import org.kakara.core.annotations.Hardness;
import org.kakara.core.annotations.Id;
import org.kakara.core.annotations.Name;
import org.kakara.core.annotations.Texture;
import org.kakara.core.events.entity.StepOnEvent;
import org.kakara.core.events.player.ClickEvent;
import org.kakara.core.events.player.PlaceEvent;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModBlock;

@Texture("blocks/yeet.png")
@Name("Yeet!")
@Id("yeet")
@Hardness(0f)
public class YeetBlock extends ModBlock {
    public YeetBlock(Mod mod) {
        super(mod);
    }

    @Override
    public void onStep(StepOnEvent event) {

    }

    @Override
    public void onPlace(PlaceEvent event) {

    }


    @Override
    public void onClick(ClickEvent clickEvent) {

    }
}
