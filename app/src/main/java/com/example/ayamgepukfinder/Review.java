package com.example.ayamgepukfinder;

public class Review {
    private String reviewId;
    private String userId;
    private String userName;
    private String userEmail;
    private String restaurantId;
    private String restaurantName;
    private float rating;
    private String comment;
    private String photoData;
    private String timestamp;
    private String date;
    private int helpfulCount;
    private Reply reply;

    public Review() {
    }

    public Review(String reviewId, String userId, String userName, String userEmail,
                  String restaurantId, String restaurantName, float rating,
                  String comment, String photoData, String timestamp, String date) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.rating = rating;
        this.comment = comment;
        this.photoData = photoData;
        this.timestamp = timestamp;
        this.date = date;
        this.helpfulCount = 0;
        this.reply = null;
    }

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getPhotoData() { return photoData; }
    public void setPhotoData(String photoData) { this.photoData = photoData; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }

    public Reply getReply() { return reply; }
    public void setReply(Reply reply) { this.reply = reply; }

    public static class Reply {
        private String adminName;
        private String message;
        private String timestamp;

        public Reply() {
        }

        public Reply(String adminName, String message, String timestamp) {
            this.adminName = adminName;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}