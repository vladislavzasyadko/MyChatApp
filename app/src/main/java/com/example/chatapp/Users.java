package com.example.chatapp;


public class Users {

    String thumb_image;
    String status;
    String username;

    public Users(){

    }

    public Users(String username, String thumb_image, String status){
        this.username = username;
        this.status = status;
        this.thumb_image = thumb_image;
    }

    public void setName(String username){
        this.username = username;
    }

    public void setImage(String thumb_image){
        this.thumb_image = thumb_image;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getName(){
        return username;
    }

    public String getImage(){
        return thumb_image;
    }

    public String getStatus(){
        return status;
    }
}
