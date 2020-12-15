package com.book.dddstart.shop.chapter7.domain.service;

import com.book.dddstart.shop.chapter1.domain.order.OrderLine;
import com.book.dddstart.shop.chapter1.domain.order.value.Money;

import java.util.List;

public class DiscountCalculationService {

    public Money calculateDiscountAmounts(
            List<OrderLine> orderLines,
            List<Coupon> coupons,
            MemberGrade grade) {

        // 쿠폰별로 할인 금액 구함
        final Money couponDiscount = coupons.stream()
                .map(this::calculateDiscount)
                .reduce(Money.ZERO, Money::add);

        // 회원에 따른 추가 할인을 구함
        final Money membershipDiscount = calculateDiscount(orderer.getMember().getGrade());

        return couponDiscount.add(membershipDiscount);
    }

    private Money calculateDiscount(Coupon coupon) {
        // orderLines의 각 상품에 대해 쿠폰을 적용해서 할인 금액 계산 로직
    }

    private Money calculateDiscount(MemberGrade grade) {
        // 등급에 따라 할인 금액 계산
    }
}
