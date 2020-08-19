package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ShopUnit;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ShopUnitRepo extends ShopUnitRepoCustom, JpaRepository<ShopUnit, Long> {

    Optional<ShopUnit> getByMlngId(Long id);

    BigInteger countAllByMlngServiceIdAndMstrShopCodeAndMlngIdNotLike(Long serviceId, String shopCode, Long id);

    Optional<ShopUnit> getByMlngServiceIdAndMstrShopCodeAndMlngIdNotLike(Long serviceId, String shopCode, Long id);
}
