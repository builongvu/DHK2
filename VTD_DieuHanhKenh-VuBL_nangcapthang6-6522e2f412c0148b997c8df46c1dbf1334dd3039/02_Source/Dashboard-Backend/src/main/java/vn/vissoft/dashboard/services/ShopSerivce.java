package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.Shop;
import vn.vissoft.dashboard.model.ShopUnit;

import java.util.List;

public interface ShopSerivce {

    List<Shop> getAllShop(String status);

    List<Shop> getAllShops();

    List<Shop> getByStatusAndOrder() throws Exception;
}
