package com.yk.leetcode.block_queue_1188;

public interface IBoundedBlockingQueue {
    void enqueue(int element);

    int dequeue();

    int size();
}
