package org.buffer;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentRingBufferTest {

    private static final int NUMBER_OF_THREADS = 5;
    private static final int FIRST_VALUE = 1;
    private static final int SECOND_VALUE = 2;
    private static final int FIFTH_VALUE = 5;
    private static final int CAPACITY = 5;

    @Test
    void concurrentOfferTest() throws InterruptedException {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        for (int i = 0; i < CAPACITY; i++) {
            executorService.execute(() -> ringBuffer.offer(FIRST_VALUE));
        }
        executorService.shutdown();

        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(5, ringBuffer.size());
    }

    @Test
    void concurrentPollTest() throws InterruptedException {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        for (int i = 0; i < CAPACITY; i++) {
            ringBuffer.offer(i);
        }

        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                try {
                    ringBuffer.poll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();

        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(2, ringBuffer.size());
    }

    @Test
    void concurrentPeekTest() throws ExecutionException, InterruptedException {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        ringBuffer.offer(FIFTH_VALUE);

        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> ringBuffer.offer(SECOND_VALUE));
            Future<Integer> value = executorService.submit(() -> ringBuffer.peek());
            assertEquals(FIFTH_VALUE, value.get());
        }
        executorService.shutdown();

        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
    }
}
