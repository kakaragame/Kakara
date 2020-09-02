package org.kakara.client.tests.commands;

import org.junit.jupiter.api.Test;
import org.kakara.client.game.commands.ClientCommandManager;
import org.kakara.client.tests.TestCommandSender;
import org.kakara.core.command.CommandManager;
import org.kakara.core.command.CommandSender;

public class TestClientCommandManager {
    private CommandManager commandManager = new ClientCommandManager();
    private CommandSender commandSender = new TestCommandSender();

    @Test
    public void generalTest() {

        commandManager.registerCommand(new TestCommand());
        commandManager.executeCommand("test", commandSender);
        commandManager.executeCommand("test:test", commandSender);
        commandManager.executeCommand("tester", commandSender);
    }

}