package org.kakara.game.items.blocks;


import org.kakara.core.common.annotations.Hardness;
import org.kakara.core.common.annotations.Key;
import org.kakara.core.common.annotations.Name;
import org.kakara.core.common.annotations.Texture;
import org.kakara.core.common.mod.Mod;
import org.kakara.core.common.mod.game.ModBlock;

@Texture("blocks/yeet.png")
@Name("Yeet!")
@Key("yeet")
@Hardness(0f)
public class YeetBlock extends ModBlock {
    public YeetBlock(Mod mod) {
        super(mod);
    }

}
