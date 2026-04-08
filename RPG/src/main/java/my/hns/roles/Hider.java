package my.hns.roles;

import my.hns.GameTimer;
import my.hns.Main;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Hider extends Roles {

    boolean isDead = false;
    GameTimer lastMovedSince = GameTimer.fromSeconds(Main.instance,3);
    Location LastPosedBlockPos;
    boolean haveFollowers = false;
    ArmorStand armorStand;
    FallingBlock fallingBlockFollower;
    BukkitTask taskFollower;
    Main instance = Main.instance;
    Material material;

    public Hider(Player p, Material material){
        super(p);
        this.material = material;

        World world = p.getWorld();
        BlockData blockData = world.getBlockData(p.getLocation());

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
            if(blockData.getMaterial() != Material.AIR) { // Cancel timer if there's a block on the player position
                lastMovedSince.cancel();
                lastMovedSince.start();
                return;
            }
            // Else set a block on the player position
            world.setBlockData(p.getLocation(), material.createBlockData());
            LastPosedBlockPos = p.getLocation(); // Get the current location of the player
            p.setGameMode(GameMode.SPECTATOR);
            Vector v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());

            instance.hider_PosedBlock.put(v,this); // Store the vector inside the HashMap
            instance.getLogger().info("[Hider.OnEnd()] Hider = " + p.getName() + ", Vector = " + instance.hider_PosedBlock.get(p.getLocation().toVector()));
            
            removeFollowers(); // Remove the falling block and the armor stand that follows the player
        });

    }

    /**
     * Creates the armor stand and the falling block that'll follow the player
     */
    private void createFollowers(){
        armorStand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.addScoreboardTag("nointeract");

        fallingBlockFollower = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), material.createBlockData());
        fallingBlockFollower.setDropItem(false);
        fallingBlockFollower.setNoPhysics(true);
        fallingBlockFollower.shouldAutoExpire(false);

        Main.instance.hider_FallingBlock.put(fallingBlockFollower,this);
        Main.instance.hider_FallingBlock.put(armorStand,this);

        armorStand.addPassenger(fallingBlockFollower);
        haveFollowers = true;
    }
    private void removeFollowers(){

        instance.hider_FallingBlock.remove(fallingBlockFollower);
        instance.hider_FallingBlock.remove(armorStand,this);

        fallingBlockFollower.remove();
        armorStand.remove();
        armorStand = null;
        fallingBlockFollower = null;

        haveFollowers = false;
    }
    @Override
    public void OnGameStart(Material material) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setMaxHealth(4);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300*20, 2));

        createFollowers();

        taskFollower = Bukkit.getScheduler().runTaskTimer(Main.instance, () -> {
            if (!player.isOnline()) return;
            if (!haveFollowers) return;

            Location loc2 = player.getLocation().add(0, 0.05, 0);
            armorStand.teleport(loc2);

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
        if(!instance.hasGameStarted) return;
        if(isDead) return;
        if(player.isInWater()) {
            instance.getLogger().info(player.getName() + " in the water");
            player.damage(10);
            return;
        }

        if (
                event.getFrom().getBlockX() != event.getTo().getBlockX()
            || event.getFrom().getBlockY() != event.getTo().getBlockY()
            || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ){
            if(player.getGameMode() == GameMode.SPECTATOR){

                boolean sameColumn =
                        event.getFrom().getBlockX() == event.getTo().getBlockX()
                        && event.getFrom().getBlockZ() == event.getTo().getBlockZ();

                boolean changedY = event.getFrom().getBlockY() != event.getTo().getBlockY();

                if (sameColumn && changedY)
                {
                    event.setCancelled(true);
                    return;
                }

                player.setGameMode(GameMode.ADVENTURE);
                player.getWorld().setBlockData(LastPosedBlockPos, Material.AIR.createBlockData());

                Vector v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
                instance.getLogger().info("[OnPlayerMove] Person = " + instance.hider_PosedBlock.get(v) + ", Vector = " + v);

                instance.hider_PosedBlock.remove(v);
                instance.getLogger().info("[OnPlayerMove] Person = " + instance.hider_PosedBlock.get(v) + ", Vector = " + v);

                createFollowers();
            }

            lastMovedSince.cancel();
            lastMovedSince.start();
        }
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() != player) return;

        instance.getLogger().info("[OnDamage] Hider = " + event.getEntity().getLocation().toVector() + ", Vector = " + instance.hider_PosedBlock.get(event.getEntity().getLocation().toVector()));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 175, 0));
        if(!haveFollowers) createFollowers();
    }
    @EventHandler
    public void OnDeath(PlayerDeathEvent event){
        if(event.getPlayer() != player) return;

        isDead = true;
        removeFollowers();
        player.setGameMode(GameMode.SPECTATOR);

        taskFollower.cancel();
        lastMovedSince.cancel();
        Vector v;

        if(LastPosedBlockPos != null){
            v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
            instance.hider_PosedBlock.remove(v,this);
        }

        instance.nbHider--;
        instance.getLogger().info("Number of hider left : " + instance.nbHider);

        if(instance.haveHidersLost()){
            instance.hasGameStarted = false;
            instance.showEndGame();
            instance.getTimer().cancel();
        }
    }

    public void damageHider(Player damager) {
        player.setGameMode(GameMode.ADVENTURE);

        if(LastPosedBlockPos != null){
            var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());
            Main.instance.hider_PosedBlock.remove(v,this);
            player.getWorld().setBlockData(LastPosedBlockPos, Material.AIR.createBlockData());
        }

        player.damage(2,damager);
    }
}
