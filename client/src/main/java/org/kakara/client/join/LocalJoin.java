package org.kakara.client.join;


import org.kakara.core.client.client.Save;

import java.io.File;
import java.util.UUID;

public class LocalJoin extends JoinDetails {
    private final Save save;
    private final UUID self;

    public LocalJoin(Save save, UUID self) {
        super(JoinType.LOCAL, new File(save.getSaveFolder(), "working"));
        this.save = save;
        this.self = self;
    }

    public Save getSave() {
        return save;
    }

    public UUID getSelf() {
        return self;
    }
}
