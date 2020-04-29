package org.kakara.client.tests;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.command.CommandSender;

public class TestCommandSender implements CommandSender {
    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(message);
    }
}
