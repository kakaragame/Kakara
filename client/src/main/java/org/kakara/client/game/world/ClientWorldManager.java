package org.kakara.client.game.world;

import org.jetbrains.annotations.Nullable;
import org.kakara.client.game.IntegratedServer;
import org.kakara.core.world.World;
import org.kakara.core.world.WorldManager;

import java.util.UUID;

public class ClientWorldManager implements WorldManager {
    private IntegratedServer integratedServer;

    public ClientWorldManager(IntegratedServer integratedServer) {
        this.integratedServer = integratedServer;
    }

    @Override
    public @Nullable World getWorldByUUID(UUID uuid) {
        return integratedServer.getSave().getWorlds().stream().filter(world -> world.getUUID().equals(uuid)).findFirst().orElse(null);
    }
}
