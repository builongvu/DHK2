package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.model.AccessLogin;
import vn.vissoft.dashboard.repo.AccessLoginRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;

public class AccessLoginRepoImpl implements AccessLoginRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * luu log lich su dang nhap
     *
     * @author VuBL
     * @since 2020/06
     * @param dataInsert
     */
    @Override
    public void saveLog(AccessLogin dataInsert) {
        String username = dataInsert.getUsername() == null ? null : dataInsert.getUsername();
        String ip = dataInsert.getIp() == null ? null : dataInsert.getIp();
        Timestamp time = dataInsert.getTimeLogin()  == null ? null : dataInsert.getTimeLogin();
        String browser = dataInsert.getBrowser() == null ? null : dataInsert.getBrowser();
        String sql = new String("insert into access_login(user_name, ip, time_login, browser) values(:username, :ip, :time, :browser)");
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("username", username);
        query.setParameter("ip", ip);
        query.setParameter("time", time);
        query.setParameter("browser", browser);
        query.executeUpdate();
    }

}
