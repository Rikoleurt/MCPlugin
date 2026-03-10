package my.rpg.rPG;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class OnJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.sendMessage("Welcome to the server!");
    }
}