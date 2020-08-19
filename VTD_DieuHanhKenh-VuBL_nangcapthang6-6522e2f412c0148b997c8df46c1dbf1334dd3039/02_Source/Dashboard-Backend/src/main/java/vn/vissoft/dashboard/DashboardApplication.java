package vn.vissoft.dashboard;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import vn.vissoft.dashboard.config.CustomLocaleResolver;
import vn.vissoft.dashboard.config.CustomPasswordEncoder;
import vn.vissoft.dashboard.config.CustomerStringEncryptor;
import vn.vissoft.dashboard.dto.StoreProcedureScheduleDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.services.StoreScheduleService;

import javax.transaction.Transactional;


@SpringBootApplication
@EnableEncryptableProperties
@EnableJpaRepositories
public class DashboardApplication  {

    @Value("${jasypt.encryptor.password}")
    private String passwordSecretKey;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DashboardApplication.class, args);
        LogManager.getLogger(DashboardApplication.class).info("Test log4j");
    }

    @Bean
    public LocaleResolver localeResolver() {
        CustomLocaleResolver localeResolver = new CustomLocaleResolver();
        LocaleContextHolder.setDefaultLocale(new Locale("vi"));
        localeResolver.setDefaultLocale(new Locale("vi"));
        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new CustomPasswordEncoder(passwordSecretKey);
    }

    @Bean
    public StringEncryptor jasyptStringEncryptor() {
        return new CustomerStringEncryptor(passwordSecretKey);
    }




}

