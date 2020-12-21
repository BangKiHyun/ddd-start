package com.book.dddstart.shop.chapter8.lock.infra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LockData {
    // primary key (type, id)
    private String type;
    private String id;

    // unique index
    private String lockId;

    private long expirationTime;

    public boolean isExpired() {
        return expirationTime < System.currentTimeMillis();
    }
}
