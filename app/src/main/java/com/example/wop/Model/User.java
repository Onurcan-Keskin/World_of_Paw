package com.example.wop.Model;

public class User {
    private String Name;
    private String Password;
    private String Email;

    public User(){}

    public User(String name, String pass, String mail){
        this.Name=name;
        this.Password=pass;
        this.Email=mail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
