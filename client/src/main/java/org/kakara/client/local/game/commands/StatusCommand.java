package org.kakara.client.local.game.commands;


import org.kakara.core.common.command.CommandSender;
import org.kakara.core.common.mod.Mod;
import org.kakara.game.Server;
import org.kakara.game.commands.BuiltinCommand;

import java.util.Collections;

public class StatusCommand extends BuiltinCommand {
    private final Server server;

    public StatusCommand(Mod mod, Server server) {
        super(Collections.singleton("stats"), "", "status");
        this.server = server;
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        executor.sendMessage("Status");
        executor.sendMessage("Number of Chunks Loaded: " + server.getPlayerEntity().getLocation().getWorld().get().getChunks().size());
        executor.sendMessage("Loc: " + server.getPlayerEntity().getLocation().toString());
    }
}
