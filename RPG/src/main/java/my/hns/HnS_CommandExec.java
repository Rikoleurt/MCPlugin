package my.hns;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HnS_CommandExec implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(command.getName().equalsIgnoreCase("squidGame")) {
            if (sender instanceof Player player) {
                player.sendMessage("SquidGame!");
                return true;
            }
        }



        return false;
    }
}
