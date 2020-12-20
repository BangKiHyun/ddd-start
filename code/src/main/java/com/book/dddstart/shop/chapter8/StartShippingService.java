package com.book.dddstart.shop.chapter8;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StartShippingService {

    private OrderRepository orderRepository;

    @Transactional
    public void startShipping(StartShippingRequest req){
        Order order = orderRepository.findById(req.getOrderNumber())
                .orElseThrow(() -> new NoSuchElementException());

        if(!order.matchVersion(req.getVersion())){
            throw new VersionConflictException();
        }
        order.startShipping();
    }
}
