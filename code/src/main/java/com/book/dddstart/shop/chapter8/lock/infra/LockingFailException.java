package com.book.dddstart.shop.chapter8.lock.infra;

import org.springframework.dao.DuplicateKeyException;

public class LockingFailException extends RuntimeException {
    public LockingFailException(DuplicateKeyException e) {
    }

    public LockingFailException() {

    }
}
