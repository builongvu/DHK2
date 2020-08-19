package vn.vissoft.dashboard.repo.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vissoft.dashboard.repo.StoreScheduleRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class StoreScheduleRepoImpl implements StoreScheduleRepo {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Object[]> getExecuteStoreProcedure(LocalDateTime localDateTime) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Date curDate = Date.valueOf(localDateTime.toLocalDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" HH:mm:ss");
        String formatDateTime = localDateTime.format(formatter);
        stringBuilder.append("select parameter,sql_command,log_dir,delay_time,thread_name,startup_type,schedule_type,execution_time,start_time,end_time")
                .append(",expected_date,week_day,month_day,execution_count,flag ");
        stringBuilder.append(" from store_procedure_schedule ");
        stringBuilder.append(" where 1=1 and status = 1 and start_time <= :startTime and (end_time >= :startTime or null is null) and expected_date <= :currentDate " +
                "and (date(execution_date) < current_date() or (ifnull(execution_count,0) < execution_time and date(execution_date) = current_date()))" +
                " and ((flag =1 and flag_type =1) or flag_type =0) order by flag_type, start_time");
        Query query = entityManager.createNativeQuery(stringBuilder.toString());
        query.setParameter("startTime", formatDateTime);
        query.setParameter("currentDate", curDate);

        return query.getResultList();
    }

    @Transactional
    @Override
    public int updateStoreSchedule(Integer storeId, Integer excutionTime, String flag, Timestamp executionDate) throws Exception {
        int count;
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("update store_procedure_schedule sps ");
        vsbdSqlBuilder.append(" set sps.execution_count =:executionTime, sps.flag =:flag, sps.execution_date =:executionDate");
        vsbdSqlBuilder.append(" where sps.schedule_id =:storeId");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("executionTime", excutionTime);
        query.setParameter("storeId", storeId);
        query.setParameter("flag", flag);
        query.setParameter("executionDate", executionDate);
        count = query.executeUpdate();
        return count;
    }

    @Override
    public List<Object[]> getStoreScheduleByCommand(String command) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select schedule_id,execution_count,execution_date,execution_time" +
                " from store_procedure_schedule where sql_command =:command and status = '1'");
        Query query = entityManager.createNativeQuery(stringBuilder.toString());
        query.setParameter("command", command);

        return query.getResultList();
    }
}
