package com.book.dddstart.shop.chapter6;

public class ChangePasswordService {

    public void changePassword(String memberId, String oldPw, String newPw) {
        Member member = memberRepository.findById(memberId):
        chekcMember(member);
        member.changePassword(oldPw, newPw);
    }
}
