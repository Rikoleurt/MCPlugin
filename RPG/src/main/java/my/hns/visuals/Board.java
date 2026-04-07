package my.hns.visuals;

import my.hns.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

import static io.papermc.paper.scoreboard.numbers.NumberFormat.blank;

public class Board implements Runnable {

    public final static Board instance = new Board();
    private Board() {}

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
                updateScoreboard(player);
            } else {
                newScoreboard(player);
            }
        }
    }

    public void newScoreboard(Player player) {
        Scoreboard s = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = s.registerNewObjective(Main.instance.getName(), Criteria.DUMMY, Component.text("test"));

        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.displayName(Component.text("---------- Hide and Seek ----------", TextColor.color(0x2D8A9D)));
        o.getScore(" ").setScore(6);
        o.getScore(" ").setScore(5);
        o.getScore(" ").setScore(4);
        o.getScore(" ").setScore(3);
        o.getScore(" ").setScore(2);
        o.getScore(" ").setScore(1);

        player.setScoreboard(s);

    }

    public void updateScoreboard(Player player) {
        Scoreboard s = player.getScoreboard();
        Objective o = s.getObjective(DisplaySlot.SIDEBAR);

        List<String> entries = new ArrayList<>();
        s.getEntries().forEach(s::resetScores);

        entries.add(blank(1));

        showMates(player, entries);
        if(Main.instance.getTimer().isStarted()){
            entries.add(blank(3));
            entries.add(blank(4));
            showTimer(entries);
        } else {
            entries.add(" ");
            entries.add(ChatColor.GOLD + "Game will start soon...");
        }
        showLines(o, entries);
    }

    /**
     * Show the mates of the player on the scoreboard
     * @param player the current player
     * @param lines the lines to show
     */
    private void showMates(Player player, List<String> lines) {
        ArrayList<Player> hiders = Main.instance.getHiders();
        ArrayList<Player> seekers = Main.instance.getSeekers();

        boolean isHider = hiders.contains(player);
        lines.add(" ");

        if (isHider) {
            lines.add(ChatColor.GOLD + "===== Team - Hider =====");
            for (Player p : hiders) {
                lines.add(ChatColor.GOLD + p.getName());
            }
        } else {
            lines.add(ChatColor.AQUA + "===== Team - Seeker =====");
            for (Player p : seekers) {
                lines.add(ChatColor.AQUA + p.getName());
            }
        }
    }

    private void showTimer(List<String> lines) {
        int currentSeconds = Main.instance.getCurrentTime() / 20;
        int maxSeconds = Main.instance.getMaxTime() - 15;

        if (currentSeconds > maxSeconds) {
            return;
        }
        lines.add(ChatColor.GOLD + "■ ■ ■ ■ ■ ■ ■ ■ ");
        lines.add(ChatColor.GOLD + ">> " + currentSeconds + "/" + maxSeconds );
        lines.add(ChatColor.GOLD + "■ ■ ■ ■ ■ ■ ■ ■");
    }

    /**
     * Show the lines we registered on the scoreboard
     * @param o the scoreboard objectives
     * @param lines the lines to show
     */
    private void showLines(Objective o, List<String> lines) {
        int score = lines.size();
        for (String line : lines) {
            if(score > 15) break;
            o.getScore(line).setScore(score);
            score--;
        }
    }

    private String blank(int size) {
        return " ".repeat(size);
    }
}
