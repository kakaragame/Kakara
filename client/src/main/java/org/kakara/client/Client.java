package org.kakara.client;

import org.kakara.core.server.ServerGameInstance;
import org.kakara.game.Server;
import org.kakara.game.ServerLoadException;

/**
 * An abstract implementation of the ServerGameInstance.
 */
public abstract class Client implements ServerGameInstance {
    protected Server server;

    /**
     * Get the server.
     *
     * @return The server.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Setup the Client.
     *
     * @throws ServerLoadException If a load error occurs.
     */
    public abstract void setup() throws ServerLoadException;
}
