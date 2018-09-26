package com.alphabgammainc.nestfinder.User;

import com.alphabgammainc.nestfinder.Classes.User;

/**
 * Created by davidhuang on 2017-06-24.
 */

public class UserManager {
    private User user;
    private UserManager userManager;

    private UserManager(){

    }


    private UserManager getInstance(){
        if(userManager == null) userManager = new UserManager();

        return userManager;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
