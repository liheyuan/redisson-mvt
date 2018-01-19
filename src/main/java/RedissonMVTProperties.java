/**
 * @(#)RedissonMVTConfig.java, Jan 19, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.io.InputStream;
import java.util.Properties;

/**
 * @author coder4
 */
public class RedissonMVTProperties {

    private Properties properties;

    public RedissonMVTProperties() {

    }

    public boolean init() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            properties = new Properties();
            properties.load(in);
            in.close();
            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public String getSingleServerAddress() {
        return properties.getProperty("serverAddress");
    }

}