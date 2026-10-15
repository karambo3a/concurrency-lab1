package org.labs;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class SoupService implements AutoCloseable {
    private record Order(long foodEaten, CompletableFuture<Boolean> result) {
    }

    private final Thread[] waiters;
    private final PriorityBlockingQueue<Order> queue;
    private final AtomicLong foodCnt;

    public SoupService(ApplicationConfig config) {
        waiters = new Thread[config.waitersCount()];
        queue = new PriorityBlockingQueue<>(config.programmersCount(), Comparator.comparingLong(Order::foodEaten));
        foodCnt = new AtomicLong(config.foodCount());

        for (int i = 0; i < waiters.length; ++i) {
            waiters[i] = new Thread(this::waiter);
        }
    }

    public void start() {
        for (Thread waiter : waiters) {
            waiter.start();
        }
    }

    public Future<Boolean> requestSoup(long foodEaten) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        queue.add(new Order(foodEaten, result));
        return result;
    }

    private void waiter() {
        try {
            while (true) {
                Order order = queue.take();
                order.result.complete(foodCnt.decrementAndGet() >= 0);
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void close() throws InterruptedException {
        for (Thread waiter : waiters) {
            waiter.interrupt();
        }
        for (Thread waiter : waiters) {
            waiter.join();
        }
    }
}