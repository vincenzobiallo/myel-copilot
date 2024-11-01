package com.luxottica.testautomation.authentication;

import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.exceptions.BackOfficeUserException;
import com.luxottica.testautomation.exceptions.UserException;
import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.utils.InjectionUtil;
import com.microsoft.playwright.Browser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.luxottica.testautomation.authentication.PlaywrightFactory.areCookiesPresent;

public class UserUtils {

    private static final List<User> users = new LinkedList<>();
    private static final List<User> backOfficeUsers = new LinkedList<>();

    private static final String FILE_PLACEHOLDER = "playwright/.auth/users.csv";

    static {
        File file = new File(FILE_PLACEHOLDER);

        // user or subusers have following pattern <name>.<number>.<store slug>
        Pattern pattern = Pattern.compile("[A-Za-z0-9]+\\.[0-9]+\\.[a-z]+");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\|");

                    String username = values[0];
                    String password = values[1];

                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);

                    if (pattern.matcher(username).matches()) {
                        user.setBackOfficeUser(false);
                        users.add(user);
                    } else {
                        user.setBackOfficeUser(true);
                        backOfficeUsers.add(user);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("users.csv file not found!");
        }
    }

    public static User getUserByUsername(Browser browser, String username) {
        try {
            Config config = InjectionUtil.getBean(Config.class);
            User user = users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElseThrow();
            if (!areCookiesPresent(user.getUsername())) {
                user.executeLogin(browser, config.getBaseUrl());
            }
            return user;
        } catch (NoSuchElementException e) {
            throw new UserException("User " + username + " not found in users list!");
        }
    }

    public static User getUserByWorker(Browser browser) {
        int workerId = Optional.ofNullable(getWorkerId()).orElse(1);
        try {
            Config config = InjectionUtil.getBean(Config.class);
            User user = backOfficeUsers.get(workerId - 1);
            if (!areCookiesPresent(user.getUsername())) {
                user.executeLogin(browser, config.getBaseUrl());
            }
            return user;
        } catch (IndexOutOfBoundsException e) {
            throw new BackOfficeUserException("Workers number is major than the number of users! Size of backOfficeUsers: " + backOfficeUsers.size() + " - Worker: " + workerId);
        }
    }

    private static Integer getWorkerId() {
        String threadName = Thread.currentThread().getName();
        Pattern pattern = Pattern.compile(".*-Worker-([0-9]+)");
        Matcher matcher = pattern.matcher(threadName);

        return matcher.find() ? Integer.parseInt(matcher.group(1)) : null;
    }
}
