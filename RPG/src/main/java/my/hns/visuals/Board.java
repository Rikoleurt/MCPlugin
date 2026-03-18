package my.hns.visuals;

import my.hns.Main;
import my.hns.Roles.Roles;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class Board implements Runnable {
    public final static Board instance = new Board();

    private Board() {}

    @Override
    public void run() {

    }

    public void newScoreboard(Player player) {
        Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = s.registerNewObjective(Main.instance.getName(), Criteria.DUMMY, Component.text("test"));

        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.displayName(Component.text("========== Hide and Seek ==========", TextColor.color(0xFF964A)));
        o.getScore("").setScore(6);
        o.getScore("").setScore(5);
        o.getScore("").setScore(4);
        o.getScore("").setScore(3);
        o.getScore("").setScore(2);
        o.getScore("").setScore(1);

        player.setScoreboard(s);
    }

    public void updateScoreboard(Player player) {
        Scoreboard s = player.getScoreboard();
        ArrayList<Player> hiders = Main.instance.getHiders();
        ArrayList<Player> seekers = Main.instance.getSeekers();

    }
}
