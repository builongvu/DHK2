package vn.vissoft.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver
        implements WebMvcConfigurer {

    List<Locale> LOCALES = Arrays.asList(
            new Locale("vi"),
            new Locale("en"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        if(headerLang == null || headerLang.isEmpty())
        {
            headerLang = "vi";
        }
        return Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
    }


}
