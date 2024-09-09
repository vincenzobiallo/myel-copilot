package com.luxottica.testautomation.constants;

/**
 * This class contains all the error messages that can be thrown by the application as notifications to the user.
 */
public class Errors {

    private static String getError(String errorKey) {
        // TODO: Get the error message from POEditor or lang file
        return errorKey;
    }

    public static final String COOKIE_BANNER_NOT_FOUND = getError("");
    public static final String ELEMENTS_NOT_VISIBLE = getError("One or more element(s) are not visible in page!");
    public static final String VIEW_ALL_NOT_VISIBLE = getError("'View All' button is not visible!");
    public static final String PRICE_POLICY_NOT_FOUND = getError("Price policy not found!");
    public static final String WRONG_DATA_IN_MENU = getError("Columns in sub-menu are not complete!");
    public static final String ELEMENTS_DISABLED = getError("Element is disabled!");
    public static final String CLASS_NOT_FOUND = getError("Specified class is not present!");

}
