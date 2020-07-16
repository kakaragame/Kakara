package org.kakara.client.game.commands;

import org.kakara.client.game.world.ClientChunk;
import org.kakara.client.game.world.ClientWorld;
import org.kakara.core.command.CommandSender;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModCommand;
import org.kakara.core.player.Player;
import org.kakara.core.world.ChunkLocation;
import org.kakara.game.GameUtils;
import org.kakara.game.Server;

import java.util.Collections;

public class RegenerateCommand extends ModCommand {
    private Server server;

    public RegenerateCommand(Mod mod, Server server) {
        super(Collections.singleton("regen"), "", mod, "regenerate");
        this.server = server;
    }


    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        if (executor instanceof Player) {
            ChunkLocation chunkLocation = GameUtils.getChunkLocation(((Player) executor).getLocation());
            executor.sendMessage("Regenerating" + chunkLocation.toString());
            ClientWorld clientWorld = (ClientWorld) ((Player) executor).getLocation().getNullableWorld();
            ClientChunk chunk = (ClientChunk) clientWorld.getChunkAt(chunkLocation);
            chunk.setUpdatedHappened(true);
        }
    }
}
