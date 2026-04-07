package my.hns.roles;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Hider extends Roles {

    boolean isDead = false;
    GameTimer lastMovedSince = GameTimer.fromSeconds(Main.instance,3);
    Location LastPosedBlockPos;

    ArmorStand armorStandFollower;
    FallingBlock fallingBlockFollower;
    BukkitTask taskFollower;

    Material material;

    public Hider(Player p, Material material){
        super(p);
        this.material = material;

        lastMovedSince
        .onStart(() ->{
            p.setExp(0);
        })
        .onTick(() -> {
            if(1 - lastMovedSince.getPercentageLeft() > .05f)
                p.setExp(1 - lastMovedSince.getPercentageLeft());
        })

        .onEnd(() -> {
            p.setExp(1);
            p.getWorld().setBlockData(p.getLocation(), material.createBlockData());
            LastPosedBlockPos = p.getLocation();

            Main.instance.getLogger().info(LastPosedBlockPos.getBlockX() + " " + LastPosedBlockPos.getBlockY() + " " + LastPosedBlockPos.getBlockZ() + " ");
            p.setGameMode(GameMode.SPECTATOR);
            Main.instance.getLogger().info("LE 2 "  + LastPosedBlockPos.getBlockX() + " " + LastPosedBlockPos.getBlockY() + " " + LastPosedBlockPos.getBlockZ() + " ");

            var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
            Main.instance.hider_PosedBlock.put(v,this);
        });

    }
    @Override
    public void OnGameStart(Material material) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setMaxHealth(4);

        armorStandFollower = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStandFollower.setInvisible(true);
        armorStandFollower.setGravity(false);
        armorStandFollower.setMarker(true);
        armorStandFollower.addScoreboardTag("nointeract");

        fallingBlockFollower = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), material.createBlockData());
        fallingBlockFollower.setDropItem(false);
        fallingBlockFollower.setNoPhysics(true);
        fallingBlockFollower.shouldAutoExpire(false);

        Main.instance.hider_FallingBlock.put(fallingBlockFollower,this);

        armorStandFollower.addPassenger(fallingBlockFollower);

        taskFollower = Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {

            if (!player.isOnline()) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            armorStandFollower.teleport(loc2);

        }, 1, 0L);
    }

    @Override
    public void OnGameStart() {}

    @Override
    public void RegisterEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this,Main.instance);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if(event.getPlayer() != player) return;
        if(isDead) return;

        if(player.isInWater()) {
            player.damage(10);
            return;
        }

        if (
            event.getFrom().getBlockX() != event.getTo().getBlockX()
            || event.getFrom().getBlockY() != event.getTo().getBlockY()
            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ){
            if(player.getGameMode() == GameMode.SPECTATOR){
                player.setGameMode(GameMode.ADVENTURE);
                player.getWorld().setBlockData(LastPosedBlockPos, Material.AIR.createBlockData());

                var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
                Main.instance.hider_PosedBlock.remove(v);
            }

            lastMovedSince.cancel();
            lastMovedSince.start();
        }
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent event){
        if(event.getPlayer() != player) return;

        isDead = true;
        player.setGameMode(GameMode.SPECTATOR);

        taskFollower.cancel();
        fallingBlockFollower.remove();
        armorStandFollower.remove();
        lastMovedSince.cancel();

        var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
        Main.instance.hider_PosedBlock.remove(v,this);
        Main.instance.hider_FallingBlock.remove(fallingBlockFollower,this);
    }


    public void damageHider(Player damager) {
        player.setGameMode(GameMode.ADVENTURE);

        var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
        Main.instance.hider_PosedBlock.remove(v,this);
        player.getWorld().setBlockData(LastPosedBlockPos, Material.AIR.createBlockData());

        player.damage(2,damager);
    }
}
