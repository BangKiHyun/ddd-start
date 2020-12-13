package com.book.dddstart.shop.chapter6.value;

public class EmptyPropertyException extends RuntimeException {

    private String propertyName;

    public EmptyPropertyException(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }
}
