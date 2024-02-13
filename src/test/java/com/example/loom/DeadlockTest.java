package com.example.loom;

import org.junit.jupiter.api.Test;

public class DeadlockTest {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    @Test
    public void testDeadlock() {
        System.out.println(System.getProperty("java.version"));
        System.out.println(NUMBER_OF_CORES);
        for (int i = 0; i < NUMBER_OF_CORES + 1; i++) {
            Thread.startVirtualThread(() -> {
                System.out.println(Thread.currentThread() + ": Before synchronized block");
                synchronized (DeadlockTest.class) {
                    System.out.println(Thread.currentThread() + ": Inside synchronized block");
                }
            });
        }
    }
}
