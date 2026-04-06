package my.hns.roles;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Roles implements Listener {
    public Player player;
    public Roles(Player p) {
        this.player = p;
        RegisterEvents();
    }
    public abstract void OnGameStart();
    public abstract void RegisterEvents();
}
