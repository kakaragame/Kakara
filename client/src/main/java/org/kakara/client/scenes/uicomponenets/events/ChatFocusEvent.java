package org.kakara.client.scenes.uicomponenets.events;

import org.kakara.engine.ui.events.UActionEvent;

/**
 * An event that is triggered when the chat is made in focus.
 * <p>
 * Use by {@link org.kakara.client.scenes.uicomponenets.ChatComponent#addUActionEvent(Class, UActionEvent)}.
 */
public interface ChatFocusEvent extends UActionEvent {
    void onChatFocus();
}
