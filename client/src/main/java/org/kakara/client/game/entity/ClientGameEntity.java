package org.kakara.client.game.entity;

import org.jetbrains.annotations.NotNull;
import org.kakara.client.MoreUtils;
import org.kakara.core.Kakara;
import org.kakara.core.events.entity.EntityTeleportEvent;
import org.kakara.core.game.Entity;
import org.kakara.core.world.GameEntity;
import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector3;

public class ClientGameEntity implements GameEntity {
    @NotNull
    private Location location;
    @NotNull
    private final Entity entity;

    public ClientGameEntity(@NotNull Location location, @NotNull Entity entity) {
        this.location = location;
        this.entity = entity;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isLiving() {
        return true;
    }

    @Override
    public boolean kill() {
        return false;
    }

    @NotNull
    public Vector3 locationAsVector3() {
        return MoreUtils.locationToVector3(location);
    }

    @Override
    public void teleport(Location location) {
        EntityTeleportEvent entityTeleportEvent = new EntityTeleportEvent(this, this.location, location);
        Kakara.getEventManager().callEvent(entityTeleportEvent);
        if (!entityTeleportEvent.isCancelled())
            this.location = entityTeleportEvent.getNewLocation();
    }

    @Override
    public Entity getEntityType() {
        return entity;
    }
}
