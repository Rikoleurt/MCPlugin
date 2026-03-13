package my.rpg.rPG;

import my.rpg.rPG.HNS.HnSMain;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPG extends JavaPlugin {

    private HnSMain _HnSmain = new HnSMain(getLogger());

    @Override
    public void onEnable() {
        getLogger().info("Hello From RPG");

        _HnSmain.onEnable();
    }

    @Override
    public void onDisable() {
        _HnSmain.onDisable();
    }
}
