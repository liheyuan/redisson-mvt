/**
 * @(#)RedissonBatchTestApp.java, Jan 22, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.redisson;

import org.redisson.api.RList;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author coder4
 */
public class RedissonNoBatchTestApp {

    public static void main(String[] args) {
        // get redissonClient
        RedissonClient redissonClient = RedissonClientUtils.createClient();

        // zscore as an example
        System.out.println("======RScoredSortedSet Batch Test======");
        RScoredSortedSet<Abc> rssSet = redissonClient.getScoredSortedSet("rssSetNBT");
        for (int i = 0; i < 10; i++) {
            rssSet.add(i * 10, new Abc(i, "abc" + i));
        }
        // batch add all in one redis cmd
        Map<Abc, Double> batchMap = batchMap = new HashMap<>();
        for (int i = 20; i < 30; i++) {
            batchMap.put(new Abc(i, "abc" + i), (double) i * 10);
        }
        rssSet.addAll(batchMap);

        System.out.println("valueRangeReversed:");
        rssSet.valueRangeReversed(0, -1).stream().forEach(s -> System.out.println(s));
        System.out.println("entryRangeReversed:");
        // notice the score is small -> big
        rssSet.entryRangeReversed(
                20, true,
                50, true,
                0, -1)
                .stream().forEach(s -> System.out.println(s.getScore() + " -> " + s.getValue()));

        redissonClient.shutdown();
    }

}