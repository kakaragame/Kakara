package org.kakara.client.scenes.uicomponenets.loadingbar;

import org.kakara.engine.ui.events.UActionEvent;

/**
 * An event that is called when a {@link LoadingBar} hits 100%.
 */
public interface LoadingBarCompleteEvent extends UActionEvent {
    void loadingBarCompleteEvent(float percent);
}
