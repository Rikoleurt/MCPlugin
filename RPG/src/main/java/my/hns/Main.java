package my.hns;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Hello From RPG");
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
        Objects.requireNonNull(getCommand("squidGame")).setExecutor(new HnS_CommandExec());
        Objects.requireNonNull(getCommand("StartGame")).setExecutor(new HnS_CommandExec());
    }
}
