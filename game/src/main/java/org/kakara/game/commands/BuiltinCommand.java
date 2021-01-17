package org.kakara.game.commands;

import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.command.Command;
import org.kakara.core.common.command.CommandSender;
import org.kakara.game.mod.KakaraMod;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class BuiltinCommand implements Command {
    private final Set<String> aliases;
    private final String description;
    private final KakaraMod mod;
    private final ControllerKey nameKey;

    public BuiltinCommand(Set<String> aliases, String description, String command) {
        this.aliases = aliases;
        this.description = description;
        mod = KakaraMod.getInstance();
        nameKey = new ControllerKey(mod, command);
    }

    public List<String> getAutoCompletionSuggestions(String command, String[] arguments, String fullCommand, CommandSender executor) {
        //TODO auto send list of online players
        return Collections.emptyList();
    }

    @Override
    public Set<String> getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ControllerKey command() {
        return nameKey;
    }
}
