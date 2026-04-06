package my.hns;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer {

    private final JavaPlugin plugin;

    private Runnable onStart = () -> {};
    private Runnable onTick = () -> {};
    private Runnable onEnd = () -> {};
    private Runnable onCancel = () -> {};

    private BukkitRunnable runnable;

    private final int maxTicks;
    private int time;
    private boolean isStarted = false;
    private boolean isPaused = false;

    public GameTimer(JavaPlugin plugin, int maxTicks) {
        this.plugin = plugin;
        this.maxTicks = maxTicks;
    }
    public static GameTimer fromSeconds(JavaPlugin plugin, int seconds) {
        return new GameTimer(plugin, seconds * 20);
    }

    public static GameTimer fromTicks(JavaPlugin plugin, int ticks) {
        return new GameTimer(plugin, ticks);
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
        time = 0;
        isStarted = true;
        isPaused = false;

        onStart.run();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (isPaused) return;

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

    public void cancel() {
        if (runnable != null) {
            runnable.cancel();
            onCancel.run();
            isStarted = false;
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public boolean isStarted()
    {
        return isStarted;
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    public int getTime() {
        return time;
    }

    public int getMaxTime() {
        return maxTicks;
    }

    public int getTickLeft(){
        return maxTicks-time;
    }

    public float getPercentageLeft() { return 1f - ((float) time / maxTicks); }
}
