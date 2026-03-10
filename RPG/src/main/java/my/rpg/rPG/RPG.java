package my.rpg.rPG;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class RPG extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadCommands();
        getServer().getPluginManager().registerEvents(new OnJoinListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onLoad(){
        super.onLoad();

        getLogger().info("Hello World!");
    }




    private void loadCommands(){
        Objects.requireNonNull(getCommand("squidGame")).setExecutor(new AddCommandTest());
        Objects.requireNonNull(getCommand("light")).setExecutor(new AddCommandTest());
    }
}
