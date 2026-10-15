package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class SoupServiceTest {

    @Test
    void servesSoupUntilGlobalSupplyIsExhausted() throws Exception {
        try (SoupService soupService = new SoupService(new ApplicationConfig(2, 1, 1))) {
            soupService.start();

            assertTrue(soupService.requestSoup(0).get(1, TimeUnit.SECONDS));
            assertFalse(soupService.requestSoup(0).get(1, TimeUnit.SECONDS));
        }
    }

    @Test
    void doesNotServeMoreSoupThanGlobalSupplyUnderConcurrentRequests() throws Exception {
        int programmers = 4;
        CountDownLatch ready = new CountDownLatch(programmers);
        CountDownLatch start = new CountDownLatch(1);

        try (
                SoupService soupService = new SoupService(
                        new ApplicationConfig(programmers, 1, 3)
                );
                ExecutorService executor = Executors.newFixedThreadPool(programmers)
        ) {
            soupService.start();
            List<Future<Boolean>> requests = new ArrayList<>();

            for (int programmerId = 0; programmerId < programmers; programmerId++) {
                long foodEaten = programmerId;
                requests.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    return soupService.requestSoup(foodEaten).get(1, TimeUnit.SECONDS);
                }));
            }

            assertTrue(ready.await(1, TimeUnit.SECONDS));
            start.countDown();

            int served = 0;
            for (Future<Boolean> request : requests) {
                if (request.get(1, TimeUnit.SECONDS)) {
                    served++;
                }
            }

            assertEquals(3, served);
        }
    }

    @Test
    void servesProgrammerWhoHasEatenLessFirst() throws Exception {
        try (SoupService soupService = new SoupService(new ApplicationConfig(2, 1, 1))) {
            Future<Boolean> programmerWhoAteMore = soupService.requestSoup(5);
            Future<Boolean> programmerWhoAteLess = soupService.requestSoup(1);

            soupService.start();

            assertTrue(programmerWhoAteLess.get(1, TimeUnit.SECONDS));
            assertFalse(programmerWhoAteMore.get(1, TimeUnit.SECONDS));
        }
    }

    @Test
    void doesNotServeSoupWhenGlobalSupplyIsEmpty() throws Exception {
        try (SoupService soupService = new SoupService(new ApplicationConfig(2, 1, 0))) {
            soupService.start();

            assertFalse(soupService.requestSoup(0).get(1, TimeUnit.SECONDS));
        }
    }
}
