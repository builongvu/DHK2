package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.VttGroupChannelDTO;
import vn.vissoft.dashboard.model.VttGroupChannel;

import java.math.BigInteger;
import java.util.List;

public interface VttGroupChannelRepoCustom {
    List<VttGroupChannel> findActiveVttGroupChannel() throws Exception;

    boolean checkExistedGroupChannelCode(String pstrCode) throws Exception;

    List<Object[]> findByCondition(VttGroupChannelDTO vttGroupChannel) throws Exception;

}
