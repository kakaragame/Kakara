package org.kakara.client;

import org.kakara.core.server.ServerGameInstance;
import org.kakara.game.Server;
import org.kakara.game.ServerLoadException;

public abstract class Client implements ServerGameInstance {
    protected Server server;

    public Server getServer() {
        return server;
    }

    public abstract void setup() throws ServerLoadException;
}
