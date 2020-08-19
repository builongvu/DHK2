package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.Shop;

import java.util.List;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Long>, ShopRepoCustom {

    List<Shop> getByStatusEquals(String status);

}
