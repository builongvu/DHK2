package vn.vissoft.dashboard.repo;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface StoreScheduleRepo {
    List<Object[]> getExecuteStoreProcedure(LocalDateTime localDateTime) throws Exception;
    int updateStoreSchedule(Integer storeId, Integer excutionTime, String flag, Timestamp executionDate) throws Exception;
    List<Object[]> getStoreScheduleByCommand(String command) throws Exception;
}
