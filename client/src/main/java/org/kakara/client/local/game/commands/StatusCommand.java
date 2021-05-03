package org.kakara.client.local.game.commands;


import org.kakara.core.common.Status;
import org.kakara.core.common.command.CommandSender;
import org.kakara.core.common.mod.Mod;
import org.kakara.game.Server;
import org.kakara.game.commands.BuiltinCommand;

import java.util.Collections;
import java.util.stream.Collectors;

public class StatusCommand extends BuiltinCommand {
    private final Server server;

    public StatusCommand(Mod mod, Server server) {
        super(Collections.singleton("stats"), "", "status");
        this.server = server;
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        executor.sendMessage("Game Status:");
        executor.sendMessage("Number of Chunks Loaded: " + server.getPlayerEntity().getLocation().getNullableWorld().getChunks()
                .stream().filter(chunk -> chunk.getStatus() == Status.LOADED).collect(Collectors.toList()).size());
        executor.sendMessage("Location: " + server.getPlayerEntity().getLocation().toString());
    }
}
