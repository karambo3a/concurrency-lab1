package org.labs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Programmer implements Runnable {
    private static final int MIN_DELAY_MS = 1;
    private static final int MAX_DELAY_MS = 5;

    private final SoupService soupService;
    private final Spoon left;
    private final Spoon right;
    private final AtomicLong foodEaten;

    public Programmer(SoupService soupService, Spoon left, Spoon right, AtomicLong foodEaten) {
        this.soupService = soupService;
        this.left = left;
        this.right = right;
        this.foodEaten = foodEaten;
    }

    @Override
    public void run() {
        try {
            while (true) {
                talk();

                boolean ate = false;

                try {
                    if (!soupService.requestSoup(foodEaten.get()).get()) {
                        return;
                    }

                    takeSpoons();

                    eat();

                    ate = true;
                } finally {
                    if (ate) {
                        putSpoons();
                    }
                }
            }
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    private void takeSpoons() throws InterruptedException {
        left.take();
        right.take();
    }

    private void eat() throws InterruptedException {
        delay();
        foodEaten.incrementAndGet();
    }

    private void putSpoons() throws InterruptedException {
        left.put();
        right.put();
    }

    private void talk() throws InterruptedException {
        delay();
    }

    private void delay() throws InterruptedException {
        Thread.sleep(
                ThreadLocalRandom.current()
                        .nextInt(MIN_DELAY_MS, MAX_DELAY_MS)
        );
    }
}