package org.kakara.game.client.player;

import com.google.gson.JsonObject;
import org.kakara.core.player.OfflinePlayer;
import org.kakara.core.player.Player;
import org.kakara.game.Server;

import java.util.UUID;

public class ClientOfflinePlayer implements OfflinePlayer {
    private UUID uuid;
    private String name;
    private long lastJoinTime;
    private Server server;

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
    public String getName() {
        return name;
    }

    @Override
    public long getLastJoinTime() {
        return lastJoinTime;
    }

    @Override
    public boolean isOnline() {
        for (Player player : server.getOnlinePlayers()) {
            if (player.getUUID().equals(uuid))
                return true;
        }
        return false;
    }

    @Override
    public Player toOnlinePlayer() {
        for (Player player : server.getOnlinePlayers()) {
            if (player.getUUID().equals(uuid))
                return player;
        }
        return null;
    }

}
