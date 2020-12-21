package com.book.dddstart.shop.chapter8.lock.infra;

import com.book.dddstart.shop.chapter8.lock.LockException;
import com.book.dddstart.shop.chapter8.lock.LockId;
import com.book.dddstart.shop.chapter8.lock.LockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SpringLockManager implements LockManager {
    private int lockTimeout = 5 * 60 * 1000;
    private JdbcTemplate jdbcTemplate;

    private RowMapper<LockData> lockDataRowMapper = (rs, rowNum) ->
            new LockData(rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getTimestamp(4).getTime());

    @Transactional
    @Override
    public LockId tryLock(String type, String id) throws LockException {
        // 해당 type과 id에 잠금이 존재하는지 검사
        checkAlreadyLocked(type, id);

        // 새로운 LockId 생성
        final LockId lockId = new LockId(UUID.randomUUID().toString());

        // 잠금 생성
        locking(type, id, lockId);
        return lockId;
    }

    private void checkAlreadyLocked(String type, String id) {
        List<LockData> locks = jdbcTemplate.query(
                "select * from locks where type = ? and id = ?",
                lockDataRowMapper, type, id);
        final Optional<LockData> lockData = handleExpiration(locks);

        // 유효 시간이 지나지 않은 LockData가 존재하면 익셉션 발생
        if (lockData.isPresent()) throw new AlreadyLockException();
    }

    // 유효 시간이 지난 데이터 처리
    // 잠금의 유효 시간이 지나면 해당 데이터 삭제
    private Optional<LockData> handleExpiration(List<LockData> locks) {
        if (locks.isEmpty()) return Optional.empty();

        final LockData lockData = locks.get(0);
        if (lockData.isExpired()) {
            jdbcTemplate.update(
                    "delete from locks where type = ? and id = ?",
                    lockData.getType(), lockData.getId());
            return Optional.empty();
        }

        return Optional.of(lockData);
    }

    // 데이터 삽입 결과가 없거나, 동일한 주요 키나 lockId를 가진 데이터가 이미 존재한다면 익셉션 발생
    private void locking(String type, String id, LockId lockId) {
        try {
            final int updatedCount = jdbcTemplate.update(
                    "insert into locks values (?,?,?,?)",
                    type, id, lockId.getValue(), new Timestamp(getExpirationTime()));
            if (updatedCount == 0) throw new LockingFailException();
        } catch (DuplicateKeyException e) {
            throw new LockingFailException(e);
        }
    }

    // 현재 시간 기준으로 유효 시간 생성
    private long getExpirationTime() {
        return System.currentTimeMillis() + lockTimeout;
    }

    // 잠금이 유효한지 검사
    // 잠금이 존재하지 않으면 익셉션 발생
    @Override
    public void checkLock(LockId lockId) throws LockException {
        final Optional<LockData> lockData = getLockData(lockId);
        if (!lockData.isPresent()) throw new NoLockException();
    }

    private Optional<LockData> getLockData(LockId lockId) {
        final List<LockData> locks = jdbcTemplate.query(
                "select * from lock where lockId = ?",
                lockDataRowMapper, lockId.getValue());
        return handleExpiration(locks);
    }

    // 잠금 데이터를 locks 테이블에서 삭제
    @Override
    public void releaseLock(LockId lockId) throws LockException {
        jdbcTemplate.update("delete from locks where lockId = ?", lockId.getValue());
    }

    // 잠금 유효 시간을 inc만큼 늘린다.
    @Override
    public void extendLockExpiration(LockId lockId, long inc) throws LockException {
        final Optional<LockData> lockDataOpt = getLockData(lockId);
        final LockData lockData =
                lockDataOpt.orElseThrow(() -> new NoLockException());
        jdbcTemplate.update(
                "update locks set expiration_time = ? where type = ? AND id = ?",
                new Timestamp(lockData.getExpirationTime() + inc),
                lockData.getType(), lockData.getId());
    }
}
