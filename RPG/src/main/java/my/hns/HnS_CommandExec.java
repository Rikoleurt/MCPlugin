package my.hns;

import my.rpg.rPG.RPG;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class HnS_CommandExec implements CommandExecutor {

    private final Logger logger = JavaPlugin.getProvidingPlugin(HnS_CommandExec.class).getLogger();
    private final HnSMain hnsmain = new HnSMain(logger);


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("squidGame")) {
            if (sender instanceof Player player) {
                logger.info("command : squidGame launched");
                player.sendMessage("SquidGame!");
                return true;
            }
        }
        if(command.getName().equalsIgnoreCase("join")) {
            if(args[0].equalsIgnoreCase("hiders")) {
                if (sender instanceof Player player) {
                    player.sendMessage("Joining hiders!");
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("seekers")) {
                if (sender instanceof Player player) {
                    player.sendMessage("Joining seekers!");
                    return true;
                }
            }
        }
        if(command.getName().equalsIgnoreCase("hiders")){
            if(args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player) {
                    logger.info("command : hiders list launched");
                    return true;
                }
            }
        }
        // LIST
        if(command.getName().equalsIgnoreCase("seekers")) {
            if(args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player player) {
                    logger.info("command : seekers list launched");
                    return true;
                }
            }
        }
        return false;
    }
}
