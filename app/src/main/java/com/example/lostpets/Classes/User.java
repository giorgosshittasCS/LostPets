package com.example.lostpets.Classes;

public class User {

    private String username;
    private String phone_number;
    private String password;

    public User(String username,String phone_number,String password){
        this.username = username;
        this.phone_number = phone_number;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getPassword() {
        return password;
    }
}
