package org.kakara.game;

import org.kakara.core.exceptions.WorldLoadException;

public class ServerLoadException extends Exception {
    public ServerLoadException(Exception e) {
        super(e);
    }
}
