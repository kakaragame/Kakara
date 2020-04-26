package org.kakara.client.game.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;

import java.util.UUID;

public class ClientOfflinePlayer implements OfflinePlayer {
    @NotNull
    private UUID uuid;
    @Nullable
    private String name;
    private final long lastJoinTime;

    public ClientOfflinePlayer(@NotNull UUID uuid, @Nullable String name, long lastJoinTime) {
        this.uuid = uuid;
        this.name = name;
        this.lastJoinTime = lastJoinTime;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
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
