package org.kakara.client.game.commands;

import org.kakara.client.game.world.ClientWorld;
import org.kakara.core.command.CommandSender;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModCommand;
import org.kakara.core.player.Player;
import org.kakara.game.Server;

import java.util.Collections;

public class SaveChunk extends ModCommand {
    private Server server;

    public SaveChunk(Mod mod, Server server) {
        super(Collections.singleton("save-chunks"), "", mod, "save");
        this.server = server;
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        if (executor instanceof Player) {
            executor.sendMessage("Saving all chunks");
            ((ClientWorld) ((Player) executor).getLocation().getWorld().get()).saveChunks();
        } else {
            executor.sendMessage("Must be a player");
        }
    }
}
