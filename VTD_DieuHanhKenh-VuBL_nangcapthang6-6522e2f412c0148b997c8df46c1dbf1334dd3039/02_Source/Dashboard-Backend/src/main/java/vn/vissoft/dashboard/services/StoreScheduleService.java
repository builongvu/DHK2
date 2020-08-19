package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StoreProcedureScheduleDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface StoreScheduleService {
      void callStoreProcedure( LocalDateTime localDateTime) throws Exception;
      List<StoreProcedureScheduleDTO> setData(LocalDateTime localDateTime) throws Exception;
      int updateStoreSchedule(Integer storeId, Integer excutionTime, String flag, Timestamp executionDate) throws Exception;
      StoreProcedureScheduleDTO setDataByCommand(String command) throws Exception;
}
