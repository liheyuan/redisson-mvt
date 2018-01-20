package com.coder4.redisson; /**
 * @(#)RedissonClientUtil.java, Jan 20, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author coder4
 */
public class RedissonClientUtils {

    public static RedissonClient createClient() {
        RedissonMVTProperties prop = new RedissonMVTProperties();
        if (!prop.init()) {
            System.err.println("prop load failed");
            System.exit(-1);
        }

        // use single server config
        Config config = new Config();
        config.useSingleServer()
                .setAddress(prop.getSingleServerAddress());

        return Redisson.create(config);
    }

}