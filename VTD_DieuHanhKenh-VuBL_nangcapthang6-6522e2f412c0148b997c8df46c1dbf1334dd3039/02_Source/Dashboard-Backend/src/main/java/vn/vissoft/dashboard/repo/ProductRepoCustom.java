package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.Product;

import java.util.List;

public interface ProductRepoCustom {

    List<Product> findActiveProduct();
    boolean checkExistedProductCode(String pstrCode);

    Long findIdByCode(String pstrCode) throws Exception;
}
