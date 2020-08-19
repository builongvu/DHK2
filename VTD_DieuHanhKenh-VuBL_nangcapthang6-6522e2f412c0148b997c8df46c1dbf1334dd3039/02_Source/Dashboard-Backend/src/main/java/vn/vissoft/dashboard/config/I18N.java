package vn.vissoft.dashboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18N {
    private static ResourceBundleMessageSource messageSource;

    @Autowired
    I18N(ResourceBundleMessageSource messageSource) {
        I18N.messageSource = messageSource;
    }

    public static String get(String msgCode,String... params) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, params, locale);
    }
}
