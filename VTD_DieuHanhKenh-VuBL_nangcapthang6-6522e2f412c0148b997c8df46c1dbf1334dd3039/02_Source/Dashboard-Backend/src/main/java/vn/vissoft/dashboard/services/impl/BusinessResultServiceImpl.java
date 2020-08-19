package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.VdsStaff;
import vn.vissoft.dashboard.repo.BusinessResultsRepoCustom;
import vn.vissoft.dashboard.repo.VdsStaffRepo;
import vn.vissoft.dashboard.services.BusinessResultsService;
import vn.vissoft.dashboard.repo.VdsStaffRepoCustom;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusinessResultServiceImpl implements BusinessResultsService {

    @Autowired
    private BusinessResultsRepoCustom businessResultsRepoCustom;

    @Autowired
    private VdsStaffRepo vdsStaffRepo;

    private DecimalFormat df = new DecimalFormat("###.##");
    private NumberFormat format = NumberFormat.getInstance();

    /**
     * lay ra du lieu bao cao theo tinh
     *
     * @author VuBL
     * @since 2020/07
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     */
    @Override
    public ResultsProvincialDTO getResultProvincial(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        ResultsProvincialDTO datas = new ResultsProvincialDTO();
        List<BusinessResultProvincialDTO> resultsProvincial = new ArrayList<>();
        List<Object[]> objsData = businessResultsRepoCustom.findResultProvincial(dashboardRequestDTO);
        List<Object[]> objsScore = businessResultsRepoCustom.findServiceScore(dashboardRequestDTO);
        Object[] objScore = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String createTime = dtf.format(now);

        if (!DataUtil.isNullOrEmpty(objsScore)) {
            objScore = objsScore.get(0);
        }

        //set data cac dong
        if (objsData != null && !objsData.isEmpty()) {
            for (Object[] obj : objsData) {
                BusinessResultProvincialDTO data = new BusinessResultProvincialDTO();
                List<BusinessResultDetailDTO> details = new ArrayList<>();

                data.setProvincialCode((String) obj[0]);
                data.setTotalScore(obj[1] == null ? null : (Double) obj[1]);
                data.setRank(obj[2] == null ? null : (Integer) obj[2]);

                //thue bao tang them
                Double scheduleTbtt = (Double) obj[3];
                Double performN1Tbtt = (Double) obj[4];
                Double performNTbtt = (Double) obj[5];
                Double deltaTbtt = (Double) obj[6];
                Double percentCompleteTbtt = (Double) obj[7];
                Double scorePassTbtt = (Double) obj[8];
                BusinessResultDetailDTO detail1 = new BusinessResultDetailDTO();
                detail1.setServiceName(I18N.get(Constants.BUSINESS_RESULT.THUE_BAO_TANG_THEM));
                detail1.setScheduleMonth(scheduleTbtt == null ? null : Double.valueOf(df.format(scheduleTbtt)));
                detail1.setPerformAccumulatedN1(performN1Tbtt == null ? null : Double.valueOf(df.format(performN1Tbtt)));
                detail1.setPerformAccumulatedN(performNTbtt == null ? null : Double.valueOf(df.format(performNTbtt)));
                detail1.setDelta(deltaTbtt == null ? null : Double.valueOf(df.format(deltaTbtt)));
                detail1.setComplete(percentCompleteTbtt == null ? null : Double.valueOf(df.format(percentCompleteTbtt/100)));
                detail1.setScorePass(scorePassTbtt == null ? null : Double.valueOf(df.format(scorePassTbtt)));

                //thue bao moi
                Double scheduleTbm = (Double) obj[9];
                Double performTbm = (Double) obj[10];
                Double percentCompleteTbm = (Double) obj[11];
                Double scorePassTbm = (Double) obj[12];
                BusinessResultDetailDTO detail2 = new BusinessResultDetailDTO();
                detail2.setServiceName(I18N.get(Constants.BUSINESS_RESULT.THUE_BAO_MOI));
                detail2.setScheduleMonth(scheduleTbm == null ? null : Double.valueOf(df.format(scheduleTbm)));
                detail2.setPerformAccumulatedN(performTbm == null ? null : Double.valueOf(df.format(performTbm)));
                detail2.setComplete(percentCompleteTbm == null ? null : Double.valueOf(df.format(percentCompleteTbm/100)));
                detail2.setScorePass(scorePassTbm == null ? null : Double.valueOf(df.format(scorePassTbm)));

                //thue bao roi mang
                Double scheduleTbrm = (Double) obj[13];
                Double performTbrm = (Double) obj[14];
                Double percentCompleteTbrm = (Double) obj[15];
                Double scorePassTbrm = (Double) obj[16];
                BusinessResultDetailDTO detail3 = new BusinessResultDetailDTO();
                detail3.setServiceName(I18N.get(Constants.BUSINESS_RESULT.THUE_BAO_ROI_MANG));
                detail3.setScheduleMonth(scheduleTbrm == null ? null : Double.valueOf(df.format(scheduleTbrm)));
                detail3.setPerformAccumulatedN(performTbrm == null ? null : Double.valueOf(df.format(performTbrm)));
                detail3.setComplete(percentCompleteTbrm == null ? null : Double.valueOf(df.format(percentCompleteTbrm/100)));
                detail3.setScorePass(scorePassTbrm == null ? null : Double.valueOf(df.format(scorePassTbrm)));

                details.add(detail1);
                details.add(detail2);
                details.add(detail3);
                data.setListDetail(details);
                resultsProvincial.add(data);

            }
            datas.setResultsProvincial(resultsProvincial);
        }

        //set data
        datas.setCreateTime(createTime);
        if (!DataUtil.isNullOrEmpty(objScore)) {
            datas.setScoreTbtt(objScore[0] == null ? null : Double.valueOf(df.format(objScore[0])));
            datas.setScoreTbm(objScore[1] == null ? null : Double.valueOf(df.format(objScore[1])));
            datas.setScoreTbrm(objScore[2] == null ? null : Double.valueOf(df.format(objScore[2])));
        }

        return datas;
    }

    /**
     * lay ra du lieu bao cao theo nhan vien
     *
     * @author VuBL
     * @since 2020/07
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     */
    @Override
    public ResultsStaffDTO getResultsStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        ResultsStaffDTO datas = new ResultsStaffDTO();
        List<BusinessResultStaffDTO> resultsStaff = new ArrayList<>();
        List<Object[]> objsData = businessResultsRepoCustom.findResultStaff(dashboardRequestDTO);
        List<Object[]> objsScore = businessResultsRepoCustom.findServiceScore(dashboardRequestDTO);
        Object[] objScore = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String createTime = dtf.format(now);

        if (!DataUtil.isNullOrEmpty(objsScore)) {
            objScore = objsScore.get(0);
        }

        //set data cac dong
        if (objsData != null && !objsData.isEmpty()) {
            for (Object[] obj : objsData) {
                BusinessResultStaffDTO data = new BusinessResultStaffDTO();
                List<BusinessResultDetailDTO> details = new ArrayList<>();

                data.setProvincialCode((String) obj[0]);
                data.setStaffCode(obj[2] == null ? null : (String) obj[2]);
                data.setStaffName(obj[3] == null ? null : (String) obj[3]);
                data.setTotalScore(obj[4] == null ? null : (Double) obj[4]);
                data.setRank(obj[5] == null ? null : (Integer) obj[5]);

                //thue bao tang them
                Double scheduleTbtt = (Double) obj[6];
                Double performN1Tbtt = (Double) obj[7];
                Double performNTbtt = (Double) obj[8];
                Double deltaTbtt = (Double) obj[9];
                Double percentCompleteTbtt = (Double) obj[10];
                Double scorePassTbtt = (Double) obj[11];
                BusinessResultDetailDTO detail1 = new BusinessResultDetailDTO();
                detail1.setServiceName(I18N.get(Constants.BUSINESS_RESULT.THUE_BAO_TANG_THEM));
                detail1.setScheduleMonth(scheduleTbtt == null ? null : Double.valueOf(df.format(scheduleTbtt)));
                detail1.setPerformAccumulatedN1(performN1Tbtt == null ? null : Double.valueOf(df.format(performN1Tbtt)));
                detail1.setPerformAccumulatedN(performNTbtt == null ? null : Double.valueOf(df.format(performNTbtt)));
                detail1.setDelta(deltaTbtt == null ? null : Double.valueOf(df.format(deltaTbtt)));
                detail1.setComplete(percentCompleteTbtt == null ? null : Double.valueOf(df.format(percentCompleteTbtt/100)));
                detail1.setScorePass(scorePassTbtt == null ? null : Double.valueOf(df.format(scorePassTbtt)));

                //thue bao moi
                Double scheduleTbm = (Double) obj[12];
                Double performTbm = (Double) obj[13];
                Double percentCompleteTbm = (Double) obj[14];
                Double scorePassTbm = (Double) obj[15];
                BusinessResultDetailDTO detail2 = new BusinessResultDetailDTO();
                detail2.setServiceName(I18N.get(Constants.BUSINESS_RESULT.THUE_BAO_MOI));
                detail2.setScheduleMonth(scheduleTbm == null ? null : Double.valueOf(df.format(scheduleTbm)));
                detail2.setPerformAccumulatedN(performTbm == null ? null : Double.valueOf(df.format(performTbm)));
                detail2.setComplete(percentCompleteTbm == null ? null : Double.valueOf(df.format(percentCompleteTbm/100)));
                detail2.setScorePass(scorePassTbm == null ? null : Double.valueOf(df.format(scorePassTbm)));

                //thue bao roi mang
                Double scheduleTbrm = (Double) obj[16];
                Double performTbrm = (Double) obj[17];
                Double percentCompleteTbrm = (Double) obj[18];
                Double scorePassTbrm = (Double) obj[19];
                BusinessResultDetailDTO detail3 = new BusinessResultDetailDTO();
                detail3.setServiceName(I18N.get(Constants.BUSINESS_RESULT.THUE_BAO_ROI_MANG));
                detail3.setScheduleMonth(scheduleTbrm == null ? null : Double.valueOf(df.format(scheduleTbrm)));
                detail3.setPerformAccumulatedN(performTbrm == null ? null : Double.valueOf(df.format(performTbrm)));
                detail3.setComplete(percentCompleteTbrm == null ? null : Double.valueOf(df.format(percentCompleteTbrm/100)));
                detail3.setScorePass(scorePassTbrm == null ? null : Double.valueOf(df.format(scorePassTbrm)));

                details.add(detail1);
                details.add(detail2);
                details.add(detail3);
                data.setListDetail(details);
                resultsStaff.add(data);

            }
            datas.setResultsStaff(resultsStaff);
        }

        //set data
        datas.setCreateTime(createTime);
        if (!DataUtil.isNullOrEmpty(objScore)) {
            datas.setScoreTbtt(objScore[0] == null ? null : Double.valueOf(df.format(objScore[0])));
            datas.setScoreTbm(objScore[1] == null ? null : Double.valueOf(df.format(objScore[1])));
            datas.setScoreTbrm(objScore[2] == null ? null : Double.valueOf(df.format(objScore[2])));
        }

        return datas;
    }

    /**
     * lay ra nguoi xuat bao cao
     *
     * @author AnhNQ
     * @since 2020/07
     * @param pstrStaffCode
     * @return
     * @throws Exception
     */
    @Override
    public VdsStaff findVdsStaffByCondition(String pstrStaffCode){
        return vdsStaffRepo.findVdsStaffByCondition(null,null,pstrStaffCode);
    }

}
