package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanQuarterlyDTO;
import vn.vissoft.dashboard.dto.PlanYearlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.model.PlanYearly;

import java.math.BigInteger;
import java.util.List;

public interface PlanYearlyService {

    List<PlanYearlyDTO> getPlanYearlyByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    BigInteger countPlanYearlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    String updatePlanYearly(List<PlanYearly> plstPlanYearlies,StaffDTO staffDTO) throws Exception;

    int deletePlanYearly(List<PlanYearly> plstPlanYearlies) throws Exception;

}
