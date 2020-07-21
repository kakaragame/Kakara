package org.kakara.game.world;

import org.kakara.core.world.ChunkLocation;
import org.kakara.core.world.World;

import java.io.File;
import java.util.Set;

public abstract class GameWorld implements World {
    public abstract boolean isLoaded();

    public abstract File getWorldFolder();

    public abstract void errorClose();

}
