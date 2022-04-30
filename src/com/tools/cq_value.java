package com.tools;

/*
cq_value 裡面存儲以下陣列
1. column qualifier
2. value
目的是用來傳遞多種資料
 */

public class cq_value {

    private String cq[] ;
    private String value[] ;
    private int length;

    public cq_value(String[] cq, String[] value) {
        this.cq = cq;
        this.value = value;
        this.length = cq.length;
    }

    public cq_value(){

    }

    public String[] getCq() {
        return cq;
    }

    public void setCq(String[] cq) {
        this.cq = cq;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
