package org.kakara.game.server.gui;

import org.kakara.core.common.gui.container.BoxedInventoryContainer;
import org.kakara.core.common.gui.container.ContainerUtils;
import org.kakara.core.common.gui.container.InventoryContainer;

public class ServerContainerUtils implements ContainerUtils {
    @Override
    public InventoryContainer createInventoryContainer() {
        return null;
    }

    @Override
    public BoxedInventoryContainer createBoxInventoryContainer() {
        return new ServerBoxInventoryContainer();
    }
}
