package my.hns.Roles;

import my.hns.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class Hider extends Roles{

    public Hider(Player p, Main m){
        super(p,m);

    }
    @Override
    public void OnGameStart() {

        ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.addScoreboardTag("nointeract");

        @SuppressWarnings("deprecation")
        FallingBlock block = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.STONE.createBlockData());
        block.setDropItem(false);
        block.setNoPhysics(true);
        block.shouldAutoExpire(false);

        stand.addPassenger(block);

        var task = Bukkit.getScheduler().runTaskTimer(main, () -> {

            if (!player.isOnline()) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            stand.teleport(loc2);

        }, 1, 0L);
    }

    @Override
    public void RegisterEvents() {

    }

    @EventHandler
    public void onPlayerAttack(PlayerVelocityEvent event){
        //event.getPlayer().sendMessage(event.getVelocity()new);
    }
}
