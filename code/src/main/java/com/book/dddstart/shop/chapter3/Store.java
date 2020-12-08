package com.book.dddstart.shop.chapter3;

public class Store {

    // 팩토리 메서드
    public Product createProduct(ProductId newProductId, ...){
        if(isBlocked()) throw new StoreBlcokException();
        return new Prodcut(newProductId, getId(), ...);
    }

    private boolean isBlocked() {
        // doSomething;
    }
}
