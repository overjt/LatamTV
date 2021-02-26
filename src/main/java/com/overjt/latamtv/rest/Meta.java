package com.overjt.latamtv.rest;


public class Meta {
    private String id;
    private String type;
    private String name;
    private String poster;
    private String description;

    public void setID(String id){
        this.id = id;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setName(String name){
        this.name = name;
    }

    public void setPoster(String poster){
        this.poster = poster;
    }

    public void setDescription(String poster){
        this.poster = poster;
    }

    public String getID(){
        return this.id;
    }
    public String getType(){
        return this.type;
    }
    public String getName(){
        return this.name;
    }

    public String getPoster(){
        return this.poster;
    }

    public String getDescription(){
        return this.description;
    }

    
}
