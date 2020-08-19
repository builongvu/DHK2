package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.Shop;

import java.util.List;

public interface ShopRepoCustom {

    List<Shop> findByStatusAndOrder() throws Exception;

}
