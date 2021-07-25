package org.kakara.client.scenes.uicomponenets.events;

import org.kakara.engine.ui.events.UActionEvent;

/**
 * An event that is triggered when the chat is made out of focus.
 * <p>
 * Use by {@link org.kakara.client.scenes.uicomponenets.ChatComponent#addUActionEvent(Class, UActionEvent)}.
 */
public interface ChatBlurEvent extends UActionEvent {
    void onChatBlur();
}
