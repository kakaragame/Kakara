package org.kakara.client.game.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.client.MoreUtils;
import org.kakara.client.game.IntegratedServer;
import org.kakara.core.ControllerKey;
import org.kakara.core.Kakara;
import org.kakara.core.events.entity.EntityTeleportEvent;
import org.kakara.core.game.DefaultGameMode;
import org.kakara.core.game.Entity;
import org.kakara.core.game.GameMode;
import org.kakara.core.gui.Inventory;
import org.kakara.core.gui.Menu;
import org.kakara.core.player.PermissionSet;
import org.kakara.core.player.Player;
import org.kakara.core.player.PlayerEntity;
import org.kakara.core.player.Toast;
import org.kakara.core.world.Location;
import org.kakara.engine.math.Vector3;
import org.kakara.game.GameUtils;

import java.util.Optional;
import java.util.UUID;

public class ClientPlayer extends ClientOfflinePlayer implements Player {
    @NotNull
    private final PermissionSet permissions = new PermissionSet();
    @NotNull
    private final Entity entity;
    @NotNull
    private Location location;
    private String displayName = getName();
    private short health = 20;
    private short hunger = 20;
    private UUID gameItemID;
    private PlayerContentInventory contentInventory;
    private GameMode gameMode;

    public ClientPlayer(JsonObject jsonObject, @NotNull Location location, IntegratedServer integratedServer) {
        super(jsonObject, integratedServer);
        entity = new PlayerEntity(getName());
        this.location = location;
        if (jsonObject.get("gamemode") == null) {
            gameMode = DefaultGameMode.CREATIVE;
        } else
            gameMode = GameUtils.getGameMode(jsonObject.get("gamemode").getAsString());
        contentInventory = new PlayerContentInventory();
        contentInventory.setItemStack(Kakara.createItemStack(Kakara.getItemManager().getItem(new ControllerKey("KVANILLA", "DIRT"))), 1);
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

    public void setLocation(Location location) {
        this.location = location;
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

    @Override
    public short getHealth() {
        return health;
    }

    @Override
    public void setHealth(short health) {
        this.health = health;
    }

    @Override
    public short getHunger() {
        return hunger;
    }

    @Override
    public void setHunger(short hunger) {
        this.hunger = hunger;
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
    public void sendNotification(@NotNull String message) {

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
    public void sendMessage(@NotNull String message) {
        getServer().renderMessageToConsole(message);
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return contentInventory;
    }

    @Override
    public void openMenu(@NotNull Menu menu) {
        if (menu instanceof Inventory) {
            Inventory inventory = (Inventory) menu;
            inventory.getRenderer().render(inventory);
        }
    }

    public void moveLocation(double i, double i1, double i2) {
        location = location.add(i, i1, i2);
    }

    public void moveLocation(float x, float y) {
        location = location.add(new Location(0, 0, 0, x, y));
    }

    public Optional<UUID> getGameItemID() {
        return Optional.ofNullable(gameItemID);
    }

    public void setGameItemID(UUID gameItemID) {
        this.gameItemID = gameItemID;
    }
}
