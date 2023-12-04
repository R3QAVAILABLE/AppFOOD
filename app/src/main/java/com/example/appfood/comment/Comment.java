package com.example.appfood.comment;

public class Comment implements Comparable<Comment> {


    String authorid;



    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommenttext() {
        return commenttext;
    }

    public void setCommenttext(String commenttext) {
        this.commenttext = commenttext;
    }

    public Comment(String authorid, String date, String commenttext) {

        this.authorid = authorid;
        this.date = date;
        this.commenttext = commenttext;
    }

    String date;
    String commenttext;

    @Override
    public int compareTo(Comment o) {
        return o.getDate().compareTo(getDate());
    }
}
