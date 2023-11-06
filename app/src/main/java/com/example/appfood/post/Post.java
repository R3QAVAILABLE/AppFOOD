package com.example.appfood.post;

public class Post implements Comparable<Post> {
    private String postId;
    private String authorId;
    private String imageUrl;
    private String name;
    private String ingredients;
    private String description;
    private String date;
    private int likes;
    private int comments;

    public Post(String postId, String authorId, String imageUrl, String name, String ingredients, String description, String date, int likes, int comments) {
        this.postId = postId;
        this.authorId = authorId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.ingredients = ingredients;
        this.description = description;
        this.date = date;
        this.likes = likes;
        this.comments = comments;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    @Override
    public int compareTo(Post o) {
        return o.getDate().compareTo(getDate());
    }
}



