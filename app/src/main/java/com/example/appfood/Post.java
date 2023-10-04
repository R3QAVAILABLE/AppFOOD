package com.example.appfood;

public class Post {
    private String postId;
    private String author;
    private String imageUrl;
    private String description;

    public Post(String postId, String author, String imageUrl, String description) {
        this.postId = postId;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
