package org.kakara.client.local.game.gui;



import org.kakara.core.common.gui.Inventory;
import org.kakara.core.common.gui.InventoryBuilder;
import org.kakara.core.common.gui.InventoryManager;
import org.kakara.core.common.gui.annotations.BuilderClass;

import java.lang.reflect.InvocationTargetException;

public class ClientInventoryManager implements InventoryManager {
    @Override
    public <T extends InventoryBuilder> T createInventory(Class<?> inventoryClass) {
        Class<? extends InventoryBuilder> clazz;
        if (inventoryClass.isAssignableFrom(Inventory.class)) {
            clazz = inventoryClass.getAnnotation(BuilderClass.class).value();
        } else if (inventoryClass.isAssignableFrom(InventoryBuilder.class)) {
            clazz = (Class<? extends InventoryBuilder>) inventoryClass;
        } else {
            throw new IllegalArgumentException("InventoryClass must be either a Inventory or InventoryBuilder");
        }

        try {
            return (T) clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
