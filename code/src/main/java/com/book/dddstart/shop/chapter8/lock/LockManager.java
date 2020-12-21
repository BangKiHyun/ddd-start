package com.book.dddstart.shop.chapter8.lock;

public interface LockManager {
    // 잠금 선점 시도
    LockId tryLock(String type, String id) throws LockException;

    // 잠금 확인
    // check
    // 1. 잠금의 유효 시간이 지났으면 이미 다른 사용자가 잠금을 선점
    // 2. 잠금을 선점하지 않은 사용자가 기능을 실행했다면 기능 실행을 막아야 함
    void checkLock(LockId lockId) throws LockException;

    // 잠금 해제
    void releaseLock(LockId lockId) throws LockException;

    // 락 유효 시간 연장
    void extendLockExpiration(LockId lockId, long inc) throws LockException;
}
