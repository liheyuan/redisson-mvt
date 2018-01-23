/**
 * @(#)RedissonBatchTestApp.java, Jan 22, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.redisson;

import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.codec.MsgPackJacksonCodec;

import java.util.Arrays;

/**
 * @author coder4
 */
public class RedissonSerdeApp {

    private static void adds(RList<Abc> rlist) {
        rlist.delete();
        rlist.add(new Abc(1, "first"));
        rlist.add(new Abc(2, "second"));
        rlist.add(new Abc(3, "third"));
    }

    public static void main(String[] args) {
        // get redissonClient
        RedissonClient redissonClient = RedissonClientUtils.createClient();

        // List with JsonJacksonCodec
        System.out.println("======RList with JsonJacksonCodec======");
        RList<Abc> rlistJson = redissonClient.getList("rListJsonJacksonCodec");
        rlistJson.delete();
        adds(rlistJson);
        rlistJson.forEach(abc -> System.out.println(abc));

        // List with xxxJacksonCodecs
        System.out.println("======RList with MsgPackJacksonCodec======");
        RList<Abc> rlistJsonMsgPackJacksonCodec = redissonClient.getList("rListMsgPackJacksonCodec", new MsgPackJacksonCodec());
        rlistJsonMsgPackJacksonCodec.delete();
        adds(rlistJsonMsgPackJacksonCodec);
        rlistJsonMsgPackJacksonCodec.forEach(abc -> System.out.println(abc));

        // List with KryoCodec
        System.out.println("======RList with KryoCodec======");
        RList<Abc> rlistKryoCodec = redissonClient.getList("rListKryoCodec", new KryoCodec(Arrays.asList(Abc.class)));
        rlistKryoCodec.delete();
        adds(rlistKryoCodec);
        rlistKryoCodec.forEach(abc -> System.out.println(abc));

        // List with Custom SepCodec
        System.out.println("======RList with SepCodec======");
        RList<Abc> rlistSepCodec = redissonClient.getList("rlistSepCodec", new SepCodec(Abc.class));
        rlistSepCodec.delete();
        adds(rlistSepCodec);
        rlistSepCodec.forEach(abc -> System.out.println(abc));

        redissonClient.shutdown();
    }

}