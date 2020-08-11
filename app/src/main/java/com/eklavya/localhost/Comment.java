package com.eklavya.localhost;

public class Comment {
    private String id;
    private String from;
    private String message;

    public Comment() {
    }

    public Comment(String id, String from, String message) {
        this.id = id;
        this.from = from;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
