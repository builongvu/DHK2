package vn.vissoft.dashboard.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.model.ApParam;


import java.util.List;

@Repository
public interface ApParamRepo extends JpaRepository<ApParam,Long>,ApParamRepoCustom {

     List<ApParam> findAllByTypeAndStatusOrderByName(String pstrType, String pstrStatus);

     List<ApParam> findAllByType(String pstrType);

     ApParam getByCode(String pstrCode) throws Exception;

     ApParam getByCodeAndType(String pstrType, String pstrCode) throws Exception;

     List<ApParam> getByType(String type) throws Exception;
}
