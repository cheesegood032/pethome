package com.pethome.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static DruidDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            props.load(is);
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {}
            }
        }
    }
}
