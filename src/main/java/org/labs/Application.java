package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Application {
    private final SoupService soupService;
    private final List<AtomicLong> foodEaten;
    private final Thread[] programmers;

    public Application(ApplicationConfig config) {
        soupService = new SoupService(config);

        List<Spoon> spoons = new ArrayList<>();
        for (int i = 0; i < config.programmersCount(); ++i) {
            spoons.add(new Spoon());
        }

        foodEaten = new ArrayList<>();
        for (int i = 0; i < config.programmersCount(); ++i) {
            foodEaten.add(new AtomicLong());
        }

        programmers = new Thread[config.programmersCount()];
        for (int i = 0; i < config.programmersCount(); ++i) {
            programmers[i] = new Thread(new Programmer(
                    soupService,
                    spoons.get(i == 0 ? 0 : i - 1),
                    spoons.get(i == 0 ? spoons.size() - 1 : i),
                    foodEaten.get(i)
            ));
        }
    }

    public void start() {
        soupService.start();

        for (Thread thread : programmers) {
            thread.start();
        }
    }

    public void stop() throws InterruptedException {
        try {
            for (Thread thread : programmers) {
                thread.join();
            }
        } finally {
            soupService.close();
        }
    }

    public List<Long> getFoodEaten() {
        return foodEaten.stream().map(AtomicLong::get).toList();
    }
}
