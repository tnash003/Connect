package com.hex.connect.Modal;

public class Links {
    private String insta;
    private String snapchat;
    private String youtube;
    private String other;

    public Links(String insta, String snapchat, String youtube, String other) {
        this.insta = insta;
        this.snapchat = snapchat;
        this.youtube = youtube;
        this.other = other;
    }

    public Links() {
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(String snapchat) {
        this.snapchat = snapchat;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
