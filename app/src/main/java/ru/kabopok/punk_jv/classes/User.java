package ru.kabopok.punk_jv.classes;

public class User {
    private String name;
    private String gender;
    private String number;
    private String password;
    public User(){
        name =null;
        gender =null;
        number =null;
        password =null;
    }
    public User(String name, String gender, String number, String password) {
        this.name = name;
        this.gender = gender;
        this.number = number;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
