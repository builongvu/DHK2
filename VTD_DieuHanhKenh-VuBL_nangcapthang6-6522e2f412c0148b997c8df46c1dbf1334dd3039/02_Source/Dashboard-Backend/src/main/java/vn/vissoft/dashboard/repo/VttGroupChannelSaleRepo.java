package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.VttGroupChannelSale;

@Repository
public interface VttGroupChannelSaleRepo extends JpaRepository<VttGroupChannelSale,Long>,VttGroupChannelSaleRepoCustom {
    VttGroupChannelSale findByGroupChannelCodeAndChannelTypeId(String pstrGroupChannelCode,Long pstrChannelTypeId) throws Exception;
}
