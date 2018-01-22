/**
 * @(#)AbstractSepable.java, Jan 22, 2018.
 * <p>
 * Copyright 2018 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.redisson;

/**
 * @author coder4
 */
public abstract class AbstractSepable implements Sepable {

    private static final String SEP = ",";

    @Override
    public String toSepString() {
        String[] arr = getFields();
        if (arr == null || arr.length == 0) {
            return "";
        }
        if (arr.length != getFieldCounts()) {
            throw new RuntimeException("getFieldCounts() != getFields().length");
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr == null) {
                throw new RuntimeException("fields[" + i + "] is null");
            }
            if (arr[i].contains(SEP)) {
                throw new RuntimeException("fields[" + i + "] contains seperator");
            }
        }
        return String.join(SEP, arr);
    }

    @Override
    public Object fromSepString(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        String[] fields = str.split(SEP);
        if (fields == null || fields.length == 0) {
            return null;
        } else if (fields.length != getFieldCounts()) {
            throw new RuntimeException("getFieldCounts() != sepArr.length");
        }

        return fromFields(fields);
    }

    protected abstract String[] getFields();

    protected abstract int getFieldCounts();

    protected abstract Object fromFields(String[] fields);
}