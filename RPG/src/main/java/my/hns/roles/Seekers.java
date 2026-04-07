package my.hns.roles;

import my.hns.GameTimer;
import my.hns.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.Duration;

public class Seekers extends Roles {

    public Seekers(Player p){
        super(p);
    }

    @Override
    public void OnGameStart(Material material) {}

    @Override
    public void OnGameStart() {
        Location seekerCage = Main.instance.seekerCage;
        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));
        player.setGameMode(GameMode.ADVENTURE);
        player.setMaxHealth(20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 3));
        var timer = GameTimer.fromSeconds(Main.instance,10);
        timer.onTick(() -> {
            player.teleport(seekerCage);
            int TicksLeft = timer.getTickLeft();
            if (TicksLeft % 20 == 0) {
                final Component mainTitle = Component.text(TicksLeft / 20, NamedTextColor.WHITE);
                final Component subtitle = Component.text("Get ready", NamedTextColor.GRAY);
                final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(100));
                final Title title = Title.title(mainTitle, subtitle, times);
                player.showTitle(title);
                player.playSound(
                        player.getLocation(),
                        Sound.BLOCK_METAL_HIT,
                        8.0f,
                        1.0f
                );
            }
        })
        .onEnd(() -> {
            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                    15.0f,
                    .75f
            );
        });

        timer.start();
    }

    @Override
    public void RegisterEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(this,Main.instance);
    }

    @EventHandler
    public void onPlayerAttackFallingBlock(io.papermc.paper.event.player.PrePlayerAttackEntityEvent event){
        if(event.getPlayer() != player) return;

        Main.instance.getLogger().info(event.getPlayer().getName() + " attacked " + event.getAttacked().getName());

        var misterX = Main.instance.hider_FallingBlock.get(event.getAttacked());
        if(misterX == null) {player.sendMessage("no Entity found in HashMap"); return;}

        player.sendMessage("Found "+ misterX.player.getName());

        misterX.damageHider(player);
    }

    @EventHandler
    public void onPlayerAttackSolidBlock(PlayerInteractEvent event){
        if(event.getPlayer() != player) return;
        if(event.getAction().isRightClick()) return;

        var bloc = event.getClickedBlock();
        if(bloc == null || bloc.getType() == Material.AIR) return;

        var LastPosedBlockPos = bloc.getLocation();
        var v = new Vector(LastPosedBlockPos.getBlockX(),LastPosedBlockPos.getBlockY(),LastPosedBlockPos.getBlockZ());

        var misterX = Main.instance.hider_PosedBlock.get(v);

        if(misterX == null)
        {
            player.sendMessage("no block found in HashMap");
            return;
        }

        player.sendMessage("Found "+ misterX.player.getName());

        misterX.damageHider(player);
    }
}
