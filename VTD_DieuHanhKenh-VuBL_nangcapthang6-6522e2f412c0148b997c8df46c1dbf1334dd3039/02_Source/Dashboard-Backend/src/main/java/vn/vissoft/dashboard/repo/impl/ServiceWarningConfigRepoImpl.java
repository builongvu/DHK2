package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ServiceWarningConfig;
import vn.vissoft.dashboard.repo.ServiceWarningRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ServiceWarningConfigRepoImpl implements ServiceWarningRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay ra muc canh bao cua chi tieu
     *
     * @param pstrChannelCode
     * @param plngServiceId
     * @return
     * @throws Exception
     * @author DatNT
     * @since 07/11/2019
     */
    @Override
    public List<ServiceWarningConfig> getWarningLevel(String pstrChannelCode, Long plngServiceId) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        List<ServiceWarningConfig> vlstWarningConfigs = null;
        vsbdBuilder.append("select * from service_warning_config where ifnull(service_id,0) =ifnull(:serviceId,0) and ifnull(vds_channel_code,'a')=ifnull(:vdsChannelCode,'a')");
        Query query = entityManager.createNativeQuery(vsbdBuilder.toString());
        query.setParameter("serviceId", plngServiceId);
        query.setParameter("vdsChannelCode", pstrChannelCode);

        List<Object[]> vlst = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstWarningConfigs = new ArrayList<>();
            for (int i = 0; i < vlst.size(); i++) {
                ServiceWarningConfig data = new ServiceWarningConfig();
                data.setId(DataUtil.safeToLong(vlst.get(i)[0]));
                data.setServiceId(DataUtil.safeToLong(vlst.get(i)[1]));
                data.setVdsChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                data.setWaningLevel(DataUtil.safeToInt(vlst.get(i)[3]));
                data.setStatus(DataUtil.safeToString(vlst.get(i)[4]));
                data.setFromValue(DataUtil.safeToDouble(vlst.get(i)[5]));
                data.setToValue(DataUtil.safeToDouble(vlst.get(i)[6]));
                data.setExp(DataUtil.safeToString(vlst.get(i)[7]));

                vlstWarningConfigs.add(data);
            }
        }

        return vlstWarningConfigs;
    }
}
