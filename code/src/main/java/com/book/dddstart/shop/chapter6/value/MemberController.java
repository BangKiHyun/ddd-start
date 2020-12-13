package com.book.dddstart.shop.chapter6.value;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MemberController {

    @RequestMapping
    public String join(JoinRequest joinRequest, Errors errors) {
        try {
            joinService.join(joinRequest);
            return successView;
        } catch (EmptyPropertyException ex) {
            errors.rejectValue(ex.getPropertyName(), "empty");
            return formView;
        } catch (InvalidPropertyException ex) {
            errors.rejectValue(ex.getPropertyName(), "invalid");
            return formView;
        } catch (DuplicateIdException ex) {
            errors.rejectValue(ex.getPropertyName(), "duplicate");
            return formView;
        }
    }
}
