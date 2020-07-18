package com.hex.connect.Modal;

public class User {
    private String username;
    private String bio;
    private String birthday;
    private String id;
    private String imageurl;

    public User(String username, String bio, String birthday, String id, String imageurl) {
        this.username = username;
        this.bio = bio;
        this.birthday = birthday;
        this.id = id;
        this.imageurl = imageurl;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
