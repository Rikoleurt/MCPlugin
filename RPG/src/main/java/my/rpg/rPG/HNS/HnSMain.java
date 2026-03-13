package my.rpg.rPG.HNS;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public class HnSMain {

    private Logger _logger;

    public HnSMain(Logger logger) {
        _logger = logger;
    }

    public void onEnable() {
        _logger.info("Hello From HnS");
    }

    public void onDisable() {

    }
}
