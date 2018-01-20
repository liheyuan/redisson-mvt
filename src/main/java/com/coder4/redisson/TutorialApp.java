package com.coder4.redisson; /**
 * @(#)TutorialApp.java, Jan 19, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author coder4
 */
public class TutorialApp {

    public static void main(String[] args) {

        RedissonMVTProperties prop = new RedissonMVTProperties();
        if (!prop.init()) {
            System.err.println("prop load failed");
            System.exit(-1);
        }

        // use single server config
        Config config = new Config();
        config.useSingleServer()
                .setAddress(prop.getSingleServerAddress());

        // get redissonClient
        RedissonClient redissonClient = Redisson.create(config);

        RSet<Long> testSet = redissonClient.getSet("test1");
        testSet.add(1l);
        System.out.println(testSet.size());

        // shutdown
        redissonClient.shutdown();
    }

}