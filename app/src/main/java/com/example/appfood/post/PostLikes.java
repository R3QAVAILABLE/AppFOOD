package com.example.appfood.post;

public class PostLikes {
    private String likeid;
    private String userid;

    public PostLikes(String likeid, String userid) {
        this.likeid = likeid;
        this.userid = userid;
    }

    public String getLikeid() {
        return likeid;
    }

    public void setLikeid(String likeid) {
        this.likeid = likeid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
