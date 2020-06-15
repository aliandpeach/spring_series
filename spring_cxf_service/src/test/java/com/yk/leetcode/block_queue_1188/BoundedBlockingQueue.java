package com.yk.leetcode.block_queue_1188;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BoundedBlockingQueue implements IBoundedBlockingQueue {

    private Object lock = new Object();

    List<Integer> list = new ArrayList<>();

    private int capacity;

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void enqueue(int element) {
        synchronized (this) {
            while (list.size() >= capacity) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            list.add(element);
            notifyAll();
        }
    }

    @Override
    public int dequeue() {
        synchronized (this) {
            while (list.size() <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Integer i = list.get(list.size() - 1);
            notifyAll();
            return i;
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return list.size();
        }
    }

    public static void main(String[] args) {
        List<Integer> l = new ArrayList<>();
        l.add(1);
        l.add(2);
        l.add(4);
        l.add(5);
        System.out.println(l);

        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(4);
        System.out.println(queue);
        System.out.println(queue.offer(6));
        System.out.println(queue.offerFirst(6));
        System.out.println(queue.pop());
        System.out.println(queue.poll());
        System.out.println(queue.peek());
        System.out.println(queue);
    }
}
