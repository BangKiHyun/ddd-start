package com.book.dddstart.shop.chapter9;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class ProductId implements Serializable {
    @Column(name = "product_id")
    private String id;

    protected ProductId() {}
    public ProductId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
