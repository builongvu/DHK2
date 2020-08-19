package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StoreProcedureScheduleDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.StoreScheduleRepo;
import vn.vissoft.dashboard.services.StoreScheduleService;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreScheduleServiceImpl implements StoreScheduleService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private StoreScheduleRepo storeScheduleRepo;

    @Override
    public void callStoreProcedure(LocalDateTime localDateTime) throws Exception {
        List<StoreProcedureScheduleDTO> vlstStores = setData(localDateTime);
        this.jdbcTemplate.update(vlstStores.get(0).getSqlCommand());
    }

    public List<StoreProcedureScheduleDTO> setData(LocalDateTime localDateTime) throws Exception {
        List<StoreProcedureScheduleDTO> vlstStores = new ArrayList<>();
        List<Object[]> vlstObjects = storeScheduleRepo.getExecuteStoreProcedure(localDateTime);
        if (!DataUtil.isNullOrEmpty(vlstObjects)) {
            for (Object[] object : vlstObjects) {
                StoreProcedureScheduleDTO storeProcedureScheduleDTO = new StoreProcedureScheduleDTO();
                storeProcedureScheduleDTO.setParameter((String) object[0]);
                storeProcedureScheduleDTO.setSqlCommand((String) object[1]);
                storeProcedureScheduleDTO.setLogDir((String) object[2]);
                storeProcedureScheduleDTO.setDelayTime((Long) object[3]);
                storeProcedureScheduleDTO.setThreadName((String) object[4]);
                storeProcedureScheduleDTO.setStartUpType((String) object[5]);
                storeProcedureScheduleDTO.setScheduleType((String) object[6]);
                storeProcedureScheduleDTO.setExecutionTime((Integer) object[7]);
                storeProcedureScheduleDTO.setStartTime((Time) object[8]);
                storeProcedureScheduleDTO.setEndTime((Time) object[9]);
                storeProcedureScheduleDTO.setExpectedDate((Date) object[10]);
                storeProcedureScheduleDTO.setWeekDay((String) object[11]);
                storeProcedureScheduleDTO.setMonthDay((String) object[12]);
                storeProcedureScheduleDTO.setExecutionCount((Integer) object[13]);
                storeProcedureScheduleDTO.setFlag((String)(object[14]));

                vlstStores.add(storeProcedureScheduleDTO);
            }

        }
        return vlstStores;
    }

    @Override
    public int updateStoreSchedule(Integer storeId, Integer excutionTime, String flag, Timestamp executionDate) throws Exception {
        return storeScheduleRepo.updateStoreSchedule(storeId, excutionTime,flag,executionDate);
    }

    @Override
    public StoreProcedureScheduleDTO setDataByCommand(String command) throws Exception {
        StoreProcedureScheduleDTO store = null;
        List<Object[]> vlstObjects = storeScheduleRepo.getStoreScheduleByCommand(command);
        if (!DataUtil.isNullOrEmpty(vlstObjects)) {
            for (Object[] object : vlstObjects) {
                StoreProcedureScheduleDTO storeProcedureScheduleDTO = new StoreProcedureScheduleDTO();
                storeProcedureScheduleDTO.setShceduleId((Integer) object[0]);
                storeProcedureScheduleDTO.setExecutionCount((Integer) object[1]);
                storeProcedureScheduleDTO.setExecutionDate((Timestamp)object[2]);
                storeProcedureScheduleDTO.setExecutionTime((Integer)object[3]);

                store=storeProcedureScheduleDTO;
            }

        }
        if (!DataUtil.isNullObject(store))
            return store;
        else return null;
    }
}
