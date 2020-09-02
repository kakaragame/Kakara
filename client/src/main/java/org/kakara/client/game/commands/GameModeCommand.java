package org.kakara.client.game.commands;

import org.kakara.core.command.CommandSender;
import org.kakara.core.game.DefaultGameMode;
import org.kakara.core.game.GameMode;
import org.kakara.core.mod.Mod;
import org.kakara.core.mod.game.ModCommand;
import org.kakara.core.player.Player;
import org.kakara.game.GameUtils;

import java.util.Collections;

public class GameModeCommand extends ModCommand {
    public GameModeCommand(Mod mod) {
        super(Collections.singleton("gm"), "", mod, "gamemode");
    }

    @Override
    public void execute(String command, String[] arguments, String fullCommand, CommandSender executor) {
        if (!(executor instanceof Player)) {
            executor.sendMessage("You must be a player");
            return;
        }
        GameMode gameMode;
        if (arguments.length == 0) {
            executor.sendMessage("No gamemode provided");
            return;
        }
        if (arguments[0].contains("#")) {
            gameMode = GameUtils.getGameMode(arguments[0]);
        } else {
            gameMode = DefaultGameMode.valueOf(arguments[0].toUpperCase());
        }
        if (gameMode == null) {
            executor.sendMessage(arguments[0] + " Does not exist");
            return;
        }
        Player player = (Player) executor;
        player.setGameMode(gameMode);
        player.sendMessage("Gamemode changed to " + player.getGameMode().getName());
    }
}
