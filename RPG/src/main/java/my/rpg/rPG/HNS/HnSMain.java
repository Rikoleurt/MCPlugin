package my.rpg.rPG.HNS;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;


public class HnSMain {

    private Logger _logger;

    private EGamePhase _gamePhase = EGamePhase.E_SELECTION;

    public HnSMain(Logger logger) {
        _logger = logger;
    }

    public void onEnable() {
        _logger.info("Hello From HnS");
    }

    public void onDisable() {

    }

    //AFTER SELECTION IS DONE
    public void StartGame(){
        _gamePhase = EGamePhase.E_INGAME;
    }


}
