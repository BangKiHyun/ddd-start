package com.book.dddstart.shop.chapter6.application;

import com.book.dddstart.shop.chapter6.parameter.ChangePasswordRequest;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

public class SimpleExam {

    @RequestMapping(method = RequestMethod.POST)
    public String changePassword(HttpServletRequest request, Errors errors) {
        // 사용자 요청을 응용 서비스가 요구하는 형식으로 변환
        final String curPw = request.getParameter("curPw");
        final String newPw = request.getParameter("newPw");

        ChangePasswordRequest chPwdReq = new ChangePasswordRequest(curPw, newPw);
        try {
            //응용 서비스 실행
            changePasswordService.changePassword(chPwdReq);
            return successView;
        } catch (BadPssswordException | NoMemberException ex) {
            // 응용 서비스의 처리 결과를 알맞은 응답으로 변환
            errors.reject("idPasswordNotMatch");
            return formView;
        }
    }
}
