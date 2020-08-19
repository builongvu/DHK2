package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.model.Shop;
import vn.vissoft.dashboard.repo.ShopRepo;
import vn.vissoft.dashboard.services.ShopSerivce;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ShopServiceImpl implements ShopSerivce {

    @Autowired
    private ShopRepo shopRepo;

    @Override
    public List<Shop> getAllShop(String status) {
        return shopRepo.getByStatusEquals(status);
    }

    @Override
    public List<Shop> getAllShops() {
        return shopRepo.findAll();
    }

    @Override
    public List<Shop> getByStatusAndOrder() throws Exception {
        return shopRepo.findByStatusAndOrder();
    }
}
