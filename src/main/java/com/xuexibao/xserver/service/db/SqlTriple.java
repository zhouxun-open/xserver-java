package com.xuexibao.xserver.service.db;

public class SqlTriple {
    public String fieldName;
    public String condition;
    public Object value;

    public SqlTriple(String fieldName, String condition, Object value) {
        this.fieldName = fieldName;
        this.condition = condition;
        this.value = value;
    }
}
