package org.kakara.client.game.commands;

import org.kakara.core.Kakara;
import org.kakara.core.command.CommandSender;
import org.kakara.core.game.ItemStack;
import org.kakara.core.gui.bnbi.BasicNineBoxedInventory;
import org.kakara.core.gui.bnbi.BasicNineBoxedInventoryBuilder;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModCommand;
import org.kakara.core.player.Player;
import org.kakara.game.Server;

import java.util.Collections;

public class TestInventoryCommand extends ModCommand {
    private Server server;

    public TestInventoryCommand(Mod mod, Server server) {
        super(Collections.singleton("tinv"), "", mod, "test-inventory");
        this.server = server;
    }


    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        if (executor instanceof Player) {
            BasicNineBoxedInventory build = new BasicNineBoxedInventoryBuilder().setCapacity(9).build();
            ItemStack stack = Kakara.createItemStack(Kakara.getItemManager().getItems().get(2));
            build.setItemStack(stack,0);
            build.setItemStack(stack,1);
            build.setItemStack(stack,2);
            build.setItemStack(stack,3);
            build.setItemStack(stack,4);
            build.setItemStack(stack,5);
            build.setItemStack(stack,7);
            build.setItemStack(stack,8);
            ((Player) executor).openMenu(build);

        }
    }
}
