package com.book.dddstart.shop.chapter6.service;

public class MemberServiceHelper {
    public static Member findExistingMember(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new NoMemberException(memberId);
        }
        return member;
    }
}
