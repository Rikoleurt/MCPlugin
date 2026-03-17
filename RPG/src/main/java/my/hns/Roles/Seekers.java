package my.hns.Roles;

import my.hns.GameTimer;
import my.hns.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

public class Seekers extends Roles  {

    public Seekers(Player p, Main m){
        super(p,m);
    }

    @Override
    public void OnGameStart() {

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 3));
        var timer = new GameTimer(main,15,true);
        timer.onTick(() -> {

            int TicksLeft = timer.getTickLeft();
            if (TicksLeft % 20 == 0) {
                final Component mainTitle = Component.text(TicksLeft / 20, NamedTextColor.WHITE);
                final Component subtitle = Component.text("get Ready", NamedTextColor.GRAY);

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
        Bukkit.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onPlayerAttack(io.papermc.paper.event.player.PrePlayerAttackEntityEvent event){
        player.sendMessage(event.getPlayer().getName() + " attacked " + event.getAttacked().getName());
    }
}
