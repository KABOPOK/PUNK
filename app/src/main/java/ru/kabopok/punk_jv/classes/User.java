package ru.kabopok.punk_jv.classes;

public class User {
    private String photoUserUrl;
    private String photoUserCloudPath;
    private String name;
    private String gender;
    private String number;
    private String password;
    public User(){
        name =null;
        gender =null;
        number =null;
        password =null;
        photoUserUrl =null;
        photoUserCloudPath = null;
    }

    public String getPhotoUserCloudPath() {
        return photoUserCloudPath;
    }

    public void setPhotoUserCloudPath(String photoUserCloudPath) {
        this.photoUserCloudPath = photoUserCloudPath;
    }

    public User(String name, String gender, String number, String password, String photoUserUrl) {
        this.name = name;
        this.gender = gender;
        this.number = number;
        this.password = password;
        this.photoUserUrl = photoUserUrl;
    }

    public String getPassword() {
        return password;
    }
    public String getPhotoUserUrl() { return photoUserUrl; }

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

    public void setPhotoUserUrl(String photoUserUrl) {this.photoUserUrl = photoUserUrl;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
