package com.book.dddstart.shop.chapter8.lock.controller;

import com.book.dddstart.shop.chapter8.Order;
import com.book.dddstart.shop.chapter8.OrderRepository;
import com.book.dddstart.shop.chapter8.lock.LockException;
import com.book.dddstart.shop.chapter8.lock.LockId;
import com.book.dddstart.shop.chapter8.lock.LockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LockExamController {

    private LockManager lockManager;
    private OrderRepository orderRepository;

    @RequestMapping("/some/edit/{id}")
    public String editForm(@PathVariable("id") Long id, ModelMap model) throws LockException {
        // 1. 오프라인 선점 잠금 시도
        final LockId lockId = lockManager.tryLock("order", String.valueOf(id));

        // 2. 기능 실행
        Order order = orderRepository.findById(Long.valueOf(lockId.getValue()))
        model.addAttribute("data", order);

        // 3. 잠금 해제에 사용할 LockId를 모델에 추가
        model.addAttribute("lockId", lockId);


        return "editForm";
    }

    @RequestMapping(value = "/some/edit/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("editReq") EditRequest editRequest,
                       @RequestParam("lid") String lockIdValue) throws LockException {
        editRequest.setId(id);

        // 1. 잠금 섬점 확인
        final LockId lockId = new LockId(lockIdValue);
        lockManager.checkLock(lockId);

        // 2. 기능 실행
        someEditService.edit(editRequest);
        model.addAttribute("data", data);

        // 3. 잠금 해제
        lockManager.releaseLock(lockId);

        return "editSuccess";
    }
}
