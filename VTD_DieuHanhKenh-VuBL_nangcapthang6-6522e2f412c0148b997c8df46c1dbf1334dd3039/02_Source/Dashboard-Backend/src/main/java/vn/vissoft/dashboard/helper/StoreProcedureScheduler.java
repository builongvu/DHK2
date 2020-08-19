//package vn.vissoft.dashboard.helper;
//
//import org.apache.commons.collections.map.MultiKeyMap;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.scheduling.TaskScheduler;
//
//import org.springframework.scheduling.annotation.*;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//
//import vn.vissoft.dashboard.dto.StoreProcedureScheduleDTO;
//import vn.vissoft.dashboard.services.StoreScheduleService;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.sql.*;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//
//@Configuration
//@EnableScheduling
//public class StoreProcedureScheduler {
//    private static final Logger LOGGER = LogManager.getLogger(StoreProcedureScheduler.class);
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private StoreScheduleService storeScheduleService;
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10);
//        return scheduler;
//    }
//
//    @Scheduled(fixedDelay = 5 * 60 * 1000)
//    public void schedule() {
//        LocalDateTime localDateTime = LocalDateTime.now();
//        List<StoreProcedureScheduleDTO> vlstStores = null;
//        try {
//            vlstStores = storeScheduleService.setData(localDateTime);
//            if (!DataUtil.isNullOrEmpty(vlstStores)) {
//                for (StoreProcedureScheduleDTO storeProcedureScheduleDTO : vlstStores) {
//                    switch (storeProcedureScheduleDTO.getScheduleType()) {
//                        case Constants.SCHEDULE_TYPE.DAILY:
//                            execute(storeProcedureScheduleDTO);
//                            break;
//                        case Constants.SCHEDULE_TYPE.WEEKLY:
//                            String weekDay = storeProcedureScheduleDTO.getWeekDay();
//                            String[] weekDays = weekDay.split(",");
//                            List<String> dayOfWeek = Arrays.asList(weekDays);
//                            List<String> trimmedDayOfWeek =
//                                    dayOfWeek.stream().map(day -> {
//                                        day = day.toUpperCase().trim();
//                                        return day;
//                                    }).collect(Collectors.toList());
//                            if (trimmedDayOfWeek.contains(localDateTime.getDayOfWeek().name().trim())) {
//                                execute(storeProcedureScheduleDTO);
//                            }
//                            break;
//                        case Constants.SCHEDULE_TYPE.MONTHLY:
//                            String monthDay = storeProcedureScheduleDTO.getMonthDay();
//                            String[] monthDays = monthDay.split(",");
//                            List<String> dayOfMonth = Arrays.asList(monthDays);
//                            List<String> trimmedDayOfMonth =
//                                    dayOfMonth.stream().map(String::trim).collect(Collectors.toList());
//                            if (trimmedDayOfMonth.contains(String.valueOf(localDateTime.getDayOfMonth()))) {
//                                execute(storeProcedureScheduleDTO);
//                            }
//                            break;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//    }
//
//    public void execute(StoreProcedureScheduleDTO storeProcedureScheduleDTO) throws Exception {
//        Timestamp current = new Timestamp(System.currentTimeMillis());
//        LOGGER.info("======================BAT DAU THUC THI====================");
//        LOGGER.info("BAT DAU LUC: " + new Date());
//        Connection connection = null;
//        Integer executionCount = null;
//        try {
//            connection = jdbcTemplate.getDataSource().getConnection();
//            CallableStatement callableSt = connection.prepareCall(storeProcedureScheduleDTO.getSqlCommand());
//            callableSt.registerOutParameter(1, Types.VARCHAR);
//            callableSt.executeUpdate();
//            if (!DataUtil.isNullOrEmpty(callableSt.getString(1))) {
//                LOGGER.info(callableSt.getString(1));
//            } else {
//                StoreProcedureScheduleDTO storeProcedureSchedule = storeScheduleService.setDataByCommand(storeProcedureScheduleDTO.getSqlCommand());
//                if (!DataUtil.isNullObject(storeProcedureScheduleDTO)) {
//                    if (storeProcedureSchedule.getExecutionDate().toLocalDateTime().toLocalDate().compareTo(LocalDate.now()) < 0) {
//                        executionCount = 1;
//                    } else {
//                        executionCount = DataUtil.safeToInt(storeProcedureSchedule.getExecutionCount()) + 1;
//                    }
//                    storeScheduleService.updateStoreSchedule(storeProcedureSchedule.getShceduleId(), executionCount, Constants.PARAM_STATUS, current);
//                    if (executionCount.equals(storeProcedureScheduleDTO.getExecutionTime())) {
//                        storeScheduleService.updateStoreSchedule(storeProcedureSchedule.getShceduleId(), executionCount, Constants.PARAM_STATUS_0, current);
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            LOGGER.error(e.getMessage(), e);
//        } finally {
//            if (connection != null)
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    LOGGER.error(e.getMessage(), e);
//                }
//        }
//
//        LOGGER.info("=======================KET THUC THUC THI===================");
//        LOGGER.info("KET THUC LUC: " + new Date());
//    }
//}
