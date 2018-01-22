/**
 * @(#)RedissonBatchTestApp.java, Jan 22, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.redisson;

import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScoredSortedSetAsync;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author coder4
 */
public class RedissonBatchTestApp {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // get redissonClient
        RedissonClient redissonClient = RedissonClientUtils.createClient();

        // create batch
        RBatch batch = redissonClient.createBatch();

        // zscore as an example
        System.out.println("======RScoredSortedSet Batch Test======");
        RScoredSortedSetAsync<Abc> rssSet = batch.getScoredSortedSet("rssSetBT"); // get async
        rssSet.deleteAsync();
        for (int i = 0; i < 10; i++) {
            rssSet.addAsync(i * 10, new Abc(i, "abc" + i));
        }
        // batch add all in one redis cmd
        Map<Abc, Double> batchMap = batchMap = new HashMap<>();
        for (int i = 20; i < 30; i++) {
            batchMap.put(new Abc(i, "abc" + i), (double) i * 10);
        }
        rssSet.addAllAsync(batchMap);

        // read in batch
        Future<Collection<Abc>> future1 = rssSet.valueRangeReversedAsync(0, -1);

        Future<Collection<ScoredEntry<Abc>>> future2 = rssSet.entryRangeReversedAsync(
                20, true,
                50, true,
                0, -1);

        // end batch
        System.out.println("batch execute result:");
        BatchResult<?> result = batch.execute();
        result.forEach(System.out::println);

        System.out.println("valueRangeReversed:");
        future1.get().stream().forEach(s -> System.out.println(s));
        System.out.println("entryRangeReversed:");
        future2.get().forEach(s -> System.out.println(s.getScore() + " -> " + s.getValue()));

        redissonClient.shutdown();
    }

}