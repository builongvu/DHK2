package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ManageInfoPartner;


import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepo extends JpaRepository<ManageInfoPartner, Long> , PartnerRepoCustom {

    List<ManageInfoPartner> findByParentShopCode(String parentShopCode);

    Optional<ManageInfoPartner> getByShopCode(String shopcode);

//    List<ManageInfoPartner> findByShopCodeOrParentShopCode(String pstrShopCode, String pstrParentCode) throws Exception;

}
