package my.hns.Roles;

import my.hns.GameTimer;
import my.hns.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

public class Hider extends Roles{

    GameTimer lastMovedSince = new GameTimer(main,3,true);

    ArmorStand followerArmorStand;
    BukkitTask ArmorStandIsFollowing;


    public Hider(Player p, Main m){
        super(p,m);

        lastMovedSince.
        onTick(() -> {
            p.setExp(1 - lastMovedSince.getPercentageLeft());
            })

        .onEnd(() -> {
            p.setExp(1);
            var blockData = p.getWorld().getBlockData(p.getLocation());
            p.getWorld().setBlockData(p.getLocation(), Material.STONE.createBlockData());
            ArmorStandIsFollowing.cancel();
            followerArmorStand.removePassenger(followerArmorStand.getPassengers().getFirst());
            followerArmorStand.addPassenger(player);
            //REGISTER TO MAIN


            });

    }
    @Override
    public void OnGameStart() {

        followerArmorStand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        followerArmorStand.setInvisible(true);
        followerArmorStand.setGravity(false);
        followerArmorStand.setMarker(true);
        followerArmorStand.addScoreboardTag("nointeract");

        @SuppressWarnings("deprecation")
        FallingBlock block = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.STONE.createBlockData());
        block.setDropItem(false);
        block.setNoPhysics(true);
        block.shouldAutoExpire(false);

        followerArmorStand.addPassenger(block);

        ArmorStandIsFollowing = Bukkit.getScheduler().runTaskTimer(main, () -> {

            if (!player.isOnline()) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            followerArmorStand.teleport(loc2);

        }, 1, 0L);
    }

    @Override
    public void RegisterEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this,main);
    }



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){

        if (
            event.getFrom().getBlockX() != event.getTo().getBlockX()
            || event.getFrom().getBlockY() != event.getTo().getBlockY()
            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ){

            lastMovedSince.cancel();
            lastMovedSince.start();
        }
    }
}
