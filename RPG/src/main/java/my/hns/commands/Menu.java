package my.hns.commands;

import my.hns.Main;
import my.hns.manager.ItemManager;
import my.hns.visuals.Board;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Menu implements Listener, CommandExecutor {

    private final Component teamSelectorComp = Component.text("Team selector");
    private final Component propSelectorComp = Component.text("Prop selector");

    private final Inventory choseTeamInv = Bukkit.createInventory(null, 27, teamSelectorComp);
    private final Inventory chosePropInv = Bukkit.createInventory(null, 27, propSelectorComp);

    private final Board board = Board.instance;
    private final Logger logger = Main.instance.getLogger();
    private final ItemManager itemManager = Main.instance.getItemManager();

    private int index = 0;

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

    public void openChoseTeamInv(Player player){
        ItemStack soul_lantern = new ItemStack(Material.SOUL_LANTERN);
        ItemStack pot = new ItemStack(Material.FLOWER_POT);

        choseTeamInv.setItem(12, getItem(soul_lantern, "Seekers"));
        choseTeamInv.setItem(14, getItem(pot, "Hiders"));
        player.openInventory(choseTeamInv);
    }

    /**
     * Allows the player to open the inventory for choosing its prop for the new game
     * @param player current Player
     * @param items Item stack to define (variable size between 4 and 5 items)
     */
    public void openPropInv(Player player, List<ItemStack> items){
        int size = items.size();

        index = switch (size) {
            case 4 -> 8;
            case 5 -> 7;
            default -> index;
        };

        if(size <= 5) {
            for (ItemStack item : items) {
                index += 2;
                chosePropInv.setItem(index, item);
            }
            player.openInventory(chosePropInv);
        } else {
            logger.info("Please reduce the number of items " + items.size());
        }
    }

    private ItemStack getItem(ItemStack i, String name, String ... lore){
        ItemMeta meta = i.getItemMeta();
        if(i.getType().equals(Material.SOUL_LANTERN)){
            meta.displayName(Component.text(name, TextColor.color(0x2CE4FF)));
            List<Component> lores = new ArrayList<>();
            Collections.addAll(lores, Component.text("Join the seekers!", TextColor.color(0x2CE4FF)));
            meta.lore(lores);
        }
        if(i.getType().equals(Material.FLOWER_POT)){
            meta.displayName(Component.text(name, TextColor.color(0xFF964A)));
            List<Component> lores = new ArrayList<>();
            Collections.addAll(lores, Component.text("Join the hiders!", TextColor.color(0xFF964A)));
            meta.lore(lores);
        }
        if(i.getType().equals(Material.PLAYER_HEAD)){
            meta.displayName(Component.text(name, TextColor.color(0x75DE42)));
            List<Component> lores = new ArrayList<>();
            Collections.addAll(lores, Component.text("Change your props!", TextColor.color(0x75DE42)));
            meta.lore(lores);
        }
        if(i.getType().equals(Material.DIAMOND)){
            meta.displayName(Component.text(name, TextColor.color(0x681AAB)));
            List<Component> lores = new ArrayList<>();
            Collections.addAll(lores, Component.text("Change your team!", TextColor.color(0x681AAB)));
            meta.lore(lores);
        }
        if(itemManager.getItems().contains(i)){
            meta.displayName(Component.text(name, TextColor.color(0xDE8304)));
            List<Component> lores = new ArrayList<>();
            Collections.addAll(lores, Component.text("Current prop, can be changed before game launching", TextColor.color(0xDE8304)));
            meta.lore(lores);
        }
        i.setItemMeta(meta);
        return i;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getView().title().equals(teamSelectorComp)) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            if (slot == 12) {
                player.getInventory().clear();

                Main.instance.addSeeker(player);
                Title title = Title.title(Component.text("You joined the seekers!", TextColor.color(0x2CE4FF)), Component.text("Have fun!"));
                choseTeamInv.close();
                player.showTitle(title);
                board.updateScoreboard(player);

                player.getInventory().setItem(0, getItem(ItemStack.of(Material.DIAMOND), "Change team"));
            }

            if (slot == 14) {
                player.getInventory().clear();

                Main.instance.addHider(player);
                Title title = Title.title(Component.text("You joined the hiders!", TextColor.color(0xFF964A)), Component.text("Chose a prop and have fun!"));
                choseTeamInv.close();
                player.showTitle(title);
                board.updateScoreboard(player);

                player.getInventory().setItem(0, getItem(ItemStack.of(Material.DIAMOND), "Change team"));
                player.getInventory().setItem(1, getItem(ItemStack.of(Material.PLAYER_HEAD), "Change prop"));
            }
            // Blague à faire plus tard
            if (slot == 27) {
                player.kick();
            }
            event.setCancelled(true);
            return;
        }

        if (event.getView().title().equals(propSelectorComp)) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            if (slot < 0 || slot >= chosePropInv.getSize()) return;

            ItemStack chosenItem = chosePropInv.getItem(slot);
            if (chosenItem != null && chosenItem.getType() != Material.AIR) {
                player.getInventory().setItem(2, getItem(chosenItem.clone(), "Current prop"));
                player.closeInventory();
            }

            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
    }
}
