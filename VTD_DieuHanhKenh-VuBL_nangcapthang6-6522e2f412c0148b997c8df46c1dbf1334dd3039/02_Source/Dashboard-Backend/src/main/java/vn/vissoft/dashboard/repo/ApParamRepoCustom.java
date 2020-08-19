package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.ApParam;

import java.util.List;

public interface ApParamRepoCustom {
    String getServiceWarningColor(String pstrCode) throws Exception;

    String getCompletedColor(String pstrCode) throws Exception;

    List<String> getWarningConfig() throws Exception;

    boolean checkExistedWarningCode(String pintCode)  throws Exception;

    List<ApParam> findByType(String pstrType) throws Exception;

    List<String> findCodeByType(String pstrType) throws Exception;
}
