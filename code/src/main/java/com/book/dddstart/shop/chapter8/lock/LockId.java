package com.book.dddstart.shop.chapter8.lock;

import lombok.Getter;

@Getter
public class LockId {
    private String value;

    public LockId(String value) {
        this.value = value;
    }
}
