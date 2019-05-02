package com.example.chatapp;


public class Users {

    String image;
    String status;
    String username;

    public Users(){

    }

    public Users(String username, String image, String status){
        this.username = username;
        this.status = status;
        this.image = image;
    }

    public void setName(String username){
        this.username = username;
    }

    public void setImage(String image){
        this.image = image;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getName(){
        return username;
    }

    public String getImage(){
        return image;
    }

    public String getStatus(){
        return status;
    }
}
