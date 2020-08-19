package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanQuarterlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.PlanQuarterly;

import java.math.BigInteger;
import java.util.List;

public interface PlanQuarterlyService {

    List<PlanQuarterlyDTO> getPlanQuarterlyByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    BigInteger countPlanQuarterlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    String updatePlanQuarterly(List<PlanQuarterly> plstPlanQuarterly,StaffDTO staffDTO) throws Exception;

    int deletePlanQuarterly(List<PlanQuarterly> plstPlanQuarterlies) throws Exception;

}
