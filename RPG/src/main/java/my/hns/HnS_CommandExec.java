package my.hns;

import my.hns.manager.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class HnS_CommandExec implements CommandExecutor {

    Main instance = Main.instance;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player){

            if(command.getName().equalsIgnoreCase("changeblock")){
                String rawInput1 = args[0];
                String rawInput2 = args[1];
                String upperInput1 = rawInput1.toUpperCase();
                String upperInput2 = rawInput2.toUpperCase();

                instance.getLogger().info(upperInput1);
                instance.getLogger().info(upperInput2);

                Material initialMaterial = Material.getMaterial(upperInput1);
                Material finalMaterial = Material.getMaterial(upperInput2);

                instance.getLogger().info("Initial Material : " + initialMaterial);
                instance.getLogger().info("Final Material : " + finalMaterial);

                ItemStack item1;
                ItemStack item2;

                try {
                    item1 = ItemStack.of(initialMaterial);
                    item2 = ItemStack.of(finalMaterial);
                    instance.getLogger().info("Item 1 :" + item1);
                    instance.getLogger().info("Item 2 :" + item2);

                    ItemManager itemManager = instance.itemManager;

                    if(itemManager.getItems().contains(item1)) itemManager.modifyItem(item1, item2);
                    else sender.sendMessage(ChatColor.RED + "[ALERT] This item isn't currently in the item set");
                } catch (NullPointerException e) {
                    sender.sendMessage(ChatColor.RED + "[ALERT] One of the item doesn't exist");
                }
            }

            switch (command.getName().toLowerCase()){
                case "squidgame" :
                {
                    player.sendMessage(player.name() + " = SquidGame! + Squidos");
                    return true;
                }

                case "startgame":
                {
                    try {
                        Main.instance.startGame();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }

                case "join": {
                    String team = args[0].toLowerCase();
                    if(!team.equals("hider") && !team.equals("seeker")){
                        player.sendMessage("wrong Argument mgl");
                        return  false;
                    }

                    if (team.equals("hider")) Main.instance.addHider(player);
                    if (team.equals("seeker")) Main.instance.addSeeker(player);
                    player.sendMessage("joined Team: " + team);
                    return true;
                }

                case "teamlist":
                {
                    List<String> names = new ArrayList<>();

                    for (Player p : Main.instance.getSeekers()) {
                        names.add(p.getName());
                    }
                    player.sendMessage("Seekers are: " + String.join(", ", names));
                    names.clear();
                    for (Player p : Main.instance.getHiders()) {
                        names.add(p.getName());
                    }
                    player.sendMessage("Hiders are: " + String.join(", ", names));
                    return true;
                }

                case "setseekerstart":
                {
                    Main.instance.seekerCage = player.getLocation();
                    Main.instance.seekerCageRotationYaw= player.getYaw();
                    return true;
                }
            }
        }
        return false;
    }
}
