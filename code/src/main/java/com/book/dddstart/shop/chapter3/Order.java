package com.book.dddstart.shop.chapter3;

import com.book.dddstart.shop.chapter1.domain.order.OrderLine;
import com.book.dddstart.shop.chapter1.domain.order.OrderNo;
import com.book.dddstart.shop.chapter1.domain.order.OrderState;
import com.book.dddstart.shop.chapter1.domain.order.ShippingInfo;

import java.util.List;

public class Order {

    private OrderLines orderLines;
    private int totalAmounts;
    private ShippingInfo shippingInfo;


    // 애그리거트 루트는 도메인 규칙을 구현한 기능을 제공
    public void changeShippingInfo(ShippingInfo newShippingInfo) {
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
    }

    private void verifyNotYetShipped() {
        if (state != OrderState.PAYMENT_WAITING && state != OrderState.PREPARING) {
            throw new IllegalArgumentException("already shipped");
        }
    }

    private void setShippingInfo(ShippingInfo shippingInfo) {
        if (shippingInfo == null) {
            throw new IllegalArgumentException("no ShippingInfo");
        }
        this.shippingInfo = shippingInfo;
    }

    public void changeOrderLines(List<OrderLine> newLines){
        orderLines.changeOrderLines(newLines);
        this.totalAmounts = orderLines.getTotalAmount();
    }

    private void calculatTotalAmounts() {
        int sum = orderLines.stream()
                .mapToInt(ol -> ol.getPrice().getValue())
                .sum();
    }
}