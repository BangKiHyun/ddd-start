package com.book.dddstart.shop.chapter7;

import com.book.dddstart.shop.chapter1.domain.order.OrderLine;
import com.book.dddstart.shop.chapter1.domain.order.value.Money;

import java.util.List;

public class Order {

    private Orderer orderer;
    private List<OrderLine> orderLines;
    private List<Coupon> coupons;

    private Money calcuatePayAmount() {
        final Money totalAmounts = calculateTotalAmounts();

        // 쿠폰별로 할인 금액 구함
        final Money discount = coupons.stream()
                .map(this::calculateDiscount)
                .reduce(Money.ZERO, Money::add);

        // 회원에 따른 추가 할인을 구함
        final Money membershipDiscount = calculateDiscount(orderer.getMember().getGrade());

        // 실제 결제 금액 계산
        return totalAmounts.minus(discount).minus(membershipDiscount);
    }

    private Money calculateTotalAmounts() {
        Money totalAmounts = orderLines.get(0).getAmounts();
        for (int i = 1; i < orderLines.size(); i++) {
            totalAmounts.add(orderLines.get(0).getAmounts());
        }

        return totalAmounts;
    }

    private Money calculateDiscount(Coupon coupon) {
        // orderLines의 각 상품에 대해 쿠폰을 적용해서 할인 금액 계산 로직
    }

    private Money calculateDiscount(MemberGrade grade) {
        // 등급에 따라 할인 금액 계산
    }
}
