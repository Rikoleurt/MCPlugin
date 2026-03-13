package my.hns;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class Main extends JavaPlugin implements Listener {

    public ArrayList<Player> Seekers = new ArrayList<Player>(5);
    public ArrayList<Player> Hiders = new ArrayList<Player>(5);

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
    }

    private void LoadCommands() {

        HnS_CommandExec _commandExec = new HnS_CommandExec(this);

        Objects.requireNonNull(getCommand("squidGame")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("StartGame")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("join")).setExecutor(_commandExec);
        Objects.requireNonNull(getCommand("team")).setExecutor(_commandExec);

    }
}
