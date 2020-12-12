package com.book.dddstart.shop.chapter6;

import java.util.List;

public class SimpleExam {

    public Result doSomeFunc(SomeReq req) {
        // 리포지터리에서 애그리거터 구함
        SomeAgg agg = someAggRepository.findById(req.getId());
        checkNull(agg);

        // 애그리거트의 도메인 기능 실행
        agg.doFunc(req.getValue());

        // 결과 리턴
        return createSuccessResult(agg);
    }

    public void blockMembers(List<String> blcokingIds){
        List<Member> members = memberRepository.findByIds(blcokingIds);
        for(Member mem : members){
            mem.block();
        }
    }
}
