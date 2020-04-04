package org.kakara.game.client;

import org.kakara.core.client.SaveSettings;

import java.io.File;

public class ClientSaveSettings extends SaveSettings {
    public ClientSaveSettings(File file) throws SaveLoadException {
        super(null, null);
    }
}
