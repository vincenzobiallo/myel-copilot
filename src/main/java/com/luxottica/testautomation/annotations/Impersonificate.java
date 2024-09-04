package com.luxottica.testautomation.annotations;

import com.luxottica.testautomation.models.MyelStore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Impersonificate {
    String door();
    MyelStore store();
}
