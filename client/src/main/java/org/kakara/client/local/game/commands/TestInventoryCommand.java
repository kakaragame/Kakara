package org.kakara.client.local.game.commands;


import org.kakara.client.local.game.player.ClientPlayer;
import org.kakara.core.common.Kakara;
import org.kakara.core.common.command.CommandSender;
import org.kakara.core.common.mod.Mod;
import org.kakara.core.common.player.Player;
import org.kakara.core.server.ServerGameInstance;
import org.kakara.core.server.game.ServerItemStack;
import org.kakara.core.server.gui.ServerBoxedInventoryContainer;
import org.kakara.core.server.player.ServerPlayer;
import org.kakara.game.Server;
import org.kakara.game.commands.BuiltinCommand;
import org.kakara.game.server.gui.bnbi.BasicNineBoxedInventory;
import org.kakara.game.server.gui.bnbi.BasicNineBoxedInventoryBuilder;

import java.util.Collections;

public class TestInventoryCommand extends BuiltinCommand {
    private final Server server;

    public TestInventoryCommand(Mod mod, Server server) {
        super(Collections.singleton("tinv"), "", "test-inventory");
        this.server = server;
    }


    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        if (executor instanceof Player) {
            BasicNineBoxedInventory build = new BasicNineBoxedInventoryBuilder().setCapacity(9).build();
            ServerItemStack stack = ((ServerGameInstance) Kakara.getGameInstance()).createItemStack(Kakara.getGameInstance().getItemManager().getItems().get(2));
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(0,stack);
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(1,stack);
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(2,stack);
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(3,stack);
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(4,stack);
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(5,stack);
            ((ServerBoxedInventoryContainer) build.getContainer()).setItemStack(6,stack);
            ((ServerPlayer) executor).openMenu(build);
        }
    }
}
