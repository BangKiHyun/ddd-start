package com.book.dddstart.shop.chapter6.value;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class JoinRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return JoinRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object request, Errors errors) {
        // v1
        ValidationUtils.rejectIfEmpty(errors, "id", "id.empty");
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty");
        ValidationUtils.rejectIfEmpty(errors, "password", "password.empty");

        // v2
        final JoinRequest joinRequest = (JoinRequest) request;
        checkEmpty(joinRequest.getId(), "id");
        checkEmpty(joinRequest.getName(), "name");
        checkEmpty(joinRequest.getPassword(), "password");
    }

    private void checkEmpty(String value, String propertyName) {
        if(value == null || value.isEmpty())
            throw new EmptyPropertyException(propertyName);
    }
}
