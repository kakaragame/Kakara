package org.kakara.game.server.gui.bnbi;


import org.kakara.core.common.Kakara;
import org.kakara.core.common.gui.annotations.BuilderClass;
import org.kakara.core.common.gui.container.BoxedInventoryContainer;
import org.kakara.game.server.gui.AbstractBoxedInventory;

@BuilderClass(BasicNineBoxedInventoryBuilder.class)
public abstract class BasicNineBoxedInventory extends AbstractBoxedInventory {

    public BasicNineBoxedInventory(int capacity) {
        super(capacity);
    }


    @Override
    public int rowSize() {
        return 9;
    }

}
