package org.kakara.client.game.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.core.Kakara;
import org.kakara.core.events.entity.EntityTeleportEvent;
import org.kakara.core.game.Entity;
import org.kakara.core.gui.Inventory;
import org.kakara.core.gui.Menu;
import org.kakara.core.player.PermissionSet;
import org.kakara.core.player.Player;
import org.kakara.core.player.PlayerEntity;
import org.kakara.core.player.Toast;
import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector3;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ClientPlayer extends ClientOfflinePlayer implements Player {
    @NotNull
    private final PermissionSet permissions = new PermissionSet();
    @NotNull
    private Location location;
    @NotNull
    private final Entity entity;
    private String displayName = getName();
    private short health = 20;
    private short hunger = 20;

    public ClientPlayer(JsonObject jsonObject, @NotNull Location location, IntegratedServer integratedServer) {
        super(jsonObject, integratedServer);
        entity = new PlayerEntity(getName());
        this.location = location;
    }

    @Override
    @NotNull
    public PermissionSet getPermissions() {
        return permissions;
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


    @Override
    public void setHealth(short health) {
        this.health = health;
    }

    @Override
    public short getHealth() {
        return health;
    }

    @Override
    public void setHunger(short hunger) {
        this.hunger = hunger;
    }

    @Override
    public short getHunger() {
        return hunger;
    }

    @Override
    public @NotNull String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    //TODO the methods below
    @Override
    public void kick(@Nullable String reason) {

    }

    @Override
    public void sendToast(@NotNull Toast toast) {

    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void sendMessage(String message) {
        //TODO send message to player console
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    @Override
    public void openMenu(@NotNull Menu menu) {

    }

}
