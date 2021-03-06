/**
 * @(#)CustomCodecs.java, Jan 22, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.redisson;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author coder4
 */
public class SepCodec implements Codec {

    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private Class<? extends Sepable> cls;

    public SepCodec(Class<? extends Sepable> cls) {
        this.cls = cls;
    }

    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        if (in instanceof Sepable) {
            out.writeCharSequence(((Sepable) in).toSepString(), DEFAULT_CHARSET);
        }
        return out;
    };

    private final Decoder<Object> decoder = (buf, state) -> {

        try {
            Constructor<? extends Sepable> ctor = cls.getConstructor();
            Sepable obj = ctor.newInstance();
            String str = buf.toString(DEFAULT_CHARSET);
            buf.readerIndex(buf.readableBytes());
            return obj.fromSepString(str);
        } catch (NoSuchMethodException e) {
            return new Object();
        } catch (Exception e) {
            return new Object();
        }
    };

    @Override
    public Decoder<Object> getMapValueDecoder() {
        return getValueDecoder();
    }

    @Override
    public Encoder getMapValueEncoder() {
        return getValueEncoder();
    }

    @Override
    public Decoder<Object> getMapKeyDecoder() {
        return getValueDecoder();
    }

    @Override
    public Encoder getMapKeyEncoder() {
        return getValueEncoder();
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}