package com.eklavya.localhost;

public class Host {
    private String id;
    private String from;
    private String title;
    private String imgUri;
    private String details;
    private String category;
    private String link;
    private String contact;
    private String latitude;
    private String longitude;
    private int great;
    private int good;
    private int ok;

    public Host() {

    }

    public Host(String id, String from, String title, String imgUri, String details,
                String category, String link, String contact, String latitude, String longitude,
                int great, int good, int ok) {
        this.id = id;
        this.from = from;
        this.title = title;
        this.imgUri = imgUri;
        this.details = details;
        this.category = category;
        this.link = link;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.great = great;
        this.good = good;
        this.ok = ok;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getGreat() {
        return great;
    }

    public void setGreat(int great) {
        this.great = great;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }
}
