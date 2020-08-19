package vn.vissoft.dashboard.services.impl;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.TableRow;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.ContentTopRepoCustom;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.services.ApParamService;
import vn.vissoft.dashboard.services.ContentTopService;

import javax.swing.text.TableView;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ContentTopServiceImpl implements ContentTopService {

    private static final Logger LOGGER = LogManager.getLogger(ContentTopServiceImpl.class);

    @Autowired
    private ContentTopRepoCustom contentTopRepoCustom;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private ApParamService apParamService;

    private DecimalFormat df = new DecimalFormat("###.##");
    private NumberFormat format = NumberFormat.getInstance();

    /**
     * lay data spark SL DV/NV LIEN TIEP KHONG DAT (level 1)
     *
     * @author VuBL
     * @since 2020/05
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     */
    @Override
    public ContinuityFailDTO getContinuityFail(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        ContinuityFailDTO data = new ContinuityFailDTO();
        Object[] obj = contentTopRepoCustom.getContinuityFail(dashboardRequestDTO);
//        String chartType = configSingleChartDTO.getChartType();
        data.setType(configSingleChartDTO.getChartType().trim());
        if (configSingleChartDTO.getDrilldown() != null) {
            if (configSingleChartDTO.getDrilldown() == 1) {
                data.setDrilldown(true);
            }
            if (configSingleChartDTO.getDrilldown() == 0) {
                data.setDrilldown(false);
            }
        }
        if (configSingleChartDTO.getDrillDownObjectId() != null) {
            data.setDrilldownObject(Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));
        }
        if (configSingleChartDTO.getDrillDownType() != null) {
            data.setDrilldownType(configSingleChartDTO.getDrillDownType());
        }

        data.setPercent(String.valueOf(obj[2] == null ? "0%" : obj[2]+"%"));
        data.setQuantity(String.valueOf(obj[0] == null ? "" : obj[0]));

        return data;
    }

    /**
     * lay data spark SL DV/NV LIEN TIEP KHONG DAT (level 2)
     *
     * @author VuBL
     * @since 2020/05
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     */
    @Override
    public TableDTO getContinuityFailLvTwo(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        TableDTO data = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        int vintCodeWarning = Integer.parseInt(dashboardRequestDTO.getCodeWarning());
        List<Object[]> vlstObjs = contentTopRepoCustom.getContinuityFailLvTwo(dashboardRequestDTO, configSingleChartDTO);
        List<String> vlstParamTitleChart = Lists.newArrayList();
        String vstrShopTitle = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        int vintColumnId = 0;

        //add cac param vao title chart
        vlstParamTitleChart.add(vstrShopTitle);
        vlstParamTitleChart.add(dashboardRequestDTO.getCodeWarning());
        vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));

        //set du lieu cho chart
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            configSingleChartDTO.setTitle(I18N.get(Constants.CODE_WARNING.TITLE_CHART_DAY, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            configSingleChartDTO.setTitle(I18N.get(Constants.CODE_WARNING.TITLE_CHART_MONTH, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            configSingleChartDTO.setTitle(I18N.get(Constants.CODE_WARNING.TITLE_CHART_QUARTER, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        data.setTitle(configSingleChartDTO.getTitle());
        data.setType(configSingleChartDTO.getChartType().trim());

        //set 2 cot mac dinh la don vi/nhan vien va thang n
        TableColumnDTO columnUnitStaff = new TableColumnDTO();
        columnUnitStaff.setAlign(Constants.CODE_WARNING.LEFT);
        columnUnitStaff.setType(Constants.CODE_WARNING.TEXT);
        columnUnitStaff.setName(I18N.get(Constants.CODE_WARNING.UNIT_STAFF));
        columnUnitStaff.setColumnId(vintColumnId);
        vlstColumns.add(columnUnitStaff);
        TableColumnDTO columnValue = new TableColumnDTO();
        columnValue.setAlign(Constants.CODE_WARNING.LEFT);
        columnValue.setType(Constants.CODE_WARNING.TEXT);
        vintColumnId = vintColumnId + 1;
        columnValue.setColumnId(vintColumnId);
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            columnValue.setName(I18N.get(Constants.CODE_WARNING.VALUE_DAY));
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            columnValue.setName(I18N.get(Constants.CODE_WARNING.VALUE_MONTH));
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            columnValue.setName(I18N.get(Constants.CODE_WARNING.VALUE_QUARTER));
        }
        vlstColumns.add(columnValue);


        //set cot cho bang theo codeWarning
        for (int i = 1 ; i < vintCodeWarning; i++) {
            vintColumnId = vintColumnId + 1;
            TableColumnDTO column = new TableColumnDTO();
            column.setType(Constants.CODE_WARNING.TEXT);
            column.setAlign(Constants.CODE_WARNING.LEFT);
            column.setColumnId(vintColumnId);
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                column.setName(I18N.get(Constants.CODE_WARNING.VALUE_DAY)+"-"+i);
            }
            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
                column.setName(I18N.get(Constants.CODE_WARNING.VALUE_MONTH)+"-"+i);
            }
            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
                column.setName(I18N.get(Constants.CODE_WARNING.VALUE_QUARTER)+"-"+i);
            }
            vlstColumns.add(column);
        }

        //set dong cho bang
        if (vlstObjs != null && !vlstObjs.isEmpty()) {
            int vintcountCodeWarning = vintCodeWarning;
            for (int i = 0; i < vlstObjs.size(); i = i + vintCodeWarning) {
//                int vintcountCodeWarning = vintCodeWarning;
                TableRowDTO row = new TableRowDTO();
                List<TableCellBaseDTO> vlstCells = new ArrayList<>();
                TableCellBaseDTO cellUnitStaff = new TableCellBaseDTO();
                cellUnitStaff.setViewValue(String.valueOf(vlstObjs.get(i)[2]));
                cellUnitStaff.setColor(Constants.COLORS.BLACK);
                cellUnitStaff.setColumnId(Integer.parseInt(Constants.CODE_WARNING.ZERO));
                vlstCells.add(cellUnitStaff);
                TableCellBaseDTO cellValue = new TableCellBaseDTO();
                cellValue.setViewValue(String.valueOf(vlstObjs.get(i)[3]));
                cellValue.setColor(Constants.COLORS.BLACK);
                cellValue.setColumnId(Integer.parseInt(Constants.CODE_WARNING.ONE));
                vlstCells.add(cellValue);
//                if (i == vintCodeWarning) {
//                    vintcountCodeWarning = vintcountCodeWarning + vintCodeWarning;
//                }
                int vintColumnIdRow = 2;
                for (int j = i+1; j < vintcountCodeWarning; j++) {
                    TableCellBaseDTO cellValueNext = new TableCellBaseDTO();
                    cellValueNext.setViewValue(String.valueOf(vlstObjs.get(j)[3]));
                    cellValueNext.setColor(Constants.COLORS.BLACK);
                    cellValueNext.setColumnId(vintColumnIdRow);
                    vlstCells.add(cellValueNext);
                    vintColumnIdRow = vintColumnIdRow + 1;
                }
                if (vintcountCodeWarning + vintCodeWarning <= vlstObjs.size()) {
                    vintcountCodeWarning = vintcountCodeWarning + vintCodeWarning;
                }
                row.setCells(vlstCells);
                vlstRows.add(row);
            }
        }

        data.setColumns(vlstColumns);
        data.setRows(vlstRows);

        return data;
    }

    /**
     * lay du lieu cho content top kenh tinh spark 3 va 4 (level 1)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public List<TopByServiceDTO> getTopByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        List<TopByServiceDTO> vlstData = new ArrayList<>();

        List<ApParamDTO> vlstApParam = apParamService.findByType(Constants.TOP_BY_SERVICE.COMPLETION_RATE);
        Object[] vObj = contentTopRepoCustom.getTopByService(dashboardRequestDTO, configSingleChartDTO);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("params");
        JsonObject queryParam;
        String vstrBadColor = null;
        String vstrGoodColor = null;
        int vintMucMot = 0;
        int vintMucHai = 0;
        int vintMucBa = 0;

        //lay mau
        if (!DataUtil.isNullObject(varrLines.get(0))) {
            queryParam = (JsonObject) varrLines.get(0);
            vstrBadColor = queryParam.get("bad").getAsString();
            vstrGoodColor = queryParam.get("good").getAsString();
        }

        //lay ra 3 muc tu cau hinh
        for (ApParamDTO ap : vlstApParam) {
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.ONE)) {
                vintMucMot = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.TWO)) {
                vintMucHai = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.THREE)) {
                vintMucBa = Integer.parseInt(ap.getValue());
            }
        }

        if (vObj != null) {
            List<String> vlstPrLessThanOne = Lists.newArrayList();
            List<String> vlstPrBetweenOneAndTwo = Lists.newArrayList();
            List<String> vlstPrBetweenTwoAndThree = Lists.newArrayList();
            List<String> vlstPrGreaterThanThree = Lists.newArrayList();
            vlstPrLessThanOne.add(String.valueOf(vintMucMot));
            vlstPrBetweenOneAndTwo.add(String.valueOf(vintMucMot));
            vlstPrBetweenOneAndTwo.add(String.valueOf(vintMucHai));
            vlstPrBetweenTwoAndThree.add(String.valueOf(vintMucHai));
            vlstPrBetweenTwoAndThree.add(String.valueOf(vintMucBa));
            vlstPrGreaterThanThree.add(String.valueOf(vintMucBa));
//            Double vintTongDonVi = vObj[4] == null ? null : (Double) vObj[4];
//            Long vintLessThanOne = DataUtil.safeToLong(vObj[0]);
//            Long vintBetweenOneAndTwo = DataUtil.safeToLong(vObj[1]);
//            Long vintBetweenTwoAndThree = DataUtil.safeToLong(vObj[2]);
//            Long vintGreaterThanThree = DataUtil.safeToLong(vObj[3]);
//            double vdblPcLessThanOne = 0;
//            double vdblPcBetweenOneAndTwo = 0;
//            double vdblPcBetweenTwoAndThree = 0;
//            double vdblPcGreaterThanThree = 0;

            //set data nho hon muc 1
            TopByServiceDTO dataLessThanOne = new TopByServiceDTO();
//            if (vObj[4] != null && vObj[0] != null) {
//                vdblPcLessThanOne = vintLessThanOne / vintTongDonVi * 100;
//            } else {
//                vdblPcLessThanOne = 0;
//            }
            dataLessThanOne.setQuantity(String.valueOf(vObj[0] == null ? "0" : vObj[0]));
//            dataLessThanOne.setPercent(df.format(vdblPcLessThanOne)+"%");
            dataLessThanOne.setColor(vstrBadColor == null ? null : vstrBadColor);
            dataLessThanOne.setDescription(I18N.get(Constants.TOP_BY_SERVICE.LESS_THAN_ONE, (String[]) vlstPrLessThanOne.toArray(new String[vlstPrLessThanOne.size()])));
            vlstData.add(dataLessThanOne);

            //set data giua 1 va 2
            TopByServiceDTO dataBetweenOneAndTwo = new TopByServiceDTO();
//            if (vObj[4] != null && vObj[1] != null) {
//                vdblPcBetweenOneAndTwo = vintBetweenOneAndTwo / vintTongDonVi * 100;
//            } else {
//                vdblPcBetweenOneAndTwo = 0;
//            }
            dataBetweenOneAndTwo.setQuantity(String.valueOf(vObj[1] == null ? "0" : vObj[1]));
//            dataBetweenOneAndTwo.setPercent(df.format(vdblPcBetweenOneAndTwo)+"%");
            dataBetweenOneAndTwo.setColor(vstrBadColor == null ? null : vstrBadColor);
            dataBetweenOneAndTwo.setDescription(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_ONE_AND_TWO, (String[]) vlstPrBetweenOneAndTwo.toArray(new String[vlstPrBetweenOneAndTwo.size()])));
            vlstData.add(dataBetweenOneAndTwo);

            //set data giua 2 va 3
            TopByServiceDTO dataBetweenTwoAndThree = new TopByServiceDTO();
//            if (vObj[4] != null && vObj[2] != null) {
//                vdblPcBetweenTwoAndThree = vintBetweenTwoAndThree / vintTongDonVi * 100;
//            } else {
//                vdblPcBetweenTwoAndThree = 0;
//            }
            dataBetweenTwoAndThree.setQuantity(String.valueOf(vObj[2] == null ? "0" : vObj[2]));
//            dataBetweenTwoAndThree.setPercent(df.format(vdblPcBetweenTwoAndThree)+"%");
            dataBetweenTwoAndThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            dataBetweenTwoAndThree.setDescription(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_TWO_AND_THREE, (String[]) vlstPrBetweenTwoAndThree.toArray(new String[vlstPrBetweenTwoAndThree.size()])));
            vlstData.add(dataBetweenTwoAndThree);

            //set data lon hon 3
            TopByServiceDTO dataGreaterThanThree = new TopByServiceDTO();
//            if (vObj[4] != null && vObj[3] != null) {
//                vdblPcGreaterThanThree = vintGreaterThanThree / vintTongDonVi * 100;
//            } else {
//                vdblPcGreaterThanThree = 0;
//            }
            dataGreaterThanThree.setQuantity(String.valueOf(vObj[3] == null ? "0" : vObj[3]));
//            dataGreaterThanThree.setPercent(df.format(vdblPcGreaterThanThree)+"%");
            dataGreaterThanThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            dataGreaterThanThree.setDescription(I18N.get(Constants.TOP_BY_SERVICE.GREATER_THAN_THREE, (String[]) vlstPrGreaterThanThree.toArray(new String[vlstPrGreaterThanThree.size()])));
            vlstData.add(dataGreaterThanThree);
        }

        return vlstData;
    }

    /**
     * lay data table theo service spark 3,4 (level 2)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public TableDTO getTableByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        List<Object[]> vlstObj = contentTopRepoCustom.getTableByService(dashboardRequestDTO, configSingleChartDTO);
        int vintCycleId = dashboardRequestDTO.getCycleId();
        List<String> vlstTitle = new ArrayList<>();
        String vstrPRow = dashboardRequestDTO.getpRow();
        List<String> vlstParamTitleChart = Lists.newArrayList();
        String vstrShopTitle = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        int vintColumnIdForColumn = 0;
        int vintColumnIdForRow = 0;
        List<ApParamDTO> vlstApParam = apParamService.findByType(Constants.TOP_BY_SERVICE.COMPLETION_RATE);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("params");
        JsonObject queryParam;
        String vstrColor = null;
        int vintMucMot = 0;
        int vintMucHai = 0;
        int vintMucBa = 0;

        //lay ra 3 muc tu cau hinh
        for (ApParamDTO ap : vlstApParam) {
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.ONE)) {
                vintMucMot = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.TWO)) {
                vintMucHai = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.THREE)) {
                vintMucBa = Integer.parseInt(ap.getValue());
            }
        }

        //set title chart
        vlstParamTitleChart.add(vstrShopTitle);
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ZERO)) {
            vlstParamTitleChart.add(String.valueOf(vintMucMot));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWZERO, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ONE)) {
            vlstParamTitleChart.add(String.valueOf(vintMucMot));
            vlstParamTitleChart.add(String.valueOf(vintMucHai));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWONE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.TWO)) {
            vlstParamTitleChart.add(String.valueOf(vintMucHai));
            vlstParamTitleChart.add(String.valueOf(vintMucBa));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWTWO, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.THREE)) {
            vlstParamTitleChart.add(String.valueOf(vintMucBa));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWTHREE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }


        //lay mau cot ty le hoan thanh
        if (!DataUtil.isNullObject(varrLines.get(0))) {
            queryParam = (JsonObject) varrLines.get(0);
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ZERO) || vstrPRow.equals(Constants.TOP_BY_SERVICE.ONE)) {
                vstrColor = queryParam.get("bad").getAsString();
            }
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.TWO) || vstrPRow.equals(Constants.TOP_BY_SERVICE.THREE)) {
                vstrColor = queryParam.get("good").getAsString();
            }
        }

        //lay ra cac title
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
           vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_UNIT));
           vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_SCHEDULE_DAY));
           vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_PERFORM_DAY));
           vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_COMPLETION_RATE));
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_UNIT));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_SCHEDULE_MONTH));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_PERFORM_MONTH));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_COMPLETION_RATE));
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_UNIT));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_SCHEDULE_QUARTER));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_PERFORM_QUARTER));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_COMPLETION_RATE));
        }

        //set cot
        TableColumnDTO columnUnit = new TableColumnDTO();
        columnUnit.setType(Constants.CODE_WARNING.TEXT);
        columnUnit.setAlign(Constants.CODE_WARNING.LEFT);
        columnUnit.setColumnId(vintColumnIdForColumn);
        columnUnit.setName(vlstTitle.get(0));
        vlstColumns.add(columnUnit);
        vlstTitle.remove(0);
        for (String s : vlstTitle) {
            vintColumnIdForColumn = vintColumnIdForColumn + 1;
            TableColumnDTO column = new TableColumnDTO();
            column.setType(Constants.CODE_WARNING.TEXT);
            column.setAlign(Constants.CODE_WARNING.RIGHT);
            column.setColumnId(vintColumnIdForColumn);
            column.setName(s);
            vlstColumns.add(column);
        }

        //set dong
        if (vlstObj != null && !vlstObj.isEmpty()) {
            int vintRowIndex = 0;
            for (Object[] obj : vlstObj) {
                TableRowDTO row = new TableRowDTO();
                List<TableCellBaseDTO> vlstCell = new ArrayList<>();

                TableCellBaseDTO cellUnit = new TableCellBaseDTO();
                cellUnit.setColor(Constants.COLORS.BLACK);
                cellUnit.setColumnId(vintColumnIdForRow);
                cellUnit.setViewValue((String) obj[0]);
                vlstCell.add(cellUnit);

                TableCellBaseDTO cellSchedule = new TableCellBaseDTO();
                cellSchedule.setColor(Constants.COLORS.BLACK);
                cellSchedule.setColumnId(vintColumnIdForRow + 1);
                cellSchedule.setViewValue(df.format(obj[2]));
                vlstCell.add(cellSchedule);

                TableCellBaseDTO cellPerform = new TableCellBaseDTO();
                cellPerform.setColor(Constants.COLORS.BLACK);
                cellPerform.setColumnId(vintColumnIdForRow + 2);
                cellPerform.setViewValue(df.format(obj[1]));
                vlstCell.add(cellPerform);

                TableCellBaseDTO cellRate = new TableCellBaseDTO();
                cellRate.setColor(vstrColor);
                cellRate.setColumnId(vintColumnIdForRow + 3);
                cellRate.setViewValue(String.valueOf(obj[3]+"%"));
                vlstCell.add(cellRate);

                row.setCells(vlstCell);
                row.setRowIndex(vintRowIndex);
                vintRowIndex = vintRowIndex + 1;
                vlstRows.add(row);
            }
        }

        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);

        return tableDTO;
    }

    /**
     * lay ra cac nguong hoan thanh cua chi tieu (lv 2 bang tong hop)
     *
     * @Author VuBL
     * @since 2020/05
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     */
    @Override
    public TableDTO getComplThresholdService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        Object[] vObj = contentTopRepoCustom.getComplThresholdService(dashboardRequestDTO, configSingleChartDTO);
        List<ApParamDTO> vlstApParam = apParamService.findByType(Constants.TOP_BY_SERVICE.COMPLETION_RATE);
        List<String> vlstTitle = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("params");
        JsonObject queryParam;
        int vintColumnId = 0;
        int vintRowId = 0;
        String vstrBadColor = null;
        String vstrGoodColor = null;
        int vintMucMot = 0;
        int vintMucHai = 0;
        int vintMucBa = 0;

        //lay mau
        if (!DataUtil.isNullObject(varrLines.get(0))) {
            queryParam = (JsonObject) varrLines.get(0);
            vstrBadColor = queryParam.get("bad").getAsString();
            vstrGoodColor = queryParam.get("good").getAsString();
        }

        //lay ra 3 muc tu cau hinh
        for (ApParamDTO ap : vlstApParam) {
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.ONE)) {
                vintMucMot = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.TWO)) {
                vintMucHai = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.THREE)) {
                vintMucBa = Integer.parseInt(ap.getValue());
            }
        }

        //set cac title bang vao list
        vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.LVONE_TITLE_THRESHOLD));
        vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.LVONE_TITLE_QUANTITY));
//        vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.LVONE_TITLE_OCCUPY));

        //set cac cot
        for (int i = 0; i < 2; i++) {
            TableColumnDTO column = new TableColumnDTO();
            column.setType(Constants.CODE_WARNING.TEXT);
            if (i > 0) {
                column.setAlign(Constants.CODE_WARNING.RIGHT);
            } else {
                column.setAlign(Constants.CODE_WARNING.LEFT);
            }
            column.setColumnId(vintColumnId);
            column.setName(vlstTitle.get(i));
            vintColumnId = vintColumnId + 1;
            vlstColumns.add(column);
        }

        //set cac dong
        if (vObj != null) {
            List<String> vlstPrLessThanOne = Lists.newArrayList();
            List<String> vlstPrBetweenOneAndTwo = Lists.newArrayList();
            List<String> vlstPrBetweenTwoAndThree = Lists.newArrayList();
            List<String> vlstPrGreaterThanThree = Lists.newArrayList();
            vlstPrLessThanOne.add(String.valueOf(vintMucMot));
            vlstPrBetweenOneAndTwo.add(String.valueOf(vintMucMot));
            vlstPrBetweenOneAndTwo.add(String.valueOf(vintMucHai));
            vlstPrBetweenTwoAndThree.add(String.valueOf(vintMucHai));
            vlstPrBetweenTwoAndThree.add(String.valueOf(vintMucBa));
            vlstPrGreaterThanThree.add(String.valueOf(vintMucBa));
//            Double vintTongDonVi = vObj[4] == null ? null : (Double) vObj[4];
//            Long vintLessThanOne = DataUtil.safeToLong(vObj[0]);
//            Long vintBetweenOneAndTwo = DataUtil.safeToLong(vObj[1]);
//            Long vintBetweenTwoAndThree = DataUtil.safeToLong(vObj[2]);
//            Long vintGreaterThanThree = DataUtil.safeToLong(vObj[3]);
//            double vdblPcLessThanOne = 0;
//            double vdblPcBetweenOneAndTwo = 0;
//            double vdblPcBetweenTwoAndThree = 0;
//            double vdblPcGreaterThanThree = 0;

            //dong nguong 1
            TableRowDTO rowLessThanOne = new TableRowDTO();
//            if (vObj[4] != null && vObj[0] != null) {
//                vdblPcLessThanOne = vintLessThanOne / vintTongDonVi * 100;
//            } else {
//                vdblPcLessThanOne = 0;
//            }
            List<TableCellBaseDTO> vlstCellLessThanOne = new ArrayList<>();
            TableCellBaseDTO cellOneLessThanOne = new TableCellBaseDTO();
            cellOneLessThanOne.setColor(vstrBadColor == null ? null : vstrBadColor);
            cellOneLessThanOne.setViewValue(I18N.get(Constants.TOP_BY_SERVICE.LESS_THAN_ONE, (String[]) vlstPrLessThanOne.toArray(new String[vlstPrLessThanOne.size()])));
            vlstCellLessThanOne.add(cellOneLessThanOne);
            TableCellBaseDTO cellTwoLessThanOne = new TableCellBaseDTO();
            cellTwoLessThanOne.setColor(vstrBadColor == null ? null : vstrBadColor);
            cellTwoLessThanOne.setViewValue(String.valueOf(vObj[0] == null ? "0" : vObj[0]));
            vlstCellLessThanOne.add(cellTwoLessThanOne);
//            TableCellBaseDTO cellThreeLessThanOne = new TableCellBaseDTO();
//            cellThreeLessThanOne.setColor(vstrBadColor == null ? null : vstrBadColor);
//            cellThreeLessThanOne.setViewValue(df.format(vdblPcLessThanOne)+"%");
//            vlstCellLessThanOne.add(cellThreeLessThanOne);
            rowLessThanOne.setRowIndex(vintRowId);
            rowLessThanOne.setClicked(true);
            rowLessThanOne.setCells(vlstCellLessThanOne);
            vlstRows.add(rowLessThanOne);

            //dong nguong 2
            TableRowDTO rowBetweenOneAndTwo = new TableRowDTO();
//            if (vObj[4] != null && vObj[1] != null) {
//                vdblPcBetweenOneAndTwo = vintBetweenOneAndTwo / vintTongDonVi * 100;
//            } else {
//                vdblPcBetweenOneAndTwo = 0;
//            }
            vintRowId = vintRowId + 1;
            List<TableCellBaseDTO> vlstCellOneToTwo = new ArrayList<>();
            TableCellBaseDTO cellOneOneToTwo = new TableCellBaseDTO();
            cellOneOneToTwo.setColor(vstrBadColor == null ? null : vstrBadColor);
            cellOneOneToTwo.setViewValue(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_ONE_AND_TWO, (String[]) vlstPrBetweenOneAndTwo.toArray(new String[vlstPrBetweenOneAndTwo.size()])));
            vlstCellOneToTwo.add(cellOneOneToTwo);
            TableCellBaseDTO cellTwoOneToTwo = new TableCellBaseDTO();
            cellTwoOneToTwo.setColor(vstrBadColor == null ? null : vstrBadColor);
            cellTwoOneToTwo.setViewValue(String.valueOf(vObj[1] == null ? "0" : vObj[1]));
            vlstCellOneToTwo.add(cellTwoOneToTwo);
//            TableCellBaseDTO cellThreeOneToTwo = new TableCellBaseDTO();
//            cellThreeOneToTwo.setColor(vstrBadColor == null ? null : vstrBadColor);
//            cellThreeOneToTwo.setViewValue(df.format(vdblPcBetweenOneAndTwo)+"%");
//            vlstCellOneToTwo.add(cellThreeOneToTwo);
            rowBetweenOneAndTwo.setRowIndex(vintRowId);
            rowBetweenOneAndTwo.setClicked(true);
            rowBetweenOneAndTwo.setCells(vlstCellOneToTwo);
            vlstRows.add(rowBetweenOneAndTwo);

            //dong nguong 3
            TableRowDTO rowBetweenTwoAndThree = new TableRowDTO();
//            if (vObj[4] != null && vObj[2] != null) {
//                vdblPcBetweenTwoAndThree = vintBetweenTwoAndThree / vintTongDonVi * 100;
//            } else {
//                vdblPcBetweenTwoAndThree = 0;
//            }
            vintRowId = vintRowId + 1;
            List<TableCellBaseDTO> vlstCellTwoToThree = new ArrayList<>();
            TableCellBaseDTO cellOneTwoToThree = new TableCellBaseDTO();
            cellOneTwoToThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            cellOneTwoToThree.setViewValue(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_TWO_AND_THREE, (String[]) vlstPrBetweenTwoAndThree.toArray(new String[vlstPrBetweenTwoAndThree.size()])));
            vlstCellTwoToThree.add(cellOneTwoToThree);
            TableCellBaseDTO cellTwoTwoToThree = new TableCellBaseDTO();
            cellTwoTwoToThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            cellTwoTwoToThree.setViewValue(String.valueOf(vObj[2] == null ? "0" : vObj[2]));
            vlstCellTwoToThree.add(cellTwoTwoToThree);
//            TableCellBaseDTO cellThreeTwoToThree = new TableCellBaseDTO();
//            cellThreeTwoToThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
//            cellThreeTwoToThree.setViewValue(df.format(vdblPcBetweenTwoAndThree)+"%");
//            vlstCellTwoToThree.add(cellThreeTwoToThree);
            rowBetweenTwoAndThree.setRowIndex(vintRowId);
            rowBetweenTwoAndThree.setClicked(true);
            rowBetweenTwoAndThree.setCells(vlstCellTwoToThree);
            vlstRows.add(rowBetweenTwoAndThree);

            //dong nguong 4
            TableRowDTO rowGreaterThanThree = new TableRowDTO();
//            if (vObj[4] != null && vObj[3] != null) {
//                vdblPcGreaterThanThree = vintGreaterThanThree / vintTongDonVi * 100;
//            } else {
//                vdblPcGreaterThanThree = 0;
//            }
            vintRowId = vintRowId + 1;
            List<TableCellBaseDTO> vlstCellGreaterThree = new ArrayList<>();
            TableCellBaseDTO cellOneGreaterThree = new TableCellBaseDTO();
            cellOneGreaterThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            cellOneGreaterThree.setViewValue(I18N.get(Constants.TOP_BY_SERVICE.GREATER_THAN_THREE, (String[]) vlstPrGreaterThanThree.toArray(new String[vlstPrGreaterThanThree.size()])));
            vlstCellGreaterThree.add(cellOneGreaterThree);
            TableCellBaseDTO cellTwoGreaterThree = new TableCellBaseDTO();
            cellTwoGreaterThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            cellTwoGreaterThree.setViewValue(String.valueOf(vObj[3] == null ? "0" : vObj[3]));
            vlstCellGreaterThree.add(cellTwoGreaterThree);
//            TableCellBaseDTO cellThreeGreaterThree = new TableCellBaseDTO();
//            cellThreeGreaterThree.setColor(vstrGoodColor == null ? null : vstrGoodColor);
//            cellThreeGreaterThree.setViewValue(df.format(vdblPcGreaterThanThree)+"%");
//            vlstCellGreaterThree.add(cellThreeGreaterThree);
            rowGreaterThanThree.setRowIndex(vintRowId);
            rowGreaterThanThree.setClicked(true);
            rowGreaterThanThree.setCells(vlstCellGreaterThree);
            vlstRows.add(rowGreaterThanThree);
        }

        //set du tieu cho tableDTO
        if (configSingleChartDTO.getDrilldown() != null) {
            tableDTO.setDrilldown(Constants.STATUS.ACTIVE == configSingleChartDTO.getDrilldown() ? true : false);
        }
        if (configSingleChartDTO.getDrillDownType() != null) {
            tableDTO.setDrilldownType(configSingleChartDTO.getDrillDownType());
        }
        if (configSingleChartDTO.getDrillDownObjectId() != null) {
            tableDTO.setDrilldownObject(Math.toIntExact(configSingleChartDTO.getDrillDownObjectId()));
        }
        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);

        return tableDTO;
    }

    /**
     * bang lv3 cua bang tong hop kenh kenh tinh
     *
     * @author VuBL
     * @since 2020/05
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     */
    @Override
    public TableDTO getTableComplThreshold(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder logBuilder = new StringBuilder();
        MultiKeyMap multiKeyMap = new MultiKeyMap();
        logBuilder.append("*LV3 BANG TONG HOP");
        long startTime = System.currentTimeMillis();
        long stepTime = System.currentTimeMillis();

        TableDTO tableDTO = new TableDTO();
        List<TableColumnDTO> vlstColumns = new ArrayList<>();
        List<TableRowDTO> vlstRows = new ArrayList<>();
        logBuilder.append(" - BAT DAU QUERY " + (System.currentTimeMillis() - stepTime) + "ms");
        List<Object[]> vlstObj = contentTopRepoCustom.getTableComplThreshold(dashboardRequestDTO, configSingleChartDTO);
        logBuilder.append(" - QUERY XONG " + (System.currentTimeMillis() - stepTime) + "ms");
        logBuilder.append(" - BAT DAU PHAN TICH " + (System.currentTimeMillis() - stepTime) + "ms");
        int vintCycleId = dashboardRequestDTO.getCycleId();
        List<String> vlstTitle = new ArrayList<>();
        String vstrPRow = dashboardRequestDTO.getpRow();
        List<String> vlstParamTitleChart = Lists.newArrayList();
        String vstrShopTitle = partnerRepo.getNameByShopAndChannel(dashboardRequestDTO.getObjectCode());
        int vintColumnIdForColumn = 0;
        int vintColumnIdForRow = 0;
        List<ApParamDTO> vlstApParam = apParamService.findByType(Constants.TOP_BY_SERVICE.COMPLETION_RATE);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("params");
        JsonObject queryParam;
        String vstrColor = null;
        int vintMucMot = 0;
        int vintMucHai = 0;
        int vintMucBa = 0;

        //lay ra 3 muc tu cau hinh
        for (ApParamDTO ap : vlstApParam) {
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.ONE)) {
                vintMucMot = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.TWO)) {
                vintMucHai = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.THREE)) {
                vintMucBa = Integer.parseInt(ap.getValue());
            }
        }

        //set title chart
        vlstParamTitleChart.add(vstrShopTitle);
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ZERO)) {
            vlstParamTitleChart.add(String.valueOf(vintMucMot));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWZERO, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ONE)) {
            vlstParamTitleChart.add(String.valueOf(vintMucMot));
            vlstParamTitleChart.add(String.valueOf(vintMucHai));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWONE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.TWO)) {
            vlstParamTitleChart.add(String.valueOf(vintMucHai));
            vlstParamTitleChart.add(String.valueOf(vintMucBa));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWTWO, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }
        if (vstrPRow.equals(Constants.TOP_BY_SERVICE.THREE)) {
            vlstParamTitleChart.add(String.valueOf(vintMucBa));
            vlstParamTitleChart.add(DataUtil.convertLocalDateToString(dashboardRequestDTO.getPrdId(), dashboardRequestDTO.getCycleId()));
            tableDTO.setTitle(I18N.get(Constants.TOP_BY_SERVICE.CHART_PROWTHREE, (String[]) vlstParamTitleChart.toArray(new String[vlstParamTitleChart.size()])));
        }


        //lay mau cot ty le hoan thanh
        if (!DataUtil.isNullObject(varrLines.get(0))) {
            queryParam = (JsonObject) varrLines.get(0);
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.ZERO) || vstrPRow.equals(Constants.TOP_BY_SERVICE.ONE)) {
                vstrColor = queryParam.get("bad").getAsString();
            }
            if (vstrPRow.equals(Constants.TOP_BY_SERVICE.TWO) || vstrPRow.equals(Constants.TOP_BY_SERVICE.THREE)) {
                vstrColor = queryParam.get("good").getAsString();
            }
        }

        //lay ra cac title
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_UNIT));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_SCHEDULE_DAY));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_PERFORM_DAY));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_COMPLETION_RATE));
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_UNIT));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_SCHEDULE_MONTH));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_PERFORM_MONTH));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_COMPLETION_RATE));
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_UNIT));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_SCHEDULE_QUARTER));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_PERFORM_QUARTER));
            vlstTitle.add(I18N.get(Constants.TOP_BY_SERVICE.TITLE_COMPLETION_RATE));
        }

        //set cot
        TableColumnDTO columnUnit = new TableColumnDTO();
        columnUnit.setType(Constants.CODE_WARNING.TEXT);
        columnUnit.setAlign(Constants.CODE_WARNING.LEFT);
        columnUnit.setColumnId(vintColumnIdForColumn);
        columnUnit.setName(vlstTitle.get(0));
        vlstColumns.add(columnUnit);
        vlstTitle.remove(0);
        for (String s : vlstTitle) {
            vintColumnIdForColumn = vintColumnIdForColumn + 1;
            TableColumnDTO column = new TableColumnDTO();
            column.setType(Constants.CODE_WARNING.TEXT);
            column.setAlign(Constants.CODE_WARNING.RIGHT);
            column.setColumnId(vintColumnIdForColumn);
            column.setName(s);
            vlstColumns.add(column);
        }

        //set dong
        if (vlstObj != null && !vlstObj.isEmpty()) {
            int vintRowIndex = 0;
            for (Object[] obj : vlstObj) {
                TableRowDTO row = new TableRowDTO();
                List<TableCellBaseDTO> vlstCell = new ArrayList<>();

                TableCellBaseDTO cellUnit = new TableCellBaseDTO();
                cellUnit.setColor(Constants.COLORS.BLACK);
                cellUnit.setColumnId(vintColumnIdForRow);
                cellUnit.setViewValue((String) obj[0]);
                vlstCell.add(cellUnit);

                TableCellBaseDTO cellSchedule = new TableCellBaseDTO();
                cellSchedule.setColor(Constants.COLORS.BLACK);
                cellSchedule.setColumnId(vintColumnIdForRow + 1);
                cellSchedule.setViewValue(df.format(obj[2]));
                vlstCell.add(cellSchedule);

                TableCellBaseDTO cellPerform = new TableCellBaseDTO();
                cellPerform.setColor(Constants.COLORS.BLACK);
                cellPerform.setColumnId(vintColumnIdForRow + 2);
                cellPerform.setViewValue(df.format(obj[1]));
                vlstCell.add(cellPerform);

                TableCellBaseDTO cellRate = new TableCellBaseDTO();
                cellRate.setColor(vstrColor);
                cellRate.setColumnId(vintColumnIdForRow + 3);
                cellRate.setViewValue(String.valueOf(obj[3]+"%"));
                vlstCell.add(cellRate);

                row.setCells(vlstCell);
                row.setRowIndex(vintRowIndex);
                vintRowIndex = vintRowIndex + 1;
                vlstRows.add(row);
            }
        }

        tableDTO.setColumns(vlstColumns);
        tableDTO.setRows(vlstRows);

        logBuilder.append(" - PHAN TICH XONG " + (System.currentTimeMillis() - stepTime) + "ms");
        logBuilder.append(" - TONG THOI GIAN " + ((System.currentTimeMillis() - startTime)) + "ms");
        LOGGER.info(logBuilder.toString());

        return tableDTO;
    }

    /**
     * diem danh gia don vi/nhan vien lv1
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public List<TopByServiceDTO> getEvaluatePoint(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        List<TopByServiceDTO> vlstData = new ArrayList<>();
        List<ApParamDTO> vlstApParam = apParamService.findByType(Constants.TOP_BY_SERVICE.EVALUATED_RATE);
        Object[] vObj = contentTopRepoCustom.getEvaluatePoint(dashboardRequestDTO, configSingleChartDTO);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getMetaData());
        JsonArray varrLines = (JsonArray) jsonObject.get("params");
        JsonObject queryParam;
        String vstrBadColor = null;
        String vstrGoodColor = null;
        int vintMucMot = 0;
        int vintMucHai = 0;
        int vintMucBa = 0;
        int vintMucBon = 0;

        //lay ra so thang lien tiep de tra ve cho lv2 nhan biet
        String vstrMonth = null;
        JsonParser jsonParserMonth = new JsonParser();
        JsonObject jsonObjectMonth = (JsonObject) jsonParserMonth.parse(configSingleChartDTO.getQueryParam());
        JsonArray paramsMonth = null;
        JsonObject queryParamMonth;
        if (!DataUtil.isNullObject(jsonObjectMonth))
            paramsMonth = (JsonArray) jsonObjectMonth.get("params");
        if (paramsMonth != null) {
            if (null != paramsMonth.get(0)) {
                queryParamMonth = (JsonObject) paramsMonth.get(0);
                vstrMonth = queryParamMonth.get("month").getAsString();
            }
        }

        //lay mau
        if (!DataUtil.isNullObject(varrLines.get(0))) {
            queryParam = (JsonObject) varrLines.get(0);
            vstrBadColor = queryParam.get("bad").getAsString();
            vstrGoodColor = queryParam.get("good").getAsString();
        }

        //lay ra 4 muc tu cau hinh
        for (ApParamDTO ap : vlstApParam) {
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.ONE)) {
                vintMucMot = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.TWO)) {
                vintMucHai = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.THREE)) {
                vintMucBa = Integer.parseInt(ap.getValue());
            }
            if (ap.getCode().trim().equals(Constants.TOP_BY_SERVICE.FOUR)) {
                vintMucBon = Integer.parseInt(ap.getValue());
            }
        }

        if (vObj != null) {
            List<String> vlstPrLessThanOne = Lists.newArrayList();
            List<String> vlstPrBetweenOneAndTwo = Lists.newArrayList();
            List<String> vlstPrBetweenTwoAndThree = Lists.newArrayList();
            List<String> vlstPrBetweenThreeAndFour = Lists.newArrayList();
            List<String> vlstPrGreaterThanFour = Lists.newArrayList();
            vlstPrLessThanOne.add(String.valueOf(vintMucMot));
            vlstPrBetweenOneAndTwo.add(String.valueOf(vintMucMot));
            vlstPrBetweenOneAndTwo.add(String.valueOf(vintMucHai));
            vlstPrBetweenTwoAndThree.add(String.valueOf(vintMucHai));
            vlstPrBetweenTwoAndThree.add(String.valueOf(vintMucBa));
            vlstPrBetweenThreeAndFour.add(String.valueOf(vintMucBa));
            vlstPrBetweenThreeAndFour.add(String.valueOf(vintMucBon));
            vlstPrGreaterThanFour.add(String.valueOf(vintMucBon));

            //set data nho hon muc 1
            TopByServiceDTO dataLessThanOne = new TopByServiceDTO();
            dataLessThanOne.setQuantity(String.valueOf(vObj[0] == null ? "0" : vObj[0]));
            dataLessThanOne.setColor(vstrBadColor == null ? null : vstrBadColor);
            dataLessThanOne.setMonth(vstrMonth == null ? null : vstrMonth);
            dataLessThanOne.setDescription(I18N.get(Constants.TOP_BY_SERVICE.LESS_THAN_ONE, (String[]) vlstPrLessThanOne.toArray(new String[vlstPrLessThanOne.size()])));
            vlstData.add(dataLessThanOne);

            //set data giua 1 va 2
            TopByServiceDTO dataBetweenOneAndTwo = new TopByServiceDTO();
            dataBetweenOneAndTwo.setQuantity(String.valueOf(vObj[1] == null ? "0" : vObj[1]));
//            dataBetweenOneAndTwo.setColor(vstrBadColor == null ? null : vstrBadColor);
            dataBetweenOneAndTwo.setMonth(vstrMonth == null ? null : vstrMonth);
            dataBetweenOneAndTwo.setDescription(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_ONE_AND_TWO, (String[]) vlstPrBetweenOneAndTwo.toArray(new String[vlstPrBetweenOneAndTwo.size()])));
            vlstData.add(dataBetweenOneAndTwo);

            //set data giua 2 va 3
            TopByServiceDTO dataBetweenTwoAndThree = new TopByServiceDTO();
            dataBetweenTwoAndThree.setQuantity(String.valueOf(vObj[2] == null ? "0" : vObj[2]));
//            dataBetweenTwoAndThree.setColor(vstrBadColor == null ? null : vstrBadColor);
            dataBetweenTwoAndThree.setMonth(vstrMonth == null ? null : vstrMonth);
            dataBetweenTwoAndThree.setDescription(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_TWO_AND_THREE, (String[]) vlstPrBetweenTwoAndThree.toArray(new String[vlstPrBetweenTwoAndThree.size()])));
            vlstData.add(dataBetweenTwoAndThree);

            //set data giu 3 va 4
            TopByServiceDTO dataBetweenThreeAndFour = new TopByServiceDTO();
            dataBetweenThreeAndFour.setQuantity(String.valueOf(vObj[3] == null ? "0" : vObj[3]));
            dataBetweenThreeAndFour.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            dataBetweenThreeAndFour.setMonth(vstrMonth == null ? null : vstrMonth);
            dataBetweenThreeAndFour.setDescription(I18N.get(Constants.TOP_BY_SERVICE.BETWEEN_THREE_AND_FOUR, (String[]) vlstPrBetweenThreeAndFour.toArray(new String[vlstPrBetweenThreeAndFour.size()])));
            vlstData.add(dataBetweenThreeAndFour);

            //set data lon hon 4
            TopByServiceDTO dataGreaterThanFour = new TopByServiceDTO();
            dataGreaterThanFour.setQuantity(String.valueOf(vObj[4] == null ? "0" : vObj[4]));
            dataGreaterThanFour.setColor(vstrGoodColor == null ? null : vstrGoodColor);
            dataGreaterThanFour.setMonth(vstrMonth == null ? null : vstrMonth);
            dataGreaterThanFour.setDescription(I18N.get(Constants.TOP_BY_SERVICE.GREATER_THAN_FOUR, (String[]) vlstPrGreaterThanFour.toArray(new String[vlstPrGreaterThanFour.size()])));
            vlstData.add(dataGreaterThanFour);
        }

        return vlstData;
    }

}
