package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Position;
import vn.vissoft.dashboard.repo.PositionRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class PositionRepoImpl implements PositionRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Position> findAllPosition() throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select p from Position p ");
        vsbdBuilder.append("order by case when p.name like 'đ%' then 'd' else p.name end");
        Query query = entityManager.createQuery(vsbdBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra ma chuc danh co ton tai, hoat dong hay khong
     *
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkExistedPositionCode(String pstrCode) throws Exception {
        boolean vblnCheck = false;
        List<Position> listActive = findAllPosition();
        if (listActive != null) {
            for (Position position : listActive) {
                if (pstrCode.equalsIgnoreCase(position.getCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    /**
     * tim id theo ma chuc danh
     *
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public Long findIdByCode(String pstrCode) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select id from Position where code =:code");
        Query query = entityManager.createQuery(vsbdBuilder.toString());
        if (!DataUtil.isNullOrEmpty(pstrCode))
            query.setParameter("code", pstrCode);
        Long vlngId = (Long) query.getSingleResult();
        if (!DataUtil.isNullOrZero(vlngId))
            return vlngId;
        else return null;
    }

    /**
     * tim ten theo ma chuc danh
     *
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public String findNameByCode(String pstrCode) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select name from Position where code =:code");
        Query query = entityManager.createQuery(vsbdBuilder.toString());
        if (!DataUtil.isNullOrEmpty(pstrCode))
            query.setParameter("code", pstrCode);
        String vstrName = (String) query.getSingleResult();
        if (!DataUtil.isNullOrEmpty(vstrName))
            return vstrName;
        else return null;
    }
}

