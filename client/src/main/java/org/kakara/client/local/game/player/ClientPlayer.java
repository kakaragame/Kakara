package org.kakara.client.local.game.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.kakara.client.MoreUtils;
import org.kakara.client.local.game.IntegratedServer;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.Serverable;
import org.kakara.core.common.exceptions.NoServerVersionAvailableException;
import org.kakara.core.common.game.DefaultGameMode;
import org.kakara.core.common.game.Entity;
import org.kakara.core.common.game.GameMode;
import org.kakara.core.common.gui.Inventory;
import org.kakara.core.common.gui.Menu;
import org.kakara.core.common.permission.PermissionSet;
import org.kakara.core.common.player.PlayerEntity;
import org.kakara.core.common.player.meter.PlayerMeterController;
import org.kakara.core.common.world.Location;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.gui.ServerBoxedInventoryContainer;
import org.kakara.core.server.player.ServerPlayer;
import org.kakara.engine.math.Vector3;
import org.kakara.game.GameUtils;

import java.util.Optional;
import java.util.UUID;

public class ClientPlayer extends ClientOfflinePlayer implements ServerPlayer {
    @NotNull
    private final PermissionSet permissions = new PermissionSet();
    @NotNull
    private final Entity entity;
    @NotNull
    private Location location;
    private final String displayName = getName();
    private final short health = 20;
    private final short hunger = 20;
    private UUID gameItemID;
    private final PlayerContentInventory contentInventory;
    private final GameMode gameMode;

    public ClientPlayer(JsonObject jsonObject, @NotNull Location location, IntegratedServer integratedServer) {
        super(jsonObject, integratedServer);
        entity = new PlayerEntity(getName());
        this.location = location;

        if (!jsonObject.has("gamemode")) {
            gameMode = DefaultGameMode.CREATIVE;
        } else
            gameMode = GameUtils.getGameMode(jsonObject.get("gamemode").getAsString());
        contentInventory = new PlayerContentInventory();
        ((ServerBoxedInventoryContainer) contentInventory.getContainer()).addItemStack(((ServerGameInstance) Kakara.getGameInstance()).createItemStack(Kakara.getGameInstance().getItemRegistry().getItem(new ControllerKey("KVANILLA", "DIRT"))));
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


    @NotNull
    public Vector3 locationAsVector3() {
        return MoreUtils.locationToVector3(location);
    }


    @Override
    public Entity getEntityType() {
        return entity;
    }



    @Override
    public @NotNull String getDisplayName() {
        return displayName;
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

        getServer().getServerController().sendMessage(message.getBytes());
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public PlayerMeterController getMeterController() {
        return null;
    }


    @Override
    public @NotNull Inventory getInventory() {
        return contentInventory;
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

    @Override
    public boolean isServerVersionAvailable() {
        return false;
    }

    @Override
    public <T extends Serverable> T getServerVersion() {
        return null;
    }

    @Override
    public void requiresServerVersion() throws NoServerVersionAvailableException {

    }

    @Override
    public void openMenu(Menu menu) {
        if (menu instanceof Inventory) {
            Inventory inventory = (Inventory) menu;
            inventory.getRenderer().render(inventory);
        }
    }
}
