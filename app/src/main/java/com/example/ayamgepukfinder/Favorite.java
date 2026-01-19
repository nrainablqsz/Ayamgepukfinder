package com.example.ayamgepukfinder;

public class Favorite {
    private int restaurantId;
    private String name;
    private String rating;
    private String location;
    private String hours;
    private long timestamp;

    public Favorite() {
    }

    public Favorite(int restaurantId, String name, String rating,
                    String location, String hours, long timestamp) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.rating = rating;
        this.location = location;
        this.hours = hours;
        this.timestamp = timestamp;
    }

    // GETTERS AND SETTERS
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}