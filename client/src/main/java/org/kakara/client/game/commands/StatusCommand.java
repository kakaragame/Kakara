package org.kakara.client.game.commands;

import org.kakara.core.command.CommandSender;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModCommand;
import org.kakara.game.Server;

import java.util.Collections;

public class StatusCommand extends ModCommand {
    private Server server;
    public StatusCommand(Mod mod, Server server) {
        super(Collections.singleton("stats"), "", mod, "status");
        this.server = server;
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        executor.sendMessage("Status");
        executor.sendMessage("Number of Chunks Loaded: "+ server.getPlayerEntity().getLocation().getWorld().getLoadedChunks().length);
        executor.sendMessage("Loc: "+ server.getPlayerEntity().getLocation().toString());
    }
}
