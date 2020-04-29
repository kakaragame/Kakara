package org.kakara.client.scenes.uicomponenets.events;

import org.kakara.engine.ui.events.UActionEvent;

public interface ChatSendEvent extends UActionEvent {
    void onChatSend(String message);
}
