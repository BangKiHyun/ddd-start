package com.book.dddstart.shop.chapter8;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StartShippingRequest {

    private Long orderNumber;
    private long version;
}
