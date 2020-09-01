package org.kakara.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kakara.core.Kakara;
import org.kakara.core.Utils;
import org.kakara.core.gui.loaders.InventoryLoader;
import org.kakara.core.gui.menu.items.ItemStackElement;
import org.kakara.core.gui.menu.items.MenuElement;
import org.kakara.core.resources.Resource;
import org.kakara.core.resources.ResourceType;
import org.kakara.game.mod.KakaraMod;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameInventoryUtils {
    public static Set<MenuElement> getItemPositions(int size) {
        String name = "/inventories/bnbi_" + size + ".json";
        //Kakara.getResourceManager().registerResource(name, ResourceType.OTHER, KakaraMod.getInstance());
        Resource resource = Kakara.getResourceManager().getResource(name, ResourceType.OTHER, KakaraMod.getInstance());
        return InventoryLoader.loadElements(resource);
    }

}
