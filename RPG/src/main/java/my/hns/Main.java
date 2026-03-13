package my.hns;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class Main extends JavaPlugin implements Listener {

    //region PlayerList
    private ArrayList<Player> Seekers = new ArrayList<Player>(5);
    private ArrayList<Player> Hiders = new ArrayList<Player>(5);

    public ArrayList<Player> getSeekers() {
        return Seekers;
    }
    public ArrayList<Player> getHiders() {
        return Hiders;
    }

    public void addHider(Player player){
        Seekers.remove(player);
        if(!Hiders.contains(player)) Hiders.add(player);
    }
    public void addSeeker(Player player){
        Hiders.remove(player);
        if(!Seekers.contains(player)) Seekers.add(player);
    }
    //endregion

    @Override
    public void onEnable() {
        getLogger().info("Hello From Hide and Seek");
        LoadCommands();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("Player joined: " + event.getPlayer().getName());
        event.joinMessage(Component.text("Welcome to the server : " + event.getPlayer().getName()));

        // Game instruction
        Player player = event.getPlayer();
        player.sendMessage("** Please join a team with the /join <team> command ** ");
        player.sendMessage("** There must be at least one player in the seekers team and one player in the hiders team **");

        int playerNb = getServer().getOnlinePlayers().size();
        getLogger().info("Player number: " + playerNb);

        getServer().getPluginManager().registerEvents(this, this);
    }
    //new ItemStack(Material.DIAMOND)
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Team selector"));
//        inv.setItem(12, metaData("Seekers", Material.LAPIS_LAZULI));
//        inv.setItem(14, metaData(new ItemStack(Material.EMERALD), "Hiders", Material.DIAMOND));
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {
                getLogger().info("Player interacted with: " + Material.DIAMOND);
                event.getPlayer().openInventory(inv);
            }
        }
    }


    private void LoadCommands() {

        HnS_CommandExec _commandExec = new HnS_CommandExec(this);

        Objects.requireNonNull(getCommand("squidGame")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("StartGame")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("join")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("team")).setExecutor(_commandExec);

    }

    private void metaData(ItemStack i, String name, Material material, String ... lore) {
        ItemMeta meta = i.getItemMeta();
        meta.displayName(Component.text(name));
    }

    private void metaData(ItemStack i, String name, Material material, String ... lore) {
        ItemMeta meta = i.getItemMeta();
        meta.displayName(Component.text(name));
    }
}



