package org.buffer;

public class Main {
    private static <T> void processRingBuffer(RingBuffer<T> ringBuffer, T value) {
        ringBuffer.offer(value);
        System.out.println(ringBuffer);
        System.out.println("------------------");
    }

    public static void main(String[] args) throws InterruptedException {
        //Первая часть демонстрирует работу основных методов RingBuffer
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(5);
        for (int i = 1; i < 6; i++) {
            processRingBuffer(ringBuffer, i);
        }

        System.out.println("First value: " + ringBuffer.peek());
        processRingBuffer(ringBuffer, 6);
        System.out.println("First value: " + ringBuffer.peek());

        System.out.println("Removed value: " + ringBuffer.poll());
        System.out.println("First value: " + ringBuffer.peek());

        processRingBuffer(ringBuffer, 7);
        processRingBuffer(ringBuffer, 8);

        System.out.println("Size: " + ringBuffer.size());

        //Во второй части демонстрируется многопоточность RingBuffer
        System.out.println("------------------");
        System.out.println("-----Multithreading-----");
        System.out.println("------------------");
        RingBuffer<Integer> mtRingBuffer = new RingBuffer<>(3);
        Thread threadOffer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                mtRingBuffer.offer(i);
                System.out.println("Added: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        );

        Thread threadPoll = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int value;
                try {
                    value = mtRingBuffer.poll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Removed: " + value);
            }
        }
        );

        threadOffer.start();
        threadPoll.start();
    }
}
