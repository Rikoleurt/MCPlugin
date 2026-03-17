package my.hns;

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

        for(Player p : Seekers)
        {
            p.teleport(seekerCage);
            p.setRotation(179.9f,0);
            Seekers s = new Seekers(p,this);

            s.OnGameStart();
        }

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


        ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.addScoreboardTag("nointeract");

        FallingBlock block = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.STONE.createBlockData());
        block.setDropItem(false);
        block.setNoPhysics(true);
        block.shouldAutoExpire(false);

        player.getVelocity();

        stand.addPassenger(block);
        var task = Bukkit.getScheduler().runTaskTimer(this, () -> {

            if (!player.isOnline()) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            stand.teleport(loc2);

        }, 1, 0L);

        GameTimer timer = new GameTimer(this, 30,true);

        timer
                .onStart(() -> player.sendMessage("Timer started!"))
                .onTick(() ->
                {
                    if (!player.isOnline()) timer.cancel();


                    int TicksLeft = timer.getTickLeft();
                    if (TicksLeft % 20 == 0) {
                        player.sendMessage("Time left: " + TicksLeft/20 + "s");
                    }
                });

        timer.start();
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



