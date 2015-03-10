package engine;

import engine.interfaces.TickObject;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ticker implements Runnable {

    public float MILLIS_PER_FRAME;

    ScheduledExecutorService scheduler;

    public double lastTickTime = Globals.getTime();
    private double lastFPS = Globals.getTime();
    private boolean running;
    private boolean paused = false;
    private int tps;
    public final int targetTPS;
    private int ticks = 0;
    private long totalTicks = 0;

    private volatile CopyOnWriteArrayList<TickObject> entities = new CopyOnWriteArrayList<>();

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

        while (running) {
            if (!paused) {
                logic();
                lastTickTime = Globals.getTime();
                if (Globals.getTime() - lastFPS > 1000) {
                    tps = ticks;
                    ticks = 0;
                    lastFPS += 1000;
                }
                ticks++;

                // add sleeptime sensing
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                Logger.getLogger(Ticker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void logic() {
        long time = System.currentTimeMillis();
        if (!entities.isEmpty()) {
            for (TickObject e : entities) {
                e.tick();
            }
        }
        totalTicks++;
    }

    public void end() {
        running = false;
        scheduler.shutdownNow();
    }

    public void add(TickObject e) {
        entities.add(e);
    }

    public void remove(TickObject e) {
        if (entities.contains(e)) {
            entities.remove(e);
        }

    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isTicking(TickObject e) {
        return entities.contains(e);
    }

    public int getTPS() {
        return tps;
    }

    private class Tick implements Runnable {

        @Override
        public void run() {

        }

    }
}
