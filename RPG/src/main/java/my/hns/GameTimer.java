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
    private boolean isStarted = false;
    private boolean isPaused = false;

    public GameTimer(JavaPlugin plugin, int maxTicks) {
        this.plugin = plugin;
        this.maxTicks = maxTicks;
    }
    /// if inSecond is false "maxSecond" will be counted as ticks
    public GameTimer(JavaPlugin plugin, int maxSeconds, boolean inSeconds) {
        this.plugin = plugin;
        if(inSeconds){
            this.maxTicks = maxSeconds * 20;
        }else{
            this.maxTicks = maxSeconds;
        }
    }

    public GameTimer onStart(Runnable r) {
        this.onStart = r;
        isStarted = true;
        return this;
    }

    public GameTimer onTick(Runnable r) {
        this.onTick = r;
        return this;
    }

    public GameTimer onEnd(Runnable r) {
        this.onEnd = r;
        isStarted = false;
        return this;
    }

    public GameTimer onCancel(Runnable r) {
        this.onCancel = r;
        isStarted = false;
        return this;
    }

    public void start() {
        time = 0;

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

    public  boolean getIsStarted ()
    {
        return isStarted;
    }

    public  boolean getIsPaused ()
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

    public float getPercentageLeft() { return 1-(((float)time)/(float)maxTicks); }
}
