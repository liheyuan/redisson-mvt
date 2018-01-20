package com.coder4.redisson; /**
 * @(#)RedissonCollectionApp.java, Jan 20, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RDeque;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * @author coder4
 */
public class RedissonCollectionApp {

    public static void main(String [] args) {
        // get redissonClient
        RedissonClient redissonClient = RedissonClientUtils.createClient();

        // Map
        System.out.println("======RMap======");
        RMap<String, Integer> rMap = redissonClient.getMap("rMap");
        rMap.delete();
        System.out.println("map[\"first\"] prev: " + rMap.put("first", 1));
        System.out.println("map[\"first\"] current: " + rMap.get("first"));
        rMap.putIfAbsent("first", 2);
        System.out.println("map[\"first\"] after putIfAbsent(2): " + rMap.get("first"));
        System.out.println("fastput map[\"first\"]: " + rMap.fastPut("first", 3)); // false:update, true:add

        // Set
        System.out.println("======RSet======");
        RSet<Abc> rSet = redissonClient.getSet("rSet");
        rSet.delete();
        System.out.println("rSet add abc1: " + rSet.add(new Abc(1, "abc1")));
        System.out.println("rSet add abc1 again: " + rSet.add(new Abc(1, "abc1"))); // base on string equals
        System.out.println("rSet remove abc1:" + rSet.remove(new Abc(1, "abc"))); // true succ, false fail
        System.out.println("rSet add abc1 again:" + rSet.add(new Abc(1, "abc")));

        // SortedSet Bug!!
//        RSortedSet<Abc> rsSet = redissonClient.getSortedSet("rsSet");
//        rsSet.delete();
//        rsSet.trySetComparator(Comparator.comparingInt(Abc::getId)); // set object comparator
//        rsSet.trySetComparator(new Comparator<Abc>() {
//            @Override
//            public int compare(Abc o1, Abc o2) {
//                return Integer.compare(o1.getId(), o2.getId());
//            }
////        });
//        rsSet.add(new com.coder4.redisson.Abc(3, "abc3"));
//        rsSet.add(new com.coder4.redisson.Abc(1, "abc3"));
//        rsSet.add(new com.coder4.redisson.Abc(2, "abc3"));
//        rsSet.stream().forEach(abc -> System.out.println(abc));

        // RLexSortedSet
        System.out.println("======RLexSortedSet======");
        RLexSortedSet rlset = redissonClient.getLexSortedSet("rlSet");
        rSet.delete();
        rlset.add("d");
        rlset.addAsync("a");
        rlset.add("f");
        rlset.stream().forEach(s -> System.out.println(s));

        // ScoredSortedSet
        System.out.println("======RScoredSortedSet======");
        RScoredSortedSet<Abc> rssSet = redissonClient.getScoredSortedSet("rssSet");
        for (int i = 0; i < 10; i++) {
            rssSet.add(i * 10, new Abc(i, "abc"+ i));
        }
        System.out.println("valueRangeReversed:");
        rssSet.valueRangeReversed(0, -1).stream().forEach(s -> System.out.println(s));
        System.out.println("entryRangeReversed:");
        // notice the score is small -> big
        rssSet.entryRangeReversed(
                20, true,
                50, true,
                0, -1)
                .stream().forEach(s -> System.out.println(s.getScore() + " -> " + s.getValue()));

        // List
        System.out.println("======RList======");
        RList<Abc> rlist = redissonClient.getList("rList");
        rlist.delete();
        rlist.add(new Abc(1, "first"));
        rlist.add(new Abc(2, "second"));
        rlist.add(new Abc(3, "third"));
        System.out.println("rList lastOne:" + rlist.get(-1));
        rlist.remove("rList remove LastOne:" + rlist.remove(new Abc(3, "third")));
        rlist.forEach(abc -> System.out.println(abc));

        // Queue (FIFO)
        System.out.println("======FIFO======");
        RQueue<Long> rQueue = redissonClient.getQueue("rQueue");
        rQueue.delete();
        rQueue.add(1l);
        rQueue.add(2l);
        rQueue.add(3l);
        System.out.println("peak:" + rQueue.peek());
        System.out.print("after peak:");
        rQueue.stream().forEach(v -> System.out.print(v));
        System.out.print("\n");
        System.out.println("remove:" + rQueue.remove());
        System.out.print("after remove:");
        rQueue.stream().forEach(v -> System.out.print(v));
        System.out.print("\n");

        // Deque
        System.out.println("======Deque======");
        RDeque<Long> rDeque = redissonClient.getDeque("rDeque");
        rDeque.clear();
        rDeque.addLast(2l);
        rDeque.addFirst(1l);
        System.out.print("traverse:");
        rDeque.stream().forEach(v -> System.out.print(v));
        System.out.print("\n");
        System.out.print("traverse:");
        System.out.println("remove first:" + rDeque.removeFirst());
        System.out.print("after remove first:");
        rDeque.stream().forEach(v -> System.out.print(v));
        System.out.print("\n");

        // Blocking Queue
        System.out.println("======Blocking Queue======");
        RBlockingQueue<Integer> rbQueue = redissonClient.getBlockingQueue("rbQueue");
        rbQueue.clear();
        rbQueue.offer(1);

        System.out.println("peek: " + rbQueue.peek()); // get first, not remove
        System.out.println("remove: " + rbQueue.remove()); // get & remove first

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rbQueue.offer(10);
        }).start();
        Integer val = null;
        while (val == null) {
            try {
                val = rbQueue.poll(1, TimeUnit.SECONDS);
                System.out.println("pool result: " + val);
            } catch (Exception e) {
                System.out.println("pool exception: " + e);
            }
        }

        // Delayed Queue
        System.out.println("======RDelayedQueue======");
        RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(rQueue);
        rQueue.delete();
        delayedQueue.delete();
        // test poll thread
        new Thread(() -> {
            Long val1 = 0L;
            while (val1 == null || val1 != 888) {
                val1 = delayedQueue.poll();
                System.out.println(val1);
            }
        }).start();
        // move object to rQueue in 10 seconds
        delayedQueue.offer(888l, 10, TimeUnit.SECONDS);

        //
        System.out.println("======RPriorityQueue======");
        RPriorityQueue<Integer> queue = redissonClient.getPriorityQueue("rpQueue");
        queue.delete();
        // lambda is bug
        // queue.trySetComparator((o1, o2) -> Integer.compare(o2, o1));
        queue.trySetComparator(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o2, o1);
            }
        });
        queue.add(5);
        queue.add(1);
        queue.add(3);
        queue.stream().forEach(s -> System.out.println(s));

        // shutdown
        redissonClient.shutdown();
    }

}