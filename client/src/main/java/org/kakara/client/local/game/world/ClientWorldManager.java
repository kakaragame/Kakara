package org.kakara.client.local.game.world;

import org.jetbrains.annotations.Nullable;
import org.kakara.client.local.game.IntegratedServer;
import org.kakara.core.common.world.World;
import org.kakara.core.server.world.WorldManager;

import java.util.UUID;

public class ClientWorldManager implements WorldManager {
    private final IntegratedServer integratedServer;

    public ClientWorldManager(IntegratedServer integratedServer) {
        this.integratedServer = integratedServer;
    }

    @Override
    public @Nullable World getWorldByUUID(UUID uuid) {
        return integratedServer.getSave().getWorlds().stream().filter(world -> world.getUUID().equals(uuid)).findFirst().orElse(null);
    }
}
