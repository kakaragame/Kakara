package org.kakara.client.game.player;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.game.Server;

import java.util.UUID;

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
        return false;
    }

    @Override
    public Player toOnlinePlayer() {
        return null;
    }
}
