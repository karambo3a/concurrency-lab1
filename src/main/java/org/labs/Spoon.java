package org.labs;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Spoon {
    private final Lock lock = new ReentrantLock();

    void take() {
        lock.lock();
    }

    void put() {
        lock.unlock();
    }
}