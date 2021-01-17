package org.kakara.client;


import org.kakara.core.common.Kakara;
import org.kakara.core.common.gui.loaders.InventoryLoader;
import org.kakara.core.common.gui.menu.items.MenuElement;
import org.kakara.core.common.resources.Resource;
import org.kakara.core.common.resources.ResourceType;
import org.kakara.game.mod.KakaraMod;

import java.util.Set;

public class GameInventoryUtils {
    public static Set<MenuElement> getItemPositions(int size) {
        String name = "/inventories/bnbi_" + size + ".json";
        //Kakara.getResourceManager().registerResource(name, ResourceType.OTHER, KakaraMod.getInstance());
        Resource resource = Kakara.getGameInstance().getResourceManager().getResource(name, ResourceType.OTHER, KakaraMod.getInstance());
        return InventoryLoader.loadElements(resource);
    }

}
