package com.luxottica.testautomation.utils;

import com.luxottica.testautomation.models.User;
import com.microsoft.playwright.Playwright;

public class Context {

    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<User> user = new ThreadLocal<>();

    public static Playwright getPlaywright() {
        return playwright.get();
    }

    public static void setPlaywright(Playwright playwright) {
        Context.playwright.set(playwright);
    }

    public static User getUser() {
        return user.get();
    }

    public static void setUser(User user) {
        Context.user.set(user);
    }
}
