package engine;

import engine.interfaces.Tickable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Ticker implements Runnable {

    public float MILLIS_PER_FRAME;
    
    ScheduledExecutorService scheduler;
    
    public double lastTickTime = Globals.getTime();
    private double lastFPS = Globals.getTime();
    private boolean running;
    private int tps;
    public final int targetTPS;
    private int ticks = 0;
    private long totalTicks = 0;

    private volatile CopyOnWriteArrayList<Tickable> entities = new CopyOnWriteArrayList<>();

    
    public Ticker(int tps) {
        MILLIS_PER_FRAME = 1_000 / tps;
        targetTPS = tps;
        
        this.tps = -1;
        
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        running = true;
        lastTickTime = Globals.getTime();
        lastFPS = Globals.getTime();
        //TimeUnit.SECONDS.toNanos(1)/60, TimeUnit.NANOSECONDS
        scheduler.scheduleAtFixedRate(new Tick(), TimeUnit.MILLISECONDS.toNanos(30), TimeUnit.SECONDS.toNanos(1)/targetTPS, TimeUnit.NANOSECONDS);
    }

    public void logic() {
        long time = System.currentTimeMillis();
        if (!entities.isEmpty()) {
            for (Tickable e : entities) {
                e.tick();
            }
        }
        totalTicks++;
    }

    public void end() {
        running = false;
        scheduler.shutdownNow();
    }

    public void add(Tickable e) {
        entities.add(e);
    }

    public void remove(Tickable e) {
        if (entities.contains(e)) {
            entities.remove(e);
        }

    }

    public boolean isTicking(Tickable e) {
        return entities.contains(e);
    }

    public int getTPS() {
        return tps;
    }
    
    
    private class Tick implements Runnable {

        @Override
        public void run() {
            if(!running) return;
            logic();
            lastTickTime = Globals.getTime();
            if (Globals.getTime() - lastFPS > 1000) {
                tps = ticks;
                ticks = 0;
                lastFPS += 1000;
            }
            ticks++;
        }
        
    }
}
