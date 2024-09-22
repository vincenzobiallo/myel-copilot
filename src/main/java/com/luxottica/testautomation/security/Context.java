package com.luxottica.testautomation.security;

import com.luxottica.testautomation.components.report.models.TestCase;
import com.luxottica.testautomation.exceptions.ContextMissingValueException;
import com.luxottica.testautomation.models.User;
import com.microsoft.playwright.Playwright;

public class Context {

    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<User> user = new ThreadLocal<>();
    private static final ThreadLocal<TestCase> test = new ThreadLocal<>();

    public static Playwright getPlaywright() {

        if (playwright.get() == null) {
            throw new ContextMissingValueException("Playwright is not set");
        }

        return playwright.get();
    }

    public static void setPlaywright(Playwright playwright) {
        Context.playwright.set(playwright);
    }

    public static User getUser() {

        if (user.get() == null) {
            throw new ContextMissingValueException("User is not set");
        }

        return user.get();
    }

    public static void setUser(User user) {
        Context.user.set(user);
    }

    public static TestCase getTest() {

        if (test.get() == null) {
            throw new ContextMissingValueException("Test is not set");
        }

        return test.get();
    }

    public static void setTest(TestCase test) {
        Context.test.set(test);
    }
}
