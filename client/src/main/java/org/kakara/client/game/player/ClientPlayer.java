package org.kakara.client.game.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.core.Kakara;
import org.kakara.core.events.entity.EntityTeleportEvent;
import org.kakara.core.game.Entity;
import org.kakara.core.player.PermissionSet;
import org.kakara.core.player.Player;
import org.kakara.core.player.PlayerEntity;
import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector3;

import java.util.Set;

public class ClientPlayer extends ClientOfflinePlayer implements Player {
    @NotNull
    private final Set<String> permissions = new PermissionSet();
    @NotNull
    private Location location;
    @NotNull
    private final Entity entity;


    public ClientPlayer(JsonObject jsonObject, Location location, IntegratedServer integratedServer) {
        super(jsonObject, integratedServer);
        entity = new PlayerEntity(getName());
        this.location = location;
    }

    @Override
    public void sendMessage(String message) {
        //TODO send message to player console
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    @Override
    public void addPermission(String permission) {
        permissions.add(permission);
    }

    @Override
    public void removePermission(String permission) {
        permissions.add(permission);
    }


    @Override
    @NotNull
    public Location getLocation() {
        return location;
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
