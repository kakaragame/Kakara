package org.kakara.game.world;


import org.kakara.core.common.world.World;

import java.io.File;

public abstract class GameWorld implements World {
    public abstract boolean isLoaded();

    public abstract File getWorldFolder();

    public abstract void errorClose();

}
