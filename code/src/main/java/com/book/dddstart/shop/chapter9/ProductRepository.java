package com.book.dddstart.shop.chapter9;

import com.myshop.catalog.domain.category.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, ProductId> {
}
