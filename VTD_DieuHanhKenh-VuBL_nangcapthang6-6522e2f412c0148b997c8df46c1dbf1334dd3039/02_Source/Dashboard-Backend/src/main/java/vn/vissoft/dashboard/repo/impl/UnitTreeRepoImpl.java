package vn.vissoft.dashboard.repo.impl;

import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.UnitTreeDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.UnitTreeRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UnitTreeRepoImpl implements UnitTreeRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay ra tat ca danh sach nhan vien(staff) va đon vi(unit) (combobox trong giao cac cap)
     *
     * @author VuBL
     * @since 2019/09
     * @return List<UnitTreeDTO>
     * @throws Exception
     */
    @Override
    public List<UnitTreeDTO> findAllUnitTrees() throws Exception {
        List<UnitTreeDTO> vlstData = null;
        StringBuilder vsbdSql = new StringBuilder("(SELECT s.staff_code, name, su.unit_id, 'staff' FROM staff s ")
                                          .append("JOIN staff_unit su ON s.staff_id = su.staff_id ")
                                          .append("WHERE s.status = '1')")
                                          .append("UNION ")
                                          .append("(SELECT unit_id, name, parent_id, 'unit' FROM unit u ")
                                          .append("WHERE parent_id is not null ")
                                          .append("AND status = '1') ")
                                          .append(" ORDER BY name collate utf8_vietnamese_ci");
        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        List<Object[]> vlst = query.getResultList();
        if(!DataUtil.isNullOrEmpty(vlst)) {
            vlstData = new ArrayList<>();
            for (Object[] obj : vlst) {
                UnitTreeDTO data = new UnitTreeDTO();
                data.setObjectId(DataUtil.safeToLong(obj[0]));
                data.setObjectName(DataUtil.safeToString(obj[1]));
                data.setParentId(DataUtil.safeToLong(obj[2]));
                data.setObjectType(DataUtil.safeToString(obj[3]));
                vlstData.add(data);
            }
        }
        return vlstData;
    }

}
