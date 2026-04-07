package my.hns;

import my.hns.manager.ItemManager;
import my.hns.roles.Hider;
import my.hns.roles.Seekers;
import my.hns.commands.Menu;
import my.hns.visuals.Board;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.*;

public final class Main extends JavaPlugin implements Listener {

    public static Main instance;
    Board board = Board.instance;
    Menu menu;
    ItemManager itemManager = new ItemManager();
    ItemStack[] items = {
            new ItemStack(Material.ACACIA_BOAT),
            new ItemStack(Material.ACACIA_DOOR),
            new ItemStack(Material.ACACIA_FENCE),
            new ItemStack(Material.ACACIA_LEAVES),
            new ItemStack(Material.ACACIA_PLANKS),
    };

    //region PlayerList
    private final ArrayList<Player> seekers = new ArrayList<>();
    private final ArrayList<Player> hiders = new ArrayList<>();
    public Location seekerCage = new Location(getServer().getWorld("world"), 4.5, 97, 56.5);
    public float seekerCageRotationYaw = 0;
    public ArrayList<Player> getSeekers() {
        return seekers;
    }
    public ArrayList<Player> getHiders() {
        return hiders;
    }

    public void addHider(Player player){
        seekers.remove(player);
        if(!hiders.contains(player)) hiders.add(player);
    }

    public void addSeeker(Player player){
        hiders.remove(player);
        if(!seekers.contains(player)){
            getLogger().info("Player joined thae seekers team!" + player.getName());
            seekers.add(player);
        }
    }
    //endregion

    //region StartGame
    public HashMap<Vector,Hider> hider_PosedBlock;
    public HashMap<Entity,Hider> hider_FallingBlock;
    public int currentTime;
    public int maxTime = 45;
    GameTimer timer = GameTimer.fromSeconds(this, maxTime);
    public int nbHider = -1;

    public void startGame() throws InterruptedException {
        hider_PosedBlock = new HashMap<>(10);
        hider_FallingBlock = new HashMap<>(10);
//        if(Seekers.size() <= 1 || Hiders.size() <= 1){
//            getLogger().info("Not enough players!");
//            return;
//        }


        for (Player p : hiders) {
            ItemStack chosenItem = p.getInventory().getItem(2);
            if (chosenItem == null || chosenItem.getType() == Material.AIR) {
                p.sendMessage(ChatColor.RED + "[ALERT] Someone didn't choose an item. Please choose an item.");
                return;
            }

            Material material = chosenItem.getType();
            Hider h = new Hider(p, material);
            h.OnGameStart(material);
        }

        nbHider = hiders.size();


        for(Player p : seekers) {
            p.getInventory().clear();

            //p.teleport(seekerCage);
            //p.setRotation(179.9f,0);
            Seekers s = new Seekers(p);

            s.OnGameStart();
        }

        launchGameTimer();
    }

    //endregion

    //region JavaPlugin
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Hello From Hide and Seek V1");
        loadCommands();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, board, 0, 20);
        menu = new Menu(this);
        addItems();
    }

    @Override
    public void onDisable() {}
    //endregion

    //region Events

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text("Welcome to the server : " + event.getPlayer().getName()));

        // Game instruction
        Player player = event.getPlayer();

        // Lobby set up
        Inventory playerInv = player.getInventory();
        if(playerInv.getItem(0) == null) playerInv.addItem(new ItemStack(Material.DIAMOND));
        board.newScoreboard(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        if (mainHand.getType() == Material.AIR) return;

        Material type = mainHand.getType();

        if (type == Material.DIAMOND) {
            menu.openChoseTeamInv(player);
            return;
        }

        if (type == Material.PLAYER_HEAD) {
            menu.openPropInv(player, itemManager.getItems());
        }
    }
    //endregion

    //region Helpers
    private void loadCommands() {
        HnS_CommandExec _commandExec = new HnS_CommandExec();

        Objects.requireNonNull(getCommand("squidGame")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("StartGame")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("join")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("teamlist")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("setSeekerStart")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("menu")).setExecutor(new Menu(this));
        Objects.requireNonNull(getCommand("changeblock")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("sound")).setExecutor(_commandExec);
    }

    private void launchGameTimer(){
        timer
                .onStart(() -> {
                    currentTime = timer.getTime();
                    getLogger().info("Timer started");
                    Title title = Title.title(
                            Component.text("Game started"),
                            Component.text("GL HF"),
                            Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(2000), Duration.ofMillis(100)));

                    for(Player p : Bukkit.getOnlinePlayers()) {
                        p.showTitle(title);
                    }
                })
                .onTick(() -> {
                    currentTime = timer.getTickLeft();
                    int currentSeconds = currentTime / 20;
                    if(currentTime % 20 == 0) {
                        int half = (timer.getMaxTime() / 20 - 15) / 2;
                        boolean is5secondLeft = currentTime / 20 <= 5;
                        Title halfTitle = Title.title(
                                Component.text(currentSeconds + " seconds left", NamedTextColor.WHITE),
                                Component.text(""),
                                Title.Times.times(Duration.ofMillis(1000), Duration.ofMillis(1000), Duration.ofMillis(100)));

                        Title rest5Title = Title.title(
                                Component.text(currentTime / 20 + " seconds left", NamedTextColor.GOLD),
                                Component.text(""),
                                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(100)));
                        double half1 = half - 0.5;
                        double half2 = half + 0.5;
                        if (currentSeconds >= half1 && currentSeconds <= half2) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.showTitle(halfTitle);
                            }
                        }
                        if (is5secondLeft) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.showTitle(rest5Title);
                            }
                        }
                    }

                })
                .onEnd(this::showEndGame);
        timer.start();
    }

    public void showEndGame(){
        Title winTitle = Title.title(
                Component.text("You win this round!", NamedTextColor.DARK_GREEN),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(3000), Duration.ofMillis(100)));

        Title lostTitle = Title.title(
                Component.text("You lost this round! (T'es nul fdp)", NamedTextColor.DARK_RED),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(3000), Duration.ofMillis(100)));

        if(!haveHidersLost()) {
            getLogger().info("Hiders win the round!");
            for (Player p : hiders) {
                p.showTitle(winTitle);
            }
            for (Player p : seekers) {
                p.showTitle(lostTitle);
            }
        } else {
            getLogger().info("Seekers win the round!");
            for (Player p : hiders) {
                p.showTitle(lostTitle);
            }
            for (Player p : seekers) {
                p.showTitle(winTitle);
            }
        }
    }
    public boolean haveHidersLost(){
        return nbHider == 0;
    }

    private void addItems() {
        itemManager.addAll(items);
    }
    //endregion

    //region GetSet
    public ItemManager getItemManager() {
        return itemManager;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public GameTimer getTimer() {
        return timer;
    }
    //endregion
}



