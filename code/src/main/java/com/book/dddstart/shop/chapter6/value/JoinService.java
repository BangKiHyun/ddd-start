package com.book.dddstart.shop.chapter6.value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JoinService {

    @Transactional
    public void join(JoinRequest joinRequest) {
        //값의 형식 검사
        checkEmpty(joinRequest.getId(), "id");
        checkEmpty(joinRequest.getName(), "name");
        checkEmpty(joinRequest.getPassword(), "password");
        if(!joinRequest.getPassword().equals(joinRequest.getConfirmPassword()))
            throw new InvalidPropertyException("confirmPassword");

        // 로직 검사
        checkDuplicateId(joinRequest.getId());
    }

    private void checkEmpty(String value, String propertyName) {
        if(value == null || value.isEmpty())
            throw new EmptyPropertyException(propertyName);
    }

    private void checkDuplicateId(String id) {
        int count = memberRepository.countsById(id);
        if(count > 0) throw new DuplicateIdException();
    }
}
