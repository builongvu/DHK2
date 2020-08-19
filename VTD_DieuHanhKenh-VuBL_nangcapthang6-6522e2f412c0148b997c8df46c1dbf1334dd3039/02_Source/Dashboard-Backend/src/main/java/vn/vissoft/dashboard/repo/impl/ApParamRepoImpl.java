package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ApParam;
import vn.vissoft.dashboard.repo.ApParamRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ApParamRepoImpl implements ApParamRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay ra mau canh bao chi tieu
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 07/11/2019
     */
    @Override
    public String getServiceWarningColor(String pstrCode) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select value from ApParam where type ='WARNING_LEVEL' and code = :code");
        Query query = entityManager.createQuery(vsbdBuilder.toString());
        if (!DataUtil.isNullOrEmpty(pstrCode))
            query.setParameter("code", pstrCode.trim());

        List<String> vlstColors = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstColors)) return null;

        return vlstColors.get(0);
    }

    /**
     * lay ra mau xac dinh chi tieu hoan thanh hay chua
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 07/11/2019
     */
    @Override
    public String getCompletedColor(String pstrCode) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select value from ApParam where type ='COMPLETED_COLOUR' and code = :code");
        Query query = entityManager.createQuery(vsbdBuilder.toString());
        if (!DataUtil.isNullOrEmpty(pstrCode))
            query.setParameter("code", pstrCode.trim());

        List<String> vlstColors = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstColors)) return null;

        return vlstColors.get(0);
    }

    /**
     * lay ra cac code muc canh bao trong ap param
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getWarningConfig() throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select code from ApParam where type ='WARNING_LEVEL'");
        Query query = entityManager.createQuery(vsbdBuilder.toString());

        List<String> vlstCodes = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstCodes)) return null;

        return vlstCodes;
    }

    /**
     * kiem tra  code canh bao da ton tai(true:la da ton tai,false: la k ton tai)
     *
     * @param pintCode
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 24/09/2019
     */
    @Override
    public boolean checkExistedWarningCode(String pintCode) throws Exception {

        boolean vblnCheck = false;
        List<String> listActive = getWarningConfig();
        if (listActive != null) {
            for (String code : listActive) {
                if (pintCode.equals(code.trim())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    @Override
    public List<ApParam> findByType(String pstrType) throws Exception {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select ap from ApParam ap where type =:type and status ='1' order by case when ap.name like 'đ%' then 'd' else ap.name end");
        Query query=entityManager.createQuery(stringBuilder.toString());
        query.setParameter("type",pstrType.trim());
        return query.getResultList();
    }

    @Override
    public List<String> findCodeByType(String pstrType) throws Exception {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select a.code from ApParam a where type = :type and status ='1'");
        Query query=entityManager.createQuery(stringBuilder.toString());
        query.setParameter("type",pstrType);

        return query.getResultList();
    }
}
