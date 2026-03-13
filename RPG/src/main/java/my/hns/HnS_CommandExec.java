package my.hns;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HnS_CommandExec implements CommandExecutor {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HnS_CommandExec.class);
    private final Logger logger = JavaPlugin.getProvidingPlugin(HnS_CommandExec.class).getLogger();
    Main main;

    public HnS_CommandExec(Main _main){
        main = _main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player){
            switch (command.getName().toLowerCase()){
                case "squidgame" :
                {
                    player.sendMessage(player.name() + " = SquidGame!");
                    return true;
                }

                case "startgame":
                {
                    try {
                        main.startGame();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }

                case "join": {
                    String team = args[0].toLowerCase();

                    if (team.equals("hider")) {
                        main.addHider(player);
                    }
                    if (team.equals("seeker")) {
                        main.addSeeker(player);
                    }
                    player.sendMessage("joined Team: " + team);

                    return true;
                }

                case "team":
                {
                    List<String> names = new ArrayList<>();

                    for (Player p : main.getSeekers()) {
                        names.add(p.getName());
                    }
                    player.sendMessage("Seekers are: " + String.join(", ", names));
                    names.clear();
                    for (Player p : main.getHiders()) {
                        names.add(p.getName());
                    }
                    player.sendMessage("Hiders are: " + String.join(", ", names));
                    return true;
                }
            }


        }

        return false;
    }
}
