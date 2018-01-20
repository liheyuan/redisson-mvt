package com.coder4.redisson; /**
 * @(#)RedissonObjectApp.java, Jan 19, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import org.apache.commons.io.IOUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBucket;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author coder4
 */
public class RedissonObjectApp {

    public static void main(String[] args) throws Exception {

        // get redissonClient
        RedissonClient redissonClient = RedissonClientUtils.createClient();

        // RBucket
        System.out.println("======RBucket======");
        RBucket<Object> rbucket = redissonClient.getBucket("rbucket");
        rbucket.delete();
        Map<String, Integer> map = new HashMap<>();
        map.put("first", 1);
        rbucket.set(rbucket);
        System.out.println("set succ: " + rbucket.trySet(rbucket)); // return false if already exists

        // RBinaryStream
        System.out.println("======RBinaryStream======");
        try {
            RBinaryStream rstream = redissonClient.getBinaryStream("rstream");
            rstream.delete();
            rstream.delete();
            // file -> redis
            IOUtils.copy(
                    RedissonObjectApp.class.getClassLoader().getResourceAsStream("data.txt")
                    , rstream.getOutputStream());
            // redis -> System.out
            IOUtils.copy(rstream.getInputStream(), System.out);
        } catch (Exception e) {
            System.err.println(e);
        }

        // AtomicLong
        System.out.println("======AtomicLong======");
        RAtomicLong ralong = redissonClient.getAtomicLong("ralong");
        ralong.delete();
        System.out.println("ralong old " + ralong.getAndSet(1));
        System.out.println("ralong new " + ralong.get());
        System.out.println("ralong inc " + ralong.incrementAndGet());

        // topic
        System.out.println("======RTopic======");
        RTopic<String> topic = redissonClient.getTopic("anyTopic");
        topic.addListener((channel, message) -> {
            System.out.println("subscribe: " + message);
        });

        String content = "test topic";
        System.out.println("publish: " + content);
        topic.publish(content);
        Thread.sleep(TimeUnit.SECONDS.toSeconds(5));

        // RHyperLogLog
        System.out.println("======RHyperLogLog======");
        RHyperLogLog<Integer> log = redissonClient.getHyperLogLog("rhlog");
        log.add(1);
        log.add(2);
        log.add(3);

        log.count();



        // shutdown
        redissonClient.shutdown();
    }

}