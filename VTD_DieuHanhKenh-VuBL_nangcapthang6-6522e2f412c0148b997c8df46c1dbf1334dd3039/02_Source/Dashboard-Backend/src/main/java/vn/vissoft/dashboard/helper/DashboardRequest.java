package vn.vissoft.dashboard.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.repo.ConfigGroupCardRepo;
import vn.vissoft.dashboard.repo.PartnerRepo;

import java.util.Date;
import java.util.HashMap;

@Component
public class DashboardRequest {
    private static final Logger LOGGER = LogManager.getLogger(DashboardRequest.class);

    /**
     * dieu kien tong hop bang du lieu chi tieu
     *
     * @param dashboardRequestDTO
     * @param phmpParams
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 04/11/2019
     */
    public StringBuilder buildSummarySQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams, String pstrQueryParam) throws Exception {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        JsonParser jsonParser = new JsonParser();

        if (!DataUtil.isNullOrEmpty(pstrQueryParam)) {
            vsbdStringBuilder.append(" where 1=1 and csmd.service_id = se.id ");

            vsbdStringBuilder.append(" and (csmd.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) ");
            phmpParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());

            vsbdStringBuilder.append(" and staff_code is null");

            JsonObject jsonObject = (JsonObject) jsonParser.parse(pstrQueryParam);
            JsonArray params = null;
            String vstrShopType;
            JsonObject queryParam;
            if (!DataUtil.isNullObject(jsonObject))
                params = (JsonArray) jsonObject.get("params");
            if (params != null) {
                if (null != params.get(0)) {
                    queryParam = (JsonObject) params.get(0);
                    vstrShopType = queryParam.get("shop").getAsString();
                    if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                        if (Constants.SHOP_PARAM.equalsIgnoreCase(vstrShopType)) {
                            vsbdStringBuilder.append(" and shop_code =:shopCode ");
                            phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
                        }
////                        vsbdStringBuilder.append(" and shop_code =:shopCode ");
//                        phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());

                    }

                }
            }
        }

        if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
            Long vlngPrdId = 0L;
            if (dashboardRequestDTO.getCycleId() != 0) {
                if (dashboardRequestDTO.getCycleId() == 1)
                    vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());   //lay prdId theo ngay
                else if (dashboardRequestDTO.getCycleId() == 2)
                    vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());   //lay prdId theo thang
                else if (dashboardRequestDTO.getCycleId() == 3)
                    vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());       //lay prdId theo quy
                vsbdStringBuilder.append(" and prd_id=:prdId");
                phmpParams.put("prdId", vlngPrdId);

            }
        }
        LOGGER.debug("SQL Params : " + phmpParams);
        return vsbdStringBuilder;
    }

    public StringBuilder buildDetailSummarySQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams, String pstrQueryParam) throws Exception {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        JsonParser jsonParser = new JsonParser();

        if (!DataUtil.isNullOrEmpty(pstrQueryParam)) {
            vsbdStringBuilder.append(" and (m.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) ");
            phmpParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
            vsbdStringBuilder.append(" and staff_code is null ");

            JsonObject jsonObject = (JsonObject) jsonParser.parse(pstrQueryParam);
            JsonArray params = null;
            String vstrServiceType;
            String vstrShopType;
            JsonObject queryParam;
            if (!DataUtil.isNullObject(jsonObject))
                params = (JsonArray) jsonObject.get("params");
            if (params != null) {
                if (null != params.get(0)) {
                    queryParam = (JsonObject) params.get(0);
                    vstrServiceType = queryParam.get("service").getAsString();
                    vstrShopType = queryParam.get("shop").getAsString();
                    if (!DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()) && DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {

                        if (Constants.SERVICE_PARAM.equalsIgnoreCase(vstrServiceType)) {
                            vsbdStringBuilder.append(" and csmd.service_id =:serviceId");
                            phmpParams.put("serviceId", dashboardRequestDTO.getServiceId());
                        } else if (Constants.CHILD_SERVICE_PARAM.equalsIgnoreCase(vstrServiceType)) {
                            vsbdStringBuilder.append(" and csmd.service_id in (select id from service where parent_id =:parentId)");
                            phmpParams.put("parentId", dashboardRequestDTO.getServiceId());
                        }
                        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                            vsbdStringBuilder.append(" and ifnull(m.parent_shop_code, 'a') = ifnull(:parentShopCode, 'a') ");
                            phmpParams.put("parentShopCode", dashboardRequestDTO.getObjectCode());
                        }
                    }

                    if (!DataUtil.isNullOrZero(dashboardRequestDTO.getServiceId()) && !DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
                        if (Constants.SHOP_PARAM.equalsIgnoreCase(vstrShopType)) {
                            vsbdStringBuilder.append(" and csmd.shop_code = :shopCode");
                            phmpParams.put("shopCode", dashboardRequestDTO.getParentShopCode());
                        } else if (Constants.CHILD_SHOP_PARAM.equalsIgnoreCase(vstrShopType)) {
                            vsbdStringBuilder.append(" and ifnull(m.parent_shop_code, 'a') = ifnull(:parentShopCode, 'a') ");
                            phmpParams.put("parentShopCode", dashboardRequestDTO.getParentShopCode());
                        }
                        vsbdStringBuilder.append(" and csmd.service_id =:serviceId");
                        phmpParams.put("serviceId", dashboardRequestDTO.getServiceId());
                    }
                }
            }
            if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
                Long vlngPrdId = 0L;
                if (dashboardRequestDTO.getCycleId() != 0) {
                    if (dashboardRequestDTO.getCycleId() == 1)
                        vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());   //lay prdId theo ngay
                    else if (dashboardRequestDTO.getCycleId() == 2)
                        vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());   //lay prdId theo thang
                    else if (dashboardRequestDTO.getCycleId() == 3)
                        vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());       //lay prdId theo quy
                    vsbdStringBuilder.append(" and prd_id=:prdId");
                    phmpParams.put("prdId", vlngPrdId);

                }
            }
        }

        LOGGER.debug("SQL Params : " + phmpParams);
        return vsbdStringBuilder;
    }

}
