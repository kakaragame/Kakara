package org.kakara.client.scenes;

import org.kakara.core.ManagedObject;
import org.kakara.core.Status;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.AbstractMenuScene;

public class LoadingScene extends AbstractMenuScene {
    private final ManagedObject statusable;
    private final Status targetStatus;
    private Runnable onCompletion;

    public LoadingScene(GameHandler handler, ManagedObject statusable, Status targetStatus, Runnable onCompletion) {
        super(handler);
        this.statusable = statusable;
        this.targetStatus = targetStatus;
        this.onCompletion = onCompletion;
    }

    @Override
    public void work() {

    }

    @Override
    public void loadGraphics(GameHandler gameHandler) throws Exception {

    }

    @Override
    public void update(float v) {
        if (statusable.getStatus() == targetStatus) {
            onCompletion.run();
        }
    }
}
