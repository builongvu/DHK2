package vn.vissoft.dashboard.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanYearlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.model.PlanYearly;

import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.PlanMonthlyService;
import vn.vissoft.dashboard.services.PlanYearlyService;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class PlanYearlyServiceImpl implements PlanYearlyService {

    @Autowired
    private PlanYearlyRepo planYearlyRepo;

    @Autowired
    private PlanYearlyHisRepo planYearlyHisRepo;

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private PlanMonthlyService planMonthlyService;

    @Autowired
    private ApParamRepo apParamRepo;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");


    public static final Logger LOGGER = LogManager.getLogger(PlanQuarterlyServiceImpl.class);
    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;


    @Override
    public List<PlanYearlyDTO> getPlanYearlyByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        return planYearlyRepo.findPlanYearlysByCondition(planMonthlyDTO, staffDTO);
    }

    @Override
    public BigInteger countPlanYearlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        return planYearlyRepo.countPlanYearlysByCondition(planMonthlyDTO, staffDTO);
    }

    @Override
    public String updatePlanYearly(List<PlanYearly> plstPlanYearlies, StaffDTO staffDTO) throws Exception {
        int vintCount = 0;
        Date vdtCurrentDate = new Date();
        long vlngTime = vdtCurrentDate.getTime();
        Long vlngPrdId;
        Long vlngServiceId;
        String vstrChannelCode;
        String vstrShopCode;
        String vstrStaffCode;
        Timestamp timestamp = new Timestamp(vlngTime);
        Map<Long, Double> hmpSchedule = new HashMap<>();
        Map<Long, Double> hmpChangeSchedule = new HashMap<>();
        Map<Long, Double> hmpSumScheduleChild = new HashMap<>();

        double dblSchelduleOfShop = 0d;
        double dblSumSchelduleChildShop = 0d;
        double dblChangeSchedule = 0d;
        String serviceName = null;
        String vstrMessage = "";
        boolean checkShopOfProvince = planMonthlyService.checkShopCodeOfProvince(staffDTO);

        List<Long> idList = plstPlanYearlies.stream()
                .map(PlanYearly::getId)
                .collect(Collectors.toList());

        for (PlanYearly plan : plstPlanYearlies) {
            dblSchelduleOfShop = planYearlyRepo.getScheduleOfShop(plan, staffDTO);
            if (!hmpSchedule.containsKey(plan.getServiceId()))
                hmpSchedule.put(plan.getServiceId(), dblSchelduleOfShop);
            if (!hmpChangeSchedule.containsKey(plan.getServiceId())) {
                dblChangeSchedule = plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            } else {
                dblChangeSchedule = hmpChangeSchedule.get(plan.getServiceId()) + plan.getfSchedule();
                hmpChangeSchedule.put(plan.getServiceId(), dblChangeSchedule);
            }

            dblSumSchelduleChildShop = planYearlyRepo.getSumScheduleChildShop(plan, idList, staffDTO);
            if (!hmpSumScheduleChild.containsKey(plan.getServiceId()))
                hmpSumScheduleChild.put(plan.getServiceId(), dblSumSchelduleChildShop);
        }


        for (PlanYearly planYearly : plstPlanYearlies) {
            if (!DataUtil.isNullObject(planYearly)) {
                for (Map.Entry<Long, Double> entry : hmpSchedule.entrySet()) {
                    if (entry.getKey() == planYearly.getServiceId() && hmpSumScheduleChild.containsKey(entry.getKey()) && hmpChangeSchedule.containsKey(entry.getKey())) {
                        if (checkShopOfProvince && Double.sum(hmpSumScheduleChild.get(entry.getKey()), hmpChangeSchedule.get(entry.getKey())) < entry.getValue()) {
                            serviceName = serviceRepo.findNameById(planYearly.getServiceId());
                            vstrMessage = I18N.get("schedule.update.error") + " " + serviceName;
                            break;
                        }

                        if (DataUtil.isNullOrEmpty(vstrMessage)) {
                            vlngPrdId = planYearly.getPrdId();
                            vlngServiceId = planYearly.getServiceId();
                            vstrChannelCode = planYearly.getVdsChannelCode();
                            vstrShopCode = planYearly.getShopCode();
                            vstrStaffCode = planYearly.getStaffCode();

                            PlanYearly planHis = planYearlyHisRepo.findPlanYearlyHis(vlngPrdId, vlngServiceId, vstrChannelCode, vstrShopCode, vstrStaffCode);
                            if (!DataUtil.isNullObject(planHis)) {
                                planYearlyHisRepo.savePlanYearHis(planHis);
                                vintCount += planYearlyRepo.updatePlanYearly(planYearly.getId(), planYearly.getfSchedule(), timestamp);
                                break;
                            }
                        }
                    }
                }

            }

        }
        if (vstrMessage.length() != 0)
            return vstrMessage;
        else return String.valueOf(vintCount);
    }

    @Override
    public int deletePlanYearly(List<PlanYearly> plstPlanYearlies) throws Exception {
        int vintCount = 0;
        Long vlngPrdId;
        Long vlngServiceId;
        String vstrChannelCode;
        String vstrShopCode;
        String vstrStaffCode;

        for (PlanYearly planYearly : plstPlanYearlies) {
            if (!DataUtil.isNullObject(planYearly)) {
                vlngPrdId = planYearly.getPrdId();
                vlngServiceId = planYearly.getServiceId();
                vstrChannelCode = planYearly.getVdsChannelCode();
                vstrShopCode = planYearly.getShopCode();
                vstrStaffCode = planYearly.getStaffCode();
                PlanYearly planHis = planYearlyHisRepo.findPlanYearlyHis(vlngPrdId, vlngServiceId, vstrChannelCode, vstrShopCode, vstrStaffCode);
                if (!DataUtil.isNullObject(planHis)) {
                    planYearlyHisRepo.savePlanYearHis(planHis);
                    vintCount += planYearlyRepo.deletePlanYearly(planYearly.getId());
                }

            }
        }
        return vintCount;
    }

}
