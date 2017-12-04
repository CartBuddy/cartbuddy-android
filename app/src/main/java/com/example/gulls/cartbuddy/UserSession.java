package com.example.gulls.cartbuddy;

/**
 * Created by ben on 12/3/2017.
 */

public class UserSession {
    private static UserSession userSession = new UserSession();
    private User user;

    public static UserSession getSession() {
        return userSession;
    }

    public void setActiveUser(User user) {
        this.user = user;
    }
    public User getActiveUser() {
        return user;
    }
}
