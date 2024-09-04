package com.luxottica.testautomation.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class InjectionUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        InjectionUtil.ctx = ctx;
    }

    public static <T> T getBean(Class<T> beanInterface) {
        return ctx.getBean(beanInterface);
    }

}
