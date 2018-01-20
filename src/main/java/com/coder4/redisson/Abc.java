package com.coder4.redisson; /**
 * @(#)Abc.java, Jan 20, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * @author coder4
 */
public class Abc {
    private int id;
    private String msg;

    // a must for json serde
    public Abc() {

    }

    public Abc(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
    final StringBuffer sb = new StringBuffer("com.coder4.redisson.Abc{");
        sb.append("id=").append(id);
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}