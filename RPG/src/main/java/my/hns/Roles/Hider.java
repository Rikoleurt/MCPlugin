package my.hns.Roles;

import my.hns.GameTimer;
import my.hns.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Hider extends Roles{

    GameTimer lastMovedSince = GameTimer.fromSeconds(main,3);
    Location LastPosedBlockPos;

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
            LastPosedBlockPos = p.getLocation();

            main.getLogger().info(LastPosedBlockPos.getBlockX() + " " + LastPosedBlockPos.getBlockY() + " " + LastPosedBlockPos.getBlockZ() + " ");
            p.setGameMode(GameMode.SPECTATOR);
            main.getLogger().info("LE 2 "  + LastPosedBlockPos.getBlockX() + " " + LastPosedBlockPos.getBlockY() + " " + LastPosedBlockPos.getBlockZ() + " ");


            //REGISTER TO MAIN

            var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
            main.hider_PosedBlock.put(v,this);
        });

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
        block.shouldAutoExpire(true); // NEED TO BE FALSE DEBUG PURPOSE ONLY

        stand.addPassenger(block);

        var task = Bukkit.getScheduler().runTaskTimer(main, () -> {

            if (!player.isOnline()) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            stand.teleport(loc2);

        }, 1, 0L);
    }

    @Override
    public void RegisterEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this,main);
    }



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if(event.getPlayer() != player) return;
        if (
            event.getFrom().getBlockX() != event.getTo().getBlockX()
            || event.getFrom().getBlockY() != event.getTo().getBlockY()
            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ){
            if(player.getGameMode() == GameMode.SPECTATOR){
                player.setGameMode(GameMode.CREATIVE); // DEBUG PURPOZSE SHOULD BE ADVENTURE
                player.getWorld().setBlockData(LastPosedBlockPos, Material.AIR.createBlockData());

                var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
                main.hider_PosedBlock.remove(v);
            }

            lastMovedSince.cancel();
            lastMovedSince.start();
        }
    }
}
