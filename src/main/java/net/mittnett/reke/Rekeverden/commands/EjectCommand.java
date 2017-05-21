package net.mittnett.reke.Rekeverden.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by edvin on 26.07.16.
 */
public class EjectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be used by Players");
            return true;
        }

        ((Player) commandSender).eject();

        return true;

    }
}
