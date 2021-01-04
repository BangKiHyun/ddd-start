package com.book.dddstart.shop.chapter9;

import com.book.dddstart.shop.chapter1.domain.order.value.Money;

import javax.persistence.*;

@Entity
public class Product {
    @EmbeddedId
    private ProductId id;

    private String name;

    private Money price;
    private String detail;

    protected Product() {
    }

    public Product(ProductId id, String name, Money price, String detail) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.detail = detail;
    }
}
