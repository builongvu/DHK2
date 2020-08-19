package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Product;
import vn.vissoft.dashboard.repo.ProductRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ProductRepoImpl implements ProductRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
//    private BaseMapper<Product, ProductDTO> mapper = new BaseMapper<>();

    /**
     * Tim nhung san pham dang hoat dong
     *
     * @return
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public List<Product> findActiveProduct() {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select p from Product p where p.mstrStatus='1' order by case when p.mstrName like 'đ%' then 'd' else p.mstrName end");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra ton tai san pham trong csdl
     *
     * @param pstrCode
     * @return
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedProductCode(String pstrCode) {
        boolean vblnCheck = false;
        for (Product product : findActiveProduct()) {
            if (pstrCode.equalsIgnoreCase(product.getMstrCode())) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    /**
     * tim kiem id san pham theo ma
     *
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public Long findIdByCode(String pstrCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select p.mlngId from Product p where p.mstrCode =:code");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("code", pstrCode);
        List<Long> vlstIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstIds)) return null;
        return vlstIds.get(0);
    }
}
