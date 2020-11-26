package org.kakara.client.join;

public abstract class JoinDetails {
    private JoinType envType;

    public JoinDetails(JoinType envType) {
        this.envType = envType;
    }

    public JoinType getEnvType() {
        return envType;
    }

    public static enum JoinType {
        SERVER,
        LOCAL
    }
}
