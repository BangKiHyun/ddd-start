package com.book.dddstart.shop.chapter1.domain.order;

import com.book.dddstart.shop.chapter1.domain.order.value.Money;

public class OrderLine {
    private Product product;
    private Money price;
    private int quantity;
    private Money amounts;

    public OrderLine(Product product, Money price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.amounts = calculateAmounts();
    }

    private Money calculateAmounts() {
        return price.multiply(quantity);
    }

    public Money getAmounts() {
        return amounts;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
