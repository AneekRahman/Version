package com.version.app.Classes;

public class RegularPost {
    private String postUserName, postContentUrl, postUserDpUrl, postDescText;
    private int postType, postUserId;
    private long postViewsCount, postHeartCount, postCommentCount, postBroadcastCount;

    public RegularPost(int postType, String postContentUrl , int postUserId, String postUserName, String postUserDpUrl, String postDescText, long postViewsCount, long postHeartCount, long postCommentCount, long postBroadcastCount) {

        this.postType = postType;
        this.postContentUrl = postContentUrl;
        this.postUserId = postUserId;
        this.postUserName = postUserName;
        this.postUserDpUrl = postUserDpUrl;
        this.postDescText = postDescText;
        this.postViewsCount = postViewsCount;
        this.postHeartCount = postHeartCount;
        this.postCommentCount = postCommentCount;
        this.postBroadcastCount = postBroadcastCount;

    }

    public int getPostType() {
        return postType;
    }

    public int getPostUserId() {
        return postUserId;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public String getPostUserDpUrl() {
        return postUserDpUrl;
    }

    public String getPostContentUrl() {
        return postContentUrl;
    }

    public String getPostDescText() {
        return postDescText;
    }

    public long getPostViewsCount() {
        return postViewsCount;
    }

    public long getPostHeartCount() {
        return postHeartCount;
    }

    public long getPostCommentCount() {
        return postCommentCount;
    }

    public long getPostBroadcastCount() {
        return postBroadcastCount;
    }
}