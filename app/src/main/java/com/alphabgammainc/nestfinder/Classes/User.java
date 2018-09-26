package com.alphabgammainc.nestfinder.Classes;

import java.util.ArrayList;

/**
 * Created by davidhuang on 2017-06-13.
 */

public class User {

    private String email;
    private String userId;
    private ArrayList<String> listings;
    private ArrayList<String> bookmarks;

    public User(){

    }

    public User(String email, String userId, ArrayList<String> listings, ArrayList<String> bookmarks){
        this.email = email;
        this.userId = userId;
        this.listings = listings;
        this.bookmarks = bookmarks;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getListings() {
        return listings;
    }

    public void setListings(ArrayList<String> listings) {
        this.listings = listings;
    }

    public ArrayList<String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(ArrayList<String> bookmarks) {
        this.bookmarks = bookmarks;
    }
}
