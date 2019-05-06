package com.example.chatapp;

public class Messages {
    private String message, type, from;
    private long time;
    private boolean seen;

    public Messages(){

    }

    public Messages(String message, boolean seen, long time, String type, String from){

        this.message = message;
        this.from = from;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getMessage() { return message;}

    public void setMessage(String message){ this.message = message;}

    public String getFrom() { return from;}

    public void setFrom(String from){ this.from = from;}

    public boolean getSeen() { return seen;}

    public void setSeen(boolean seen){ this.seen = seen;}

    public long getTime(){return time;}

    public void setTime(long time){this.time = time;}

    public String getType(){return type;}

    public void setType(String type){this.type = type;}
}
