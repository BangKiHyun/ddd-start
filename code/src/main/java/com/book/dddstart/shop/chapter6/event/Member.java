package com.book.dddstart.shop.chapter6.event;

public class Member {
    private Password password;

    public void initialzePassword() {
        String newPassword = generateRandeomPassword();
        this.password = new Password(newPassword);
        Events.raise(new PasswordChangeEvent(this.id, password));
    }
}
