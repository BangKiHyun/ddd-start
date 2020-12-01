package com.book.dddstart.shop.chapter1.domain.order;

import com.book.dddstart.shop.chapter1.domain.order.value.Address;
import com.book.dddstart.shop.chapter1.domain.order.value.Receiver;

public class ShippingInfo {
    // value type
    private Receiver receiver;
    private Address address;

    public ShippingInfo(Receiver receiver, Address address) {
        this.receiver = receiver;
        this.address = address;
    }
}
