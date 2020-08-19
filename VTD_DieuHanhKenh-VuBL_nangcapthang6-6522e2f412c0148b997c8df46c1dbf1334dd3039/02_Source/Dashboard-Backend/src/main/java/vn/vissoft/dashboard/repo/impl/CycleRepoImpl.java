package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.model.Cycle;
import vn.vissoft.dashboard.repo.CycleRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class CycleRepoImpl implements CycleRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Cycle> findByAssign(int pintAssign) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        vsbdBuilder.append("select c from Cycle c where assign = :assign");
        Query query = entityManager.createQuery(vsbdBuilder.toString());
        query.setParameter("assign", pintAssign);

        return query.getResultList();
    }


    /**
     * kiem tra channel code da ton tai(true:la da ton tai,false: la k ton tai)
     *
     * @author DatNT
     * @version 1.0
     * @since 24/09/2019
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkExistedCycleCode(String pstrCode)  throws Exception{

        boolean vblnCheck = false;
        List<Cycle> listActive = findByAssign(1);
        if(listActive!=null)
        {
            for (Cycle cycle : listActive) {
                if (pstrCode.equalsIgnoreCase(cycle.getCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }
}
