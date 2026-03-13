package my.rpg.rPG;

import my.rpg.rPG.HNS.HnSMain;
import my.rpg.rPG.HNS.HnS_CommandExec;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RPG extends JavaPlugin {

    private HnSMain _HnSmain = new HnSMain(getLogger());

    @Override
    public void onEnable() {
        getLogger().info("Hello From RPG");
        LoadCommands();

        _HnSmain.onEnable();
    }

    @Override
    public void onDisable() {
        _HnSmain.onDisable();
    }


    private void LoadCommands() {
        Objects.requireNonNull(getCommand("squidGame")).setExecutor(new HnS_CommandExec());
        Objects.requireNonNull(getCommand("StartGame")).setExecutor(new HnS_CommandExec());
    }
}
