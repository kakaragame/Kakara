package org.kakara.client.join;

import java.io.File;

public abstract class JoinDetails {
    private final JoinType envType;
    private final File workingDirectory;

    public JoinDetails(JoinType envType, File workingDirectory) {
        this.envType = envType;
        if (!workingDirectory.exists()) workingDirectory.mkdirs();
        this.workingDirectory = workingDirectory;
    }

    public JoinType getEnvType() {
        return envType;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }


    public enum JoinType {
        SERVER,
        LOCAL
    }
}
