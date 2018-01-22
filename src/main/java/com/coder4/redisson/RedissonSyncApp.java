/**
 * @(#)RedissonBatchTestApp.java, Jan 22, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.redisson;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @author coder4
 */
public class RedissonSyncApp {

    private static void work(RLock lock, int threadNum) {
        for (int i = 0; i < 5; i++) {
            lock.lock();
            System.out.format("thread %d print %d\n", threadNum, i);
            lock.unlock();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // get redissonClient
        RedissonClient redissonClient = RedissonClientUtils.createClient();

        // Simple Lock
        RLock lock = redissonClient.getLock("lock");
        Thread tl1 = new Thread(() -> work(lock, 1));
        Thread tl2 = new Thread(() -> work(lock, 2));
        tl1.start();
        tl2.start();
        tl1.join();
        tl2.join();

        // CountDownLatch
        RCountDownLatch latch = redissonClient.getCountDownLatch("countDownLatch");
        latch.trySetCount(10);
        Thread td1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                latch.countDown();
                System.out.println("countdown: " + latch.getCount());
            }
        });
        td1.start();
        latch.await();
        td1.join();

        redissonClient.shutdown();
    }

}