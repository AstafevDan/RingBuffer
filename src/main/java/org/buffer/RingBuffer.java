package org.buffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс представляет собой многопоточную коллекцию {@code RingBuffer} (круговая очередь).
 * Реализован на основе связного списка. Работает по принципу FIFO.
 * Содержит основные методы по вставке и удалению элемента, а также просмотру первого элемента в очереди.
 * Все операции синхронизированы с помощью {@link ReadWriteLock}, то есть коллекция пригодна для использования в многопоточной среде.
 *
 * @param <T> тип объектов, хранящихся в этой структуре данных
 * @author Даниил Астафьев
 * @version 1.0
 * @see ReadWriteLock
 */
public class RingBuffer<T> {

    /**
     * Узел - составляющая часть {@code RingBuffer}.
     * Rear.next указывает на самый "старый" элемент списка.
     */
    private Node<T> rear;

    /**
     * Текущий размер коллекции.
     */
    private int size;

    /**
     * Вместимость коллекции. Является фиксированной.
     */
    private final int capacity;

    /**
     * {@link Lock}, содержащий пару блокировок - для чтения и для записи.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Блокировка, используемая для операций записи.
     */
    private final Lock writeLock = lock.writeLock();

    /**
     * Блокировка, используемая для операций чтения.
     */
    private final Lock readLock = lock.readLock();

    /**
     * Условие, сигнализирующее о том, что коллекция не пуста.
     */
    private final Condition notEmptyCondition = writeLock.newCondition();

    /**
     * При создании {@code RingBuffer} инициализируем вместимость коллекции.
     *
     * @param capacity вместимость коллекции
     */
    public RingBuffer(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Метод для добавления элемента в {@code RingBuffer}. Является синхронизированным. Также сигнализирует другим потокам, что коллекция не пуста.
     * Если структура данных полностью заполнена, то перезаписывает самый "старый" элемент в {@code RingBuffer}.
     *
     * @param value значение, добавляемое в {@code RingBuffer}
     */
    public void offer(T value) {
        try {
            writeLock.lock();

            if (this.isFull()) {
                this.rear.next.val = value;
                this.rear = this.rear.next;
                notEmptyCondition.signal();
            } else {
                Node<T> newNode = new Node<>(value);
                if (this.isEmpty()) {
                    newNode.next = newNode;
                } else {
                    newNode.next = this.rear.next;
                    this.rear.next = newNode;
                }
                this.rear = newNode;

                this.size++;
                notEmptyCondition.signal();
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Метод для удаления элемента из {@code RingBuffer}. Если очередь пустая, то ждет соответствующего сигнала от других потоков о ее заполнении. Является синхронизированным.
     *
     * @return удаленное значение типа {@code T}.
     * @throws InterruptedException
     */
    public T poll() throws InterruptedException {
        try {
            writeLock.lock();

            while (this.isEmpty()) {
                notEmptyCondition.await();
            }
            T tmp = this.rear.next.val;

            if (this.size == 1) {
                this.rear = null;
            } else {
                this.rear.next = this.rear.next.next;
            }

            this.size--;

            return tmp;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Метод возвращает первый элемент коллекции без его удаления из очереди. Является синхронизированным.
     *
     * @return значение самого "старого" в коллекции объекта типа {@code T}
     */
    public T peek() {
        try {
            readLock.lock();
            return this.isEmpty() ? null : this.rear.next.val;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Метод возвращает {@code true}, если коллекция пуста. Иначе - {@code false}. Является синхронизированным.
     *
     * @return {@code true}, если коллекция пуста, иначе - {@code false}
     */
    public boolean isEmpty() {
        try {
            readLock.lock();
            return this.size == 0;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Метод возвращает количество элементов в {@code RingBuffer}. Является синхронизированным.
     *
     * @return размер коллекции
     */
    public int size() {
        try {
            readLock.lock();
            return this.size;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Вспомогательный метод, чтобы проверить полностью ли заполен {@code RIngBuffer}.
     *
     * @return {@code true}, если коллекция полностью заполнена, иначе - {@code false}
     */
    private boolean isFull() {
        return this.size == this.capacity;
    }

    /**
     * Переопределенный метод для представления элементов коллекции в виде строки. Является синхронизированным.
     *
     * @return строку, представляющую {@code RingBuffer}
     */
    @Override
    public String toString() {
        try {
            readLock.lock();
            StringBuffer str = new StringBuffer();
            Node<T> curr = rear.next;
            int count = 0;
            str.append("[ ");
            while (count != this.size) {
                str.append(curr.val).append(", ");
                curr = curr.next;
                count++;
            }
            str.deleteCharAt(str.lastIndexOf(",")).append("]");
            return str.toString();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Вспомогательный класс, описывающий узел, который является составной частью {@code RingBuffer}.
     *
     * @param <T> тип объектов, хранящихся в этом узле
     * @author Даниил Астафьев
     * @version 1.0
     */
    static class Node<T> {
        /**
         * Значение, хранящееся в узле.
         */
        T val;

        /**
         * Ссылка на следующий узел.
         */
        Node<T> next;

        public Node(T val) {
            this.val = val;
            this.next = null;
        }
    }
}
