package org.kakara.client.game.commands;

import org.kakara.core.command.CommandSender;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModCommand;
import org.kakara.game.Server;

import java.util.Collections;

public class KillCommand extends ModCommand {
    private Server server;

    public KillCommand(Mod mod, Server server) {
        super(Collections.singleton("killer"), "", mod, "kill");
        this.server = server;
    }


    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        executor.sendMessage("Stopping");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }).start();
    }
}
