package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.VttPosition;

@Repository
public interface VttPositionRepo extends JpaRepository<VttPosition,Long>,VttPositionRepoCustom {

    VttPosition findByGroupChannelCodeAndPositionCode(String pstrGroupChannelCode,String pstrPositionCode) throws Exception;
}
