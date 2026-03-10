package my.rpg.rPG;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddCommandTest implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(command.getName().equalsIgnoreCase("squidGame")){
            if(sender instanceof Player player){
                player.sendMessage("SquidGame!");
                return true;
            }
        }
        if(command.getName().equalsIgnoreCase("light")){
            if(sender instanceof Player player){
                for(int i = 0; i < Integer.parseInt(args[0]); i++)
                    player.getWorld().strikeLightning(player.getLocation());
                return true;
            }
        }

        return false;
    }
}
