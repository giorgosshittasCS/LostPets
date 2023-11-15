package com.example.lostpets.Classes;

public class User {

    public static String user;

    private String username;
    private String phone_number;
    private String password;

    public User(String username,String phone_number,String password){
        this.username = username;
        this.phone_number = phone_number;
        this.password = password;
    }
    public User(){}

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
