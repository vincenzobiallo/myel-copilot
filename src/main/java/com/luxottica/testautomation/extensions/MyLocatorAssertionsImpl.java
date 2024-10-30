package com.luxottica.testautomation.extensions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.impl.LocatorAssertionsImpl;
import com.microsoft.playwright.options.AriaRole;
import org.apache.logging.log4j.util.Strings;
import org.opentest4j.AssertionFailedError;

import java.util.regex.Pattern;

public class MyLocatorAssertionsImpl extends LocatorAssertionsImpl implements MyLocatorAssertions {

    public MyLocatorAssertionsImpl(Locator locator) {
        super(locator);
    }

    private void handleAssertionMessage(Runnable assertion, String message) {
        try {
            assertion.run();
        } catch (AssertionFailedError e) {
            if (!Strings.isBlank(message)) {
                throw new AssertionError(message, e);
            } else {
                throw e;
            }
        }
    }

    public void containsText(String text, ContainsTextOptions options, String message) {
        handleAssertionMessage(() -> super.containsText(text, options), message);
    }

    public void containsText(Pattern pattern, ContainsTextOptions options, String message) {
        handleAssertionMessage(() -> super.containsText(pattern, options), message);
    }

    public void containsText(String[] strings, ContainsTextOptions options, String message) {
        handleAssertionMessage(() -> super.containsText(strings, options), message);
    }

    public void containsText(Pattern[] patterns, ContainsTextOptions options, String message) {
        handleAssertionMessage(() -> super.containsText(patterns, options), message);
    }

    public void hasAccessibleDescription(String description, HasAccessibleDescriptionOptions options, String message) {
        handleAssertionMessage(() -> super.hasAccessibleDescription(description, options), message);
    }

    public void hasAccessibleDescription(Pattern pattern, HasAccessibleDescriptionOptions options, String message) {
        handleAssertionMessage(() -> super.hasAccessibleDescription(pattern, options), message);
    }

    public void hasAccessibleName(String name, HasAccessibleNameOptions option, String message) {
        handleAssertionMessage(() -> super.hasAccessibleName(name, option), message);
    }

    public void hasAccessibleName(Pattern pattern, HasAccessibleNameOptions options, String message) {
        handleAssertionMessage(() -> super.hasAccessibleName(pattern, options), message);
    }

    public void hasAttribute(String name, String text, HasAttributeOptions options, String message) {
        handleAssertionMessage(() -> super.hasAttribute(name, text, options), message);
    }

    public void hasAttribute(String name, Pattern pattern, HasAttributeOptions options, String message) {
        handleAssertionMessage(() -> super.hasAttribute(name, pattern, options), message);
    }

    public void hasClass(String text, HasClassOptions options, String message) {
        handleAssertionMessage(() -> super.hasClass(text, options), message);    
    }

    public void hasClass(Pattern pattern, HasClassOptions options, String message) {
        handleAssertionMessage(() -> super.hasClass(pattern, options), message);    
    }

    public void hasClass(String[] strings, HasClassOptions option, String message) {
        handleAssertionMessage(() -> super.hasClass(strings, option), message);   
    }

    public void hasClass(Pattern[] patterns, HasClassOptions options, String message) {
        handleAssertionMessage(() -> super.hasClass(patterns, options), message);        
    }

    public void hasCount(int count, HasCountOptions options, String message) {
        handleAssertionMessage(() -> super.hasCount(count, options), message);
    }

    public void hasCSS(String name, String value, HasCSSOptions options, String message) {
        handleAssertionMessage(() -> super.hasCSS(name, value, options), message);
    }

    public void hasCSS(String name, Pattern pattern, HasCSSOptions options, String message) {
        handleAssertionMessage(() -> super.hasCSS(name, pattern, options), message);
    }

    public void hasId(String id, HasIdOptions options, String message) {
        handleAssertionMessage(() -> super.hasId(id, options), message);
    }

    public void hasId(Pattern pattern, HasIdOptions options, String message) {
        handleAssertionMessage(() -> super.hasId(pattern, options), message);    
    }

    public void hasJSProperty(String name, Object value, HasJSPropertyOptions options, String message) {
        handleAssertionMessage(() -> super.hasJSProperty(name, value, options), message);
    }

    public void hasRole(AriaRole role, HasRoleOptions options, String message) {
        handleAssertionMessage(() -> super.hasRole(role, options), message);
    }

    public void hasText(String text, HasTextOptions options, String message) {
        handleAssertionMessage(() -> super.hasText(text, options), message);
    }

    public void hasText(Pattern pattern, HasTextOptions options, String message) {
        handleAssertionMessage(() -> super.hasText(pattern, options), message);
    }

    public void hasText(String[] strings, HasTextOptions options, String message) {
        handleAssertionMessage(() -> super.hasText(strings, options), message);
    }

    public void hasText(Pattern[] patterns, HasTextOptions options, String message) {
        handleAssertionMessage(() -> super.hasText(patterns, options), message);
    }

    public void hasValue(String value, HasValueOptions options, String message) {
        handleAssertionMessage(() -> super.hasValue(value, options), message);
    }

    public void hasValue(Pattern pattern, HasValueOptions options, String message) {
        handleAssertionMessage(() -> super.hasValue(pattern, options), message);
    }

    public void hasValues(String[] values, HasValuesOptions options, String message) {
        handleAssertionMessage(() -> super.hasValues(values, options), message);
    }

    public void hasValues(Pattern[] patterns, HasValuesOptions options, String message) {
        handleAssertionMessage(() -> super.hasValues(patterns, options), message);
    }

    public void isChecked(IsCheckedOptions options, String message) {
        handleAssertionMessage(() -> super.isChecked(options), message);
    }

    public void isDisabled(IsDisabledOptions options, String message) {
        handleAssertionMessage(() -> super.isDisabled(options), message);
    }

    public void isEditable(IsEditableOptions options, String message) {
        handleAssertionMessage(() -> super.isEditable(options), message);
    }

    public void isEmpty(IsEmptyOptions options, String message) {
        handleAssertionMessage(() -> super.isEmpty(options), message);
    }

    public void isEnabled(IsEnabledOptions options, String message) {
        handleAssertionMessage(() -> super.isEnabled(options), message);
    }

    public void isFocused(IsFocusedOptions options, String message) {
        handleAssertionMessage(() -> super.isFocused(options), message);
    }

    public void isHidden(IsHiddenOptions options, String message) {
        handleAssertionMessage(() -> super.isHidden(options), message);
    }

    public void isInViewport(IsInViewportOptions options, String message) {
        handleAssertionMessage(() -> super.isInViewport(options), message);
    }

    public void isVisible(IsVisibleOptions options, String message) {
        handleAssertionMessage(() -> super.isVisible(options), message);
    }

    public void isAttached(IsAttachedOptions options, String message) {
        handleAssertionMessage(() -> super.isAttached(options), message);
    }

}


