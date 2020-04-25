package org.kakara.game.client.player;

import com.google.gson.JsonObject;
import org.kakara.core.Kakara;
import org.kakara.core.events.entity.EntityTeleportEvent;
import org.kakara.core.game.Entity;
import org.kakara.core.game.entity.PathFinder;
import org.kakara.core.player.Player;
import org.kakara.core.world.Location;
import org.kakara.game.Server;

public class ClientPlayer extends ClientOfflinePlayer implements Player {
    private Location location;

    public ClientPlayer(JsonObject jsonObject, Location location, Server server) {
        super(jsonObject, server);
        this.location = location;
    }

    @Override
    public void sendMessage(String message) {
        //TODO client text box.
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void moveLocation(double x, double y, double z) {
        location = location.add(x, y, z);
    }

    public void moveLocation(float yaw, float pitch) {
        location = location.add(new Location(0, 0, 0, pitch, yaw));
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
        return this;
    }

    @Override
    public PathFinder getPathFinder() {
        //Players Dont Have a Path Finder
        return null;
    }
}
