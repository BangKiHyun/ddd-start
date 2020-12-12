package com.book.dddstart.shop.chapter6.service;

public class ChangePasswordService {
    private MemberRepository memberRepository;

    public void changePassword(String memberId, String currentPw, String newPw) {
        Member member = memberRepository.findById(memberId);
        if (member == null) throw new NoMemberException(memberId);
        member.changePassword(currentPw, newPw);
    }
}
