package org.kakara.client.local.game.commands;


import org.kakara.core.common.command.CommandSender;
import org.kakara.core.common.mod.Mod;
import org.kakara.game.Server;
import org.kakara.game.commands.BuiltinCommand;

import java.util.Collections;

public class KillCommand extends BuiltinCommand {
    private final Server server;

    public KillCommand(Mod mod, Server server) {
        super(Collections.singleton("killer"), "", "kill");
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
