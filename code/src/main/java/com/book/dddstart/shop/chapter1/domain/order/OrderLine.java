package com.book.dddstart.shop.chapter1.domain.order;

public class OrderLine {
    private Product product;
    private int price;
    private int quantity;
    private int amounts;

    public OrderLine(Product product, int price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.amounts = calculateAmounts();
    }

    private int calculateAmounts() {
        return price * amounts;
    }

    public int getAmounts() {
        return amounts;
    }
}
