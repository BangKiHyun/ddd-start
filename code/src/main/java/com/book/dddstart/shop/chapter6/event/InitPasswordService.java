package com.book.dddstart.shop.chapter6.event;

public class InitPasswordService {

    public void initializePssword(String memberId) {
        Events.handle((PasswordChangeEvent evt) -> {
            // evt.getId()에 이메일 발송 기능 구현
        });

        Member member = memberRepository.findById(memberId);
        member.initializePassword();
    }
}
