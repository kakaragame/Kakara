package org.kakara.client.local.game.commands;


import org.kakara.core.common.command.CommandSender;
import org.kakara.core.common.game.DefaultGameMode;
import org.kakara.core.common.game.GameMode;
import org.kakara.core.common.mod.Mod;
import org.kakara.core.common.player.Player;
import org.kakara.game.GameUtils;
import org.kakara.game.commands.BuiltinCommand;

import java.util.Collections;

public class GameModeCommand extends BuiltinCommand {
    public GameModeCommand(Mod mod) {
        super(Collections.singleton("gm"), "", "gamemode");
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        if (!(executor instanceof Player)) {
            executor.sendMessage("You must be a player to execute this command!");
            return;
        }
        GameMode gameMode;
        if (arguments.length == 0) {
            executor.sendMessage("{#a1120a}No gamemode provided!");
            return;
        }
        try{
            if (arguments[0].contains("#")) {
                gameMode = GameUtils.getGameMode(arguments[0]);
            } else {
                gameMode = DefaultGameMode.valueOf(arguments[0].toUpperCase());
            }
        }catch (IllegalArgumentException ex){
            executor.sendMessage(String.format("{#a1120a}The gamemode %s does not exist!", arguments[0]));
            return;
        }

        executor.sendMessage("The functionality of this command is not implemented yet! Please try again on a different version!");
       // Player player = (S) executor;
        //player.setGameMode(gameMode);
       // player.sendMessage("Gamemode changed to " + player.getGameMode().getName());
    }
}
