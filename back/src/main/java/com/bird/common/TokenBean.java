package com.bird.common;

import java.io.Serializable;

/**
 * token存储
 *
 * @author zhyyy
 **/
public class TokenBean implements Serializable {
    private static final long serialVersionUID = 1936056658480287561L;

    private long ts;
    private String token;

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
