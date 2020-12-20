package com.book.dddstart.shop.chapter8;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Order {

    @Id
    private Long id;

    @Version
    private long version;

    public boolean matchVersion(long version) {
        return this.version != version;
    }
}
