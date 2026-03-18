package my.hns;

import my.hns.Roles.Hider;
import my.hns.Roles.Seekers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public final class Main extends JavaPlugin implements Listener {

    //region PlayerList
    private ArrayList<Player> Seekers = new ArrayList<Player>(5);
    private ArrayList<Player> Hiders = new ArrayList<Player>(5);

    public Location seekerCage = new Location(getServer().getWorld("world"), 4.5, 97, 56.5);
    public float seekerCageRotationYaw = 0;

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
        if(!Seekers.contains(player)){
            getLogger().info("Player joined the seekers team!" + player.getName());
            Seekers.add(player);
        }
    }
    //endregion

    public HashMap<Location,Hider> hider_PosedBlock;

    public void startGame() throws InterruptedException {
        hider_PosedBlock = new HashMap<>(3);
//        if(Seekers.size() <= 1 || Hiders.size() <= 1){
//            getLogger().info("Not enough players!");
//            return;
//        }

        for(Player p : Hiders){
            p.getInventory().clear();

            p.teleport(new Location(p.getWorld(),0,100,0));
            Hider h = new Hider(p,this);
            h.OnGameStart();
        }

        for(Player p : Seekers) {
            p.getInventory().clear();

            p.teleport(seekerCage);
            p.setRotation(179.9f,0);
            Seekers s = new Seekers(p,this);

            s.OnGameStart();
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Hello From Hide and Seek V1");
        LoadCommands();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        event.joinMessage(Component.text("Welcome to the server : " + event.getPlayer().getName()));

        // Game instruction
        Player player = event.getPlayer();
        player.sendMessage("** Please join a team with the /join <team> command ** ");
        player.sendMessage("** There must be at least one player in the seekers team and one player in the hiders team **");

        // Lobby set up
        Inventory playerInv = player.getInventory();
        if(playerInv.getItem(0) == null) playerInv.addItem(new ItemStack(Material.DIAMOND));

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Team selector"));
        ItemStack sword = new ItemStack(Material.SOUL_LANTERN);
        ItemStack pot = new ItemStack(Material.FLOWER_POT);
        inv.setItem(12, metaData(sword, "Seekers"));
        inv.setItem(14, metaData(pot, "Hiders"));
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
        Objects.requireNonNull(getCommand("teamlist")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("setSeekerStart")).setExecutor(_commandExec);

    }

    private ItemStack metaData(ItemStack i, String name, String ... lore) {
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
        i.setItemMeta(meta);

        return i;
    }
}



