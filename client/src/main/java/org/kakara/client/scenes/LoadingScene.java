package org.kakara.client.scenes;


import org.kakara.core.common.ManagedObject;
import org.kakara.core.common.Status;
import org.kakara.engine.GameHandler;
import org.kakara.engine.scene.AbstractMenuScene;

public class LoadingScene extends AbstractMenuScene {
    private final ManagedObject statusable;
    private final Status targetStatus;
    private final Runnable onCompletion;

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
        System.out.println("getStatus() = " + statusable.getStatus());
        if (statusable.getStatus() == targetStatus) {
            onCompletion.run();
        }
    }
}
