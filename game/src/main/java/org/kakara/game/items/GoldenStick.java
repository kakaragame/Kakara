package org.kakara.game.items;

import org.kakara.core.annotations.Id;
import org.kakara.core.annotations.Name;
import org.kakara.core.annotations.Texture;
import org.kakara.core.events.player.ClickEvent;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModItem;

@Id("golden_stick")
@Name("Golden Stick")
@Texture("items/golden-stick.png")
public class GoldenStick extends ModItem {
    public GoldenStick(Mod mod) {
        super(mod);
    }

    @Override
    public void onClick(ClickEvent clickEvent) {

    }
}