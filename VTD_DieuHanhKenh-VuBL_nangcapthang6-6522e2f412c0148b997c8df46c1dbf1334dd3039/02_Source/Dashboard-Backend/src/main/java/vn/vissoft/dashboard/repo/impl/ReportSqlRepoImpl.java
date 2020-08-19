package vn.vissoft.dashboard.repo.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.SqlReportDetailDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ReportSql;
import vn.vissoft.dashboard.repo.ReportSqlRepoCustom;

public class ReportSqlRepoImpl implements ReportSqlRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger LOGGER = LogManager.getLogger(ReportSqlRepoImpl.class);

    /**
     * Tim nhung sql content dang hoat dong
     *
     * @return
     * @author QuangND
     * @since 13/09/2019
     */
    @Override
    public List<ReportSql> findActiveReportSql() {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select rs from ReportSql rs where rs.status='1' order by case when rs.name like 'đ%' then 'd' else rs.name end");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra ton tai reportsql trong csdl
     *
     * @param pstrCode
     * @return
     * @author QuangND
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedReportSqlCode(String pstrCode) {
        boolean vblnCheck = false;
        for (ReportSql reportSql : findActiveReportSql()) {
            if (pstrCode.equalsIgnoreCase(reportSql.getCode())) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    /**
     * Tìm report sql theo ID
     *
     * @return
     * @author Manhtd
     * @since 11/2019
     */
    @Override
    public ReportSql findReportSqlById(Long plngId) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        if (DataUtil.isNullOrZero(plngId)) return null;
        vsbdSqlBuilder.append("select rs from ReportSql rs where rs.status='1' and rs.id=:id");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("id", plngId);
        return (ReportSql) query.getSingleResult();
    }

    /**
     * run query trong conent của đói tượng SQL
     *
     * @return
     * @author Manhtd
     * @since 11/2019
     */
    @Override
    public List<Object[]> getContentSql(SqlReportDetailDTO sqlReportDetailDTO) {
        try {

            ReportSql reportSql = this.findReportSqlById(sqlReportDetailDTO.getIdReport());
            String contentSql = reportSql.getContent();

            Query query = entityManager.createNativeQuery(contentSql);

            if (sqlReportDetailDTO.getFromDate() != null) {
                query.setParameter("fromDate", sqlReportDetailDTO.getFromDate());
            }

            if (sqlReportDetailDTO.getToDate() != null) {
                query.setParameter("toDate", sqlReportDetailDTO.getToDate());
            }

            if (sqlReportDetailDTO.getShopCode() != null) {
                query.setParameter("shopCode", sqlReportDetailDTO.getShopCode());
            }

            return query.getResultList();

        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
        return null;
    }

    /**
     * lấy tên các cột trong câu query
     *
     * @return
     * @author Manhtd
     * @since 11/2019
     */
    @Override
    public List<String> getColumnName(String sql) {

        Session session = entityManager.unwrap(Session.class);
        List<String> vlstListColumn = new ArrayList<>();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sql);
                try (ResultSet rs = stmt.executeQuery();) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int numOfCols = metaData.getColumnCount();
                    vlstListColumn.add(I18N.get("common.header.order"));
                    for (int i = 1; i <= numOfCols; i++) {
                        String colName = metaData.getColumnName(i);
                        vlstListColumn.add(colName);
                    }

                } catch (Exception e) {
                    LOGGER.error(e.toString(), e);
                } finally {
                    stmt.close();
                }
            }
        });
        return vlstListColumn;
    }

}
