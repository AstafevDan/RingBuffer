package org.buffer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //Первая часть демонстрирует работу основных методов RingBuffer
        RingBuffer<Integer> ringBuffer = new RingBuffer<>(5);
        ringBuffer.offer(1);
        System.out.println(ringBuffer);
        System.out.println("------------------");
        ringBuffer.offer(2);
        System.out.println(ringBuffer);
        System.out.println("------------------");
        ringBuffer.offer(3);
        System.out.println(ringBuffer);
        System.out.println("------------------");
        ringBuffer.offer(4);
        System.out.println(ringBuffer);
        System.out.println("------------------");
        ringBuffer.offer(5);
        System.out.println(ringBuffer);
        System.out.println("------------------");
        System.out.println("First value: " + ringBuffer.peek());
        ringBuffer.offer(6);
        System.out.println(ringBuffer);
        System.out.println("First value: " + ringBuffer.peek());
        System.out.println("------------------");
        System.out.println("Removed value: " + ringBuffer.poll());
        System.out.println("First value: " + ringBuffer.peek());
        ringBuffer.offer(7);
        System.out.println(ringBuffer);
        ringBuffer.offer(8);
        System.out.println(ringBuffer);
        System.out.println("------------------");
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
