package org.nxl.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class SDao {

    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private JdbcTemplate jdbcTemplateMaster;
    private JdbcTemplate jdbcTemplateSlave;

    public SDao(DataSource dataSourceMaster) {
        this.dataSourceMaster = dataSourceMaster;
        this.dataSourceMaster = dataSourceMaster;
        jdbcTemplateMaster = new JdbcTemplate(dataSourceMaster);
        jdbcTemplateSlave = new JdbcTemplate(dataSourceSlave);
    }

    public SDao(DataSource dataSourceMaster, DataSource dataSourceSlave) {
        this.dataSourceMaster = dataSourceMaster;
        this.dataSourceSlave = dataSourceSlave;
        jdbcTemplateMaster = new JdbcTemplate(dataSourceMaster);
        jdbcTemplateSlave = new JdbcTemplate(dataSourceSlave);
    }

    /**
     *
     * @param sql
     * @param params
     * @return
     */
    public int executeSql(String sql,Object... params){
        return jdbcTemplateMaster.update(sql, params);
    }

    public <T> T uniqueOne(String sql,Class<T> t,Object... params){
       return jdbcTemplateSlave.queryForObject(sql,t,params);
    }

}
