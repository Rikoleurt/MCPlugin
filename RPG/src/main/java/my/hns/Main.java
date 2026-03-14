package my.hns;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Bukkit.getWorld;

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
        if(!Seekers.contains(player)){
            getLogger().info("Player joined the seekers team!" + player.getName());
            Seekers.add(player);
        }
    }
    //endregion


    public void startGame() throws InterruptedException {
//        if(Seekers.size() <= 1 || Hiders.size() <= 1){
//            getLogger().info("Not enough players!");
//            return;
//        }
        int max = 200; // 10 seconds

        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {

                if(time % 20 == 0){
                    getLogger().info("Tick! : " + (time / 20f) + "/" + (max / 20f));
                    getServer().broadcast(Component.text((time / 20)));
                }
                for (Player p : Seekers) {
                    p.teleport(new Location(getWorld("world"), -6, 72, -30));
                    p.setGameMode(GameMode.ADVENTURE);
                }
                if (time > max) {
                    cancel();
                    Objects.requireNonNull(getWorld("world")).setBlockData(new Location(getWorld("world"), -6, 72, -29), Material.AIR.createBlockData());
                    Objects.requireNonNull(getWorld("world")).setBlockData(new Location(getWorld("world"), -6, 73, -29), Material.AIR.createBlockData());
                    Objects.requireNonNull(getWorld("world")).setBlockData(new Location(getWorld("world"), -7, 72, -29), Material.AIR.createBlockData());
                    Objects.requireNonNull(getWorld("world")).setBlockData(new Location(getWorld("world"), -7, 73, -29), Material.AIR.createBlockData());
                    getServer().broadcast(Component.text("Game Started!"));
                    return;
                }
                time++;
            }
        }.runTaskTimer(this, 0, 1);
        for(Player p : getServer().getOnlinePlayers()){
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999999, 1));
        }
    }



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

        event.joinMessage(Component.text("Welcome to the server : " + event.getPlayer().getName()));

        // Game instruction
        Player player = event.getPlayer();
        player.sendMessage("** Please join a team with the /join <team> command ** ");
        player.sendMessage("** There must be at least one player in the seekers team and one player in the hiders team **");

        // Lobby set up
        Inventory playerInv = player.getInventory();
        if(playerInv.getItem(0) == null) playerInv.addItem(new ItemStack(Material.DIAMOND));

        int playerNb = getServer().getOnlinePlayers().size();
        getLogger().info("Player number: " + playerNb);

        getServer().getPluginManager().registerEvents(this, this);


        GameTimer timer = new GameTimer(this, 200);



        timer
                .onStart(() -> player.sendMessage("Timer started!"))
                .onTick(() ->
                {
                    int TicksLeft = timer.getTickLeft();
                    if (TicksLeft % 20 == 0) {
                        player.sendMessage("Time left: " + TicksLeft/20 + "s");
                    }
                })
                .onEnd(() -> player.sendMessage("Timer finished!"))
                .onCancel(() -> player.sendMessage("Timer cancelled"));

        timer.start();

        ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.addScoreboardTag("nointeract");

        FallingBlock block = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.STONE.createBlockData());
        block.setDropItem(false);

        stand.addPassenger(block);
        var task = Bukkit.getScheduler().runTaskTimer(this, () -> {

            if (!player.isOnline()) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            stand.teleport(loc2);

        }, 1, 0L);
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
        Objects.requireNonNull(getCommand("team")).setExecutor(_commandExec);
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



