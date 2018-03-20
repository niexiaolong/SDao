package org.nxl;

import com.alibaba.druid.pool.DruidDataSource;
import org.nxl.dao.SDao;

public class Client {

    public static void main(String[] args) {
        DruidDataSource ds = new DruidDataSource();
        ds.setUsername("root");
        ds.setPassword("sa");
        ds.setUrl("jdbc:mysql://10.0.0.120:3306/app_data?tinyInt1isBit=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull");
        ds.setDriverClassName("com.mysql.jdbc.Driver");


        SDao dao = new SDao(ds);

        dao.executeSql("delete from app_list where id = ?",5);
    }
}
