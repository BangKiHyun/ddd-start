package com.book.dddstart.shop.chapter9;

import java.util.List;

public interface ProductRecommendationService {
    public List<Product> getRecommendationOf(ProductId id);
}
