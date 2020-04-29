package org.kakara.client.tests.commands;

import org.kakara.core.NameKey;
import org.kakara.core.command.Command;
import org.kakara.core.command.CommandSender;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCommand implements Command {
    @Override
    public NameKey command() {
        return new NameKey("test", "test");
    }

    @Override
    public Set<String> getAliases() {
        return Collections.singleton("tester");
    }

    @Override
    public String getDescription() {
        return "Tests Stuff";
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        assertTrue(command.equals("test") || command.equals("tester"));
        executor.sendMessage(command);
    }
}