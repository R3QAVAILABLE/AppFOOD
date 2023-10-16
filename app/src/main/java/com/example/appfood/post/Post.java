package com.example.appfood.post;

public class Post {
    private String postId;
    private String author;
    private String authorimageUrl;
    private String imageUrl;
    private String description;



    public Post(String postId, String author, String imageUrl, String description, String authorimageUrl) {
        this.postId = postId;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
        this.authorimageUrl = authorimageUrl;
    }
    public String getAuthorimageUrl() { return authorimageUrl; }

    public void setAuthorimageUrl(String authorimageUrl) { this.authorimageUrl = authorimageUrl; }

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
