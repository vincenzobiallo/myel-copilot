package com.luxottica.testautomation.extensions;

import com.luxottica.testautomation.constants.Errors;
import com.microsoft.playwright.assertions.LocatorAssertions;

public interface MyLocatorAssertions extends LocatorAssertions {

    default void isVisible(String messageOnFailure) {
        this.isVisible(null, messageOnFailure);
    }
    void isVisible(IsVisibleOptions options, String messageOnFailure);

    @Override
    default void isVisible() {
        this.isVisible(null, Errors.ELEMENTS_NOT_VISIBLE);
    }

    default void isAttached(String messageOnFailure) {
        this.isAttached(null, messageOnFailure);
    }

    void isAttached(IsAttachedOptions options, String messageOnFailure);

    default void hasCount(int count, String messageOnFailure) {
        this.hasCount(count, null, messageOnFailure);
    }

    void hasCount(int count, HasCountOptions options, String messageOnFailure);

    default void isEnabled(String messageOnFailure) {
        this.isEnabled(null, messageOnFailure);
    }

    void isEnabled(IsEnabledOptions options, String messageOnFailure);

    @Override
    default void isEnabled() {
        this.isEnabled(null, Errors.ELEMENTS_DISABLED);
    }
}
