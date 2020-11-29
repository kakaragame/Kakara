package org.kakara.game.items;


import org.kakara.core.common.annotations.Key;
import org.kakara.core.common.annotations.Name;
import org.kakara.core.common.annotations.Texture;
import org.kakara.core.common.mod.Mod;
import org.kakara.core.common.mod.game.ModItem;

@Key("golden_stick")
@Name("Golden Stick")
@Texture("items/golden-stick.png")
public class GoldenStick extends ModItem {
    public GoldenStick(Mod mod) {
        super(mod);
    }


}
