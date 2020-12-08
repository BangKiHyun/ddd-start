package com.book.dddstart.shop.chapter3;

public class RegisterProductService {

    public ProductId registerNewProduct(NewProductRequest req){
        Stroe account = accountRepository.findStoreById(req.getStoredId());
        checkNull(account);

        // 중요 도메인 로직이 서비스 영역에 노출
        if(account.isBlocked()){
            throw new StoreBlockedException();
        }
        ProductId id = productRepository.nextId();
        Product product = new Product(id, account.getId(), ...);

        productRepository.save(product);
        return id;
    }
}
