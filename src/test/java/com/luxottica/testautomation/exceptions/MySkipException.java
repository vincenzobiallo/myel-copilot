package com.luxottica.testautomation.exceptions;

import org.testng.SkipException;

public class MySkipException extends SkipException {

    private final boolean makeScreenshot;

    public MySkipException(String message, boolean makeScreenshot) {
        super(message);
        this.makeScreenshot = makeScreenshot;
    }

    public MySkipException(String message) {
        super(message);
        this.makeScreenshot = false;
    }

    public boolean makeScreenshot() {
        return makeScreenshot;
    }

}
