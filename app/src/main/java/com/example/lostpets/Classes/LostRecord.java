package com.example.lostpets.Classes;


import com.google.firebase.firestore.GeoPoint;

//class Location
//
//{
//    private double longitude;
//    private double latitude;
//
//    public Location(double longitude, double latitude)
//
//    {
//        this.longitude = longitude;
//        this.latitude = latitude;
//    }
//
//    public double getLongitude()
//
//    {
//        return longitude;
//    }
//
//    public void setLongitude(double longitude)
//
//    {
//        this.longitude = longitude;
//    }
//
//    public double getLatitude()
//
//    {
//        return latitude;
//    }
//
//    public void setLatitude(double latitude)
//
//    {
//        this.latitude = latitude;
//    }
//}
public class LostRecord {

    private String id;
    private String name;
    private String owner;
    private String age;
    private String breed;
    private String color;
    private String date;
    private String award;
    private String city;
    private String description;
    private String contact;
    private GeoPoint location;
    private String pic;

    public LostRecord(String owner,String age, String award, String breed, String color, String date, String description,
                      String name, GeoPoint location,String city,String contact,String pic) {
        this.age = age;
        this.award = award;
        this.breed = breed;
        this.color = color;
        this.date = date;
        this.description = description;
        this.name = name;
        this.location = location;
        this.city=city;
        this.owner=owner;
        this.contact=contact;
        this.pic = pic;
    }
    public LostRecord(){}
    public void setAge(String age) {
        this.age = age;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setLocation(GeoPoint location) {
        this.location = location;
    }
    public String getAge() {
        return age;
    }

    public String getAward() {
        return award;
    }

    public String getBreed() {
        return breed;
    }

    public String getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }
    public String getContact() {
        return contact;
    }


    public GeoPoint getLocation() {
        return location;
    }

    public String getPic(){
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
