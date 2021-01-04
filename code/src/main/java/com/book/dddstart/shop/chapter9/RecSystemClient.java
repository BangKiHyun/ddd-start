package com.book.dddstart.shop.chapter9;

import java.util.List;
import java.util.stream.Collectors;

public class RecSystemClient implements ProductRecommendationService{
    private ProductRepository productRepository;

    @Override
    public List<Product> getRecommendationOf(ProductId id) {
        final List<RecommendationItem> items = getRecItems(id.getId());
        return toProducts(items);
    }

    private List<RecommendationItem> getRecItems(String id) {
        // 외부 추천 시스템을 위한 클라이언트라고 가정
        return externalRecClient.getRecs(id);
    }

    private List<Product> toProducts(List<RecommedationItem> items){
        return items.stream()
                .map(item -> toProductId(item.getItemId()))
                .map(productId -> productRepository.findById(productId))
                .collect(Collectors.toList());
    }

    private ProductId toProductId(String itemId) {
        return new ProductId(itemId);
    }
}
