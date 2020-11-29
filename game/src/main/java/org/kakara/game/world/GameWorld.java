package org.kakara.game.world;


import org.kakara.core.server.world.ServerWorld;

import java.io.File;

public abstract class GameWorld implements ServerWorld {
    public abstract boolean isLoaded();

    public abstract File getWorldFolder();

    public abstract void errorClose();

}
