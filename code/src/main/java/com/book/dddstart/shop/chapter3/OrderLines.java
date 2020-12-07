package com.book.dddstart.shop.chapter3;

import com.book.dddstart.shop.chapter1.domain.order.OrderLine;

import java.util.List;

public class OrderLines {
    private List<OrderLine> orderLines;

    // 애그리거트 루트 기능 위임
    public void changeOrderLines(List<OrderLine> newLines) {
        this.orderLines = newLines;
    }

    public int getTotalAmount() {
        // doSomething
    }
}
