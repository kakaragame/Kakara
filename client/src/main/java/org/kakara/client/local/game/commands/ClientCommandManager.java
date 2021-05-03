package org.kakara.client.local.game.commands;

import org.jetbrains.annotations.NotNull;
import org.kakara.client.MoreUtils;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.command.Command;
import org.kakara.core.common.command.CommandManager;
import org.kakara.core.common.command.CommandSender;
import org.kakara.core.common.command.TabCompleter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ClientCommandManager implements CommandManager {
    private final Set<TabCompleter> tabCompleters = new HashSet<>();
    private final Set<Command> registeredCommands = new HashSet<>();

    @Override
    public void executeCommand(@NotNull String command, @NotNull CommandSender sender) {
        String[] split = command.split(" ");
        String commandValue = split[0];

        Optional<Command> command1;
        if (commandValue.contains(":")) {
            command1 = registeredCommands.stream().filter(command2 -> command2.command().equals(new ControllerKey(commandValue))).findFirst();
        } else {
            command1 = registeredCommands.stream().filter(command2 -> command2.getAliases().contains(commandValue) || command2.command().getKey().equalsIgnoreCase(commandValue)).findFirst();
        }

        String commandValueFinal = commandValue;
        if (commandValueFinal.contains(":")) commandValueFinal = commandValueFinal.split(":")[1];
        if (command1.isPresent()) {
            command1.get().execute(commandValueFinal, MoreUtils.removeFirst(split), command, sender);
        } else {
            //TODO pull from config
            sender.sendMessage("{#a1120a}Command Not Found");
        }

    }

    @Override
    public void registerCommand(@NotNull Command command) {
        if (registeredCommands.stream().anyMatch(command1 -> command1.getClass().equals(command.getClass()))) {
            //TODO throw already registered exception
            return;
        }
        registeredCommands.add(command);
    }

    @Override
    public void registerAutoCompleter(@NotNull TabCompleter completer) {
        if (tabCompleters.stream().anyMatch(completer1 -> completer1.getClass().equals(completer.getClass()))) {
            //TODO throw already registered exception
            return;
        }
        tabCompleters.add(completer);
    }

    @Override
    public Set<TabCompleter> getRegisterTabCompleters() {
        return new HashSet<>(tabCompleters);
    }

    @Override
    public Set<Command> getRegisteredCommands() {
        return new HashSet<>(registeredCommands);
    }

    @Override
    public Class<?> getStageClass() {
        return CommandManager.class;
    }
}
