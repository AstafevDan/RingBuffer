package org.buffer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RingBufferTest {

    private static final int FIRST_VALUE = 1;
    private static final int SECOND_VALUE = 2;
    private static final int THIRD_VALUE = 3;
    private static final int FOURTH_VALUE = 4;
    private static final int FIFTH_VALUE = 5;
    private static final int SIXTH_VALUE = 6;
    private static final int CAPACITY = 5;
    private static final String EXPECTED_STRING = "[ 2, 3, 4, 5, 6 ]";

    @Test
    void offerTest() {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);

        ringBuffer.offer(FIRST_VALUE);

        assertEquals(1, ringBuffer.size());
        assertFalse(ringBuffer.isEmpty());
    }

    @Test
    void offerFullTest() {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);

        ringBuffer.offer(FIRST_VALUE);
        ringBuffer.offer(SECOND_VALUE);
        ringBuffer.offer(THIRD_VALUE);
        ringBuffer.offer(FOURTH_VALUE);
        ringBuffer.offer(FIFTH_VALUE);
        ringBuffer.offer(SIXTH_VALUE);

        assertEquals(5, ringBuffer.size());
        assertFalse(ringBuffer.isEmpty());
    }

    @Test
    void pollTest() throws InterruptedException {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);

        ringBuffer.offer(FIRST_VALUE);
        ringBuffer.offer(SECOND_VALUE);
        int value = ringBuffer.poll();

        assertEquals(1, ringBuffer.size());
        assertEquals(FIRST_VALUE, value);
    }

    @Test
    void pollFullTest() throws InterruptedException {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);

        ringBuffer.offer(FIRST_VALUE);
        ringBuffer.offer(SECOND_VALUE);
        ringBuffer.offer(THIRD_VALUE);
        ringBuffer.offer(FOURTH_VALUE);
        ringBuffer.offer(FIFTH_VALUE);
        ringBuffer.offer(SIXTH_VALUE);
        int value = ringBuffer.poll();

        assertEquals(4, ringBuffer.size());
        assertEquals(SECOND_VALUE, value);
    }

    @Test
    void peekTest() {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);
        ringBuffer.offer(FIRST_VALUE);
        ringBuffer.offer(SECOND_VALUE);
        ringBuffer.offer(THIRD_VALUE);

        assertEquals(FIRST_VALUE, ringBuffer.peek());
    }

    @Test
    void toStringTest() {
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(CAPACITY);

        ringBuffer.offer(FIRST_VALUE);
        ringBuffer.offer(SECOND_VALUE);
        ringBuffer.offer(THIRD_VALUE);
        ringBuffer.offer(FOURTH_VALUE);
        ringBuffer.offer(FIFTH_VALUE);
        ringBuffer.offer(SIXTH_VALUE);

        assertEquals(EXPECTED_STRING, ringBuffer.toString());
    }
}
