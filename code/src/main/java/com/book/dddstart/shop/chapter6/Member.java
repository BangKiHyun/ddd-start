package com.book.dddstart.shop.chapter6;

public class Member {
    
    public void changePassword(String oldPw, String newPw){
        if(!matchPassword(oldPw)) throw new BadPasswordException();
        setPassword(newPw);
    }

    private boolean matchPassword(String pwd) {
        return passwordEncoder.matches(pwd);
    }

    private void setPassword(String newPw){
        if(isEmpty(newPw)) throw new IllegalArgumentException("no new password");
        this.password = newPw;
    }
}
