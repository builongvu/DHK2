package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.VdsGroupChannel;

import java.util.List;

public interface ChannelRepoCustom {

    List<String> findAllChannelCode() throws Exception;

    boolean checkExistedChannelCode(String pstrCode) throws Exception;

    List<VdsGroupChannel> findActiveChannel() throws Exception;

    List<VdsGroupChannel> findActiveGroupChannel() throws Exception;

    boolean checkExistedGroupChannelCode(String pstrCode) throws Exception;

    VdsGroupChannel findChannelByShopCode(String pstrShopCode) throws Exception;

    List<VdsGroupChannel> findByStatusAndNotExists() throws Exception;

    String findNameByCode(String pstrVdsChannelCode) throws Exception;

}
