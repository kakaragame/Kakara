package org.kakara.client.scenes.uicomponenets.events;

import org.kakara.engine.ui.events.UActionEvent;

/**
 * An event that is triggered when a message is sent to the chat.
 * <p>
 * Use by {@link org.kakara.client.scenes.uicomponenets.ChatComponent#addUActionEvent(Class, UActionEvent)}.
 */
public interface ChatSendEvent extends UActionEvent {
    void onChatSend(String message);
}
