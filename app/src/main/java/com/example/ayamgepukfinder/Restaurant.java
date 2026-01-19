package com.example.ayamgepukfinder;

public class Restaurant {
    private int id;
    private String name;
    private String description;
    private double rating;
    private String location;
    private String phone;
    private String hours;
    private double latitude;
    private double longitude;
    private String image;
    private String spicyLevel;
    private String priceRange;
    private String category;

    public Restaurant() {
    }

    // Constructor
    public Restaurant(int id, String name, String description, double rating,
                      String location, String phone, String hours,
                      double latitude, double longitude, String image,
                      String spicyLevel, String priceRange, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.location = location;
        this.phone = phone;
        this.hours = hours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.spicyLevel = spicyLevel;
        this.priceRange = priceRange;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHours() { return hours; }
    public void setHours(String hours) { this.hours = hours; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSpicyLevel() { return spicyLevel; }
    public void setSpicyLevel(String spicyLevel) { this.spicyLevel = spicyLevel; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}