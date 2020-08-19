package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.PlanMonthly;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public interface PlanMonthlyRepoCustom {

     List<PlanMonthlyDTO> findPlanMonthlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;
     
     List<PlanMonthlyDTO> findPlanMonthlysByReportSql(PlanMonthlyDTO planMonthlyDTO) throws Exception;

     PlanMonthly findPlanMonthlyFromPlan(Object o,StaffDTO staffDTO) throws Exception;

     void persist(PlanMonthly planMonthly);

     void update(PlanMonthly planMonthly);

     public BigInteger countPlanMonthlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

     int updatePlanMonthly(Long plngPlanMonthlyId, double pdblFschedule, Timestamp updateDate) throws Exception;

     int deletePlanMonthly(Long plngPlanMonthlyId) throws Exception;

     List<PlanMonthlyDTO> findTemplateFileVDS(PlanMonthlyDTO planMonthlyDTO,StaffDTO staffDTO) throws Exception;

     List<PlanMonthlyDTO> findTemplateFileLevel(PlanMonthlyDTO planMonthlyDTO,StaffDTO staffDTO) throws Exception;

     StringBuilder buildSQLFromPlan(Object o, HashMap params, StaffDTO staffDTO) throws Exception;

     double getScheduleOfShop(PlanMonthly planMonthlyDTO,StaffDTO staffDTO) throws Exception;

     double getSumScheduleChildShop(PlanMonthly planMonthlyDTO,List<Long> ids,StaffDTO staffDTO) throws Exception;

     double getSumScheduleStaff(PlanMonthly planMonthlyDTO,List<Long> ids,StaffDTO staffDTO) throws Exception;

}
