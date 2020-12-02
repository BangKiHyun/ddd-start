package com.book.dddstart.shop.chapter2.module.high;

import com.book.dddstart.shop.chapter1.domain.order.OrderLine;
import com.book.dddstart.shop.chapter1.domain.order.value.Money;

import java.util.Arrays;
import java.util.List;

public class CalculateDiscountService {
//    // 변경 전
//    private DroolsRuleEngine ruleEngine;
//
//    public CalculateDiscountService() {
//        this.ruleEngine = new DroolsRuleEngine();
//    }

    // 변경 후
    private RuleDiscounter ruleDiscounter;

    // injection
    public CalculateDiscountService(RuleDiscounter ruleDiscounter) {
        this.ruleDiscounter = ruleDiscounter;
    }

//    public Money calculateDiscount(List<OrderLine> orderLines, String customerId) {
//        Customer customer = findCustomer(customerId);
//        return ruleDiscounter.applyRules(customer, orderLines);
//    }
}
