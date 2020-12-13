package com.book.dddstart.shop.chapter6.value;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinRequest {

    private String id;
    private String name;
    private String password;
    private String confirmPassword;
}
