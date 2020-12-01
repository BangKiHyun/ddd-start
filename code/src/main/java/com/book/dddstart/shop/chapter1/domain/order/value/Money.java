package com.book.dddstart.shop.chapter1.domain.order.value;

// value type은 불변으로 구현
public class Money {
    private int value;

    public Money(int value) {
        this.value = value;
    }

    public Money add(Money money){
        return new Money(this.value + money.value);
    }

    public Money multiply(int multiplier) {
        return new Money(value * multiplier);
    }

    public int getValue() {
        return value;
    }
}
