package com.example.track;

public class CreateUser {

    private String name;
    private String lat;
    private String lng;

    public CreateUser(){

    }

    public String getpolicename(){
        return name;
    }

    public void setpolicename(String name)
    {
        this.name= name;
    }

    public String getLat1()
    {
        return lat;
    }
    public void setpoliceLat(String lat)
    {
        this.lat= lat;
    }
    public String getLng1()
    {
        return lng;
    }
    public void setPoliceLng(String lng)
    {
        this.lng= lng;
    }
}

