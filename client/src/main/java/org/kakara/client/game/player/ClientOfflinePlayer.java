package org.kakara.client.game.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.game.Server;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClientOfflinePlayer implements OfflinePlayer {
    @NotNull
    private final UUID uuid;
    @Nullable
    private final String name;
    private final long lastJoinTime;
    @NotNull
    private final Server server;


    public ClientOfflinePlayer(JsonObject jsonObject, Server server) {
        uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        name = jsonObject.get("name").getAsString();
        lastJoinTime = jsonObject.get("lastjointime").getAsLong();
        this.server = server;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public long getLastJoinTime() {
        return lastJoinTime;
    }

    @Override
    public boolean isOnline() {
        return server.getOnlinePlayers().stream().anyMatch(player -> player.getUUID().equals(uuid));
    }

    @Override
    public Player toOnlinePlayer() {
        Optional<Player> playerOptional = server.getOnlinePlayers().stream().filter(player -> player.getUUID().equals(uuid)).findFirst();
        if (playerOptional.isEmpty()) {
            return null;
        }
        return playerOptional.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof UUID) {
            return o.equals(uuid);
        }
        if (!(o instanceof ClientOfflinePlayer)) return false;
        ClientOfflinePlayer that = (ClientOfflinePlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    //TODO methods below
    @Override
    public void ban(@Nullable String reason) {

    }

    @Override
    public void ban(long duration, @NotNull TimeUnit timeUnit, @Nullable String reason) {
    }


}
