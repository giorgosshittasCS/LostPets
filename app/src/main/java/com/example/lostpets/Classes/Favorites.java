package com.example.lostpets.Classes;

public class Favorites {

    private String id;
    private String userId;
    private String recordId;


    public Favorites(){

    }

    public Favorites(String userId,String recordId){
        this.userId = userId;
        this.recordId = recordId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
