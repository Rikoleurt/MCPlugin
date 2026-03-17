package my.hns.commands;

import my.hns.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Menu implements Listener, CommandExecutor {

    private final Component invName = Component.text("Team selector");
    private final Inventory inv = Bukkit.createInventory(null, 27, invName);

    public Menu(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {

            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        return false;
    }

    public void openMenuInventory(Player player){
        ItemStack soul_lantern = new ItemStack(Material.SOUL_LANTERN);
        ItemStack pot = new ItemStack(Material.FLOWER_POT);

        inv.setItem(12, getItem(soul_lantern, "Seekers"));
        inv.setItem(14, getItem(pot, "Hiders"));
        player.openInventory(inv);
    }

    private ItemStack getItem(ItemStack i, String name, String ... lore){
        ItemMeta meta = i.getItemMeta();
        if(i.getType().equals(Material.SOUL_LANTERN)){
            meta.displayName(net.kyori.adventure.text.Component.text(name, TextColor.color(0x2CE4FF)));
            List<net.kyori.adventure.text.Component> lores = new ArrayList<>();
            Collections.addAll(lores, net.kyori.adventure.text.Component.text("Join the seekers!", TextColor.color(0x2CE4FF)));
            meta.lore(lores);
        }
        if(i.getType().equals(Material.FLOWER_POT)){
            meta.displayName(net.kyori.adventure.text.Component.text(name, TextColor.color(0xFF964A)));
            List<net.kyori.adventure.text.Component> lores = new ArrayList<>();
            Collections.addAll(lores, Component.text("Join the hiders!", TextColor.color(0xFF964A)));
            meta.lore(lores);
        }
        i.setItemMeta(meta);

        return i;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(!event.getView().title().equals(invName)) return;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        Main.instance.getLogger().info(player.getName() + " clicked slot: " + slot);
        final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(100));

        if(slot == 12){
            Main.instance.addSeeker(player);
            Title title = Title.title(Component.text("You joined the seekers!", TextColor.color(0x2CE4FF)), Component.text("Have fun!"));
            inv.close();
            player.showTitle(title);
        }
        if(slot == 14){
            Main.instance.addHider(player);
            Title title = Title.title(Component.text("You joined the hiders!", TextColor.color(0xFF964A)), Component.text("Have fun!"));
            inv.close();
            player.showTitle(title);

        }
        // Blague à faire plus tard
        if(slot == 27){
            player.kick();
        }
        event.setCancelled(true);
    }
}
