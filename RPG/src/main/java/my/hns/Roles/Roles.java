package my.hns.Roles;

import my.hns.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Roles implements Listener {

    public Player player;

    public Roles(Player p) {
        this.player = p;

        RegisterEvents();
    }

    public abstract void OnGameStart();

    public abstract void RegisterEvents();


}
