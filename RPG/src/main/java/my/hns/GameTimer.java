package my.hns;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer {

    private JavaPlugin plugin;

    private Runnable onStart = () -> {};
    private Runnable onTick = () -> {};
    private Runnable onEnd = () -> {};
    private Runnable onCancel = () -> {};

    private BukkitRunnable runnable;

    private int maxTicks = 2000;
    private int time = 0;
    private boolean paused = false;

    public GameTimer(JavaPlugin plugin, int maxTicks) {
        this.plugin = plugin;
        this.maxTicks = maxTicks;
    }

    public GameTimer onStart(Runnable r) {
        this.onStart = r;
        return this;
    }

    public GameTimer onTick(Runnable r) {
        this.onTick = r;
        return this;
    }

    public GameTimer onEnd(Runnable r) {
        this.onEnd = r;
        return this;
    }

    public GameTimer onCancel(Runnable r) {
        this.onCancel = r;
        return this;
    }

    public void start() {

        onStart.run();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (paused) return;

                if (time >= maxTicks) {
                    onEnd.run();
                    cancel();
                    return;
                }

                onTick.run();
                time++;
            }
        };

        runnable.runTaskTimer(plugin, 0L, 1L);
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void cancel() {
        if (runnable != null) {
            runnable.cancel();
            onCancel.run();
        }
    }

    public int getTime() {
        return time;
    }
}
