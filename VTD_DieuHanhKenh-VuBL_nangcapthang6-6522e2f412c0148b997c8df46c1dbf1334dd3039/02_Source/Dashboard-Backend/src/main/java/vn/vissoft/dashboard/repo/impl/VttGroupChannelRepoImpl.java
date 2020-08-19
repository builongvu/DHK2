package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.dto.VttGroupChannelDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.VttGroupChannel;
import vn.vissoft.dashboard.repo.VttGroupChannelRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

public class VttGroupChannelRepoImpl implements VttGroupChannelRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * tim vtt group channel con hoat dong
     * @return
     * @throws Exception
     */
    @Override
    public List<VttGroupChannel> findActiveVttGroupChannel() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select c from VttGroupChannel c ")
                .append("where c.status='1' ")
                .append("order by case when c.groupChannelName like 'đ%' then 'd' else c.groupChannelName end");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra su ton tai cua group channel code
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkExistedGroupChannelCode(String pstrCode) throws Exception {
        boolean vblnCheck = false;
        List<VttGroupChannel> listActive = findActiveVttGroupChannel();
        if (listActive != null) {
            for (VttGroupChannel channel : listActive) {
                if (pstrCode.equalsIgnoreCase(channel.getGroupChannelCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    /**
     * tim kiem vtt group channl theo dieu kien
     * @author DatNT
     * @since 30/12/2019
     * @param vttGroupChannel
     * @return
     * @throws Exception
     */
    @Override
    public List<Object[]> findByCondition(VttGroupChannelDTO vttGroupChannel) throws Exception {
        StringBuilder vsbdBuilder = new StringBuilder();
        HashMap vhmpHashMap = new HashMap();
        int vintFirstResult = 0;
        int vintMaxResult = 0;
        if (vttGroupChannel.getPager() != null) {
            vintFirstResult = (vttGroupChannel.getPager().getPage() - 1) * vttGroupChannel.getPager().getPageSize();
            vintMaxResult = vttGroupChannel.getPager().getPageSize();
        }
        vsbdBuilder.append("select * from (");
        vsbdBuilder.append("select vgc.group_channel_code,vgc.group_channel_name,'Theo chức danh nhân sự',p.position_id positionId,")
                .append(" vp.position_code positionCode,p.position_name positionName, null ChannelTypeId,vgc.group_status  ");
        vsbdBuilder.append("from vtt_group_channel vgc,vtt_position vp,position p ");
        vsbdBuilder.append(" where vgc.group_channel_code=vp.group_channel_code and vp.position_code=p.position_code");
        vsbdBuilder.append(" union all ");
        vsbdBuilder.append("select vgc.group_channel_code,vgc.group_channel_name,'Theo hệ thống bán hàng',null positionId,")
                .append("null positionCode,null positionName, vgcs.channel_type_id ChannelTypeId,vgc.group_status ");
        vsbdBuilder.append("from vtt_group_channel vgc, vtt_group_channel_sale vgcs where vgc.group_channel_code=vgcs.group_channel_code) a");
        vsbdBuilder.append(buildSQL(vttGroupChannel, vhmpHashMap));
        Query query = entityManager.createNativeQuery(vsbdBuilder.toString());
        vhmpHashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        query.setFirstResult(vintFirstResult);
        query.setMaxResults(vintMaxResult);
        return query.getResultList();
    }


    /**
     * dieu kien tim kiem vtt group channel
     * @author DatNT
     * @since 30/12/2019
     * @param vttGroupChannel
     * @param phmpHashmap
     * @return
     */
    private StringBuilder buildSQL(VttGroupChannelDTO vttGroupChannel, HashMap phmpHashmap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" where 1=1 ");
        if (!DataUtil.isNullOrEmpty(vttGroupChannel.getGroupChannelCode())) {
            stringBuilder.append(" and a.group_channel_code =:groupChannelCode");
            phmpHashmap.put("groupChannelCode", vttGroupChannel.getGroupChannelCode().trim());
        }
        if (!DataUtil.isNullOrEmpty(vttGroupChannel.getStatus())) {
            stringBuilder.append(" and a.group_status =:groupStatus");
            phmpHashmap.put("groupStatus", vttGroupChannel.getStatus());
        }

        return stringBuilder;
    }

}


