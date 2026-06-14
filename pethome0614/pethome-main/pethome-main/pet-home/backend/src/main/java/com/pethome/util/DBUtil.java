package com.pethome.util;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库工具类 —— 基于 DBCP2 连接池
 */
public class DBUtil {

    private static BasicDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                throw new RuntimeException("找不到 db.properties 配置文件");
            }
            props.load(is);
            is.close();

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(props.getProperty("db.driver"));
            dataSource.setUrl(props.getProperty("db.url"));
            dataSource.setUsername(props.getProperty("db.username"));
            dataSource.setPassword(props.getProperty("db.password"));
            dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.initialSize", "5")));
            dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.maxTotal", "20")));
            dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.maxIdle", "10")));
            dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.minIdle", "5")));
            dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("db.maxWaitMillis", "5000")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据库连接池失败", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭资源（Connection, Statement, ResultSet）
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * 关闭资源（Connection, Statement）
     */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    /**
     * 仅关闭连接
     */
    public static void close(Connection conn) {
        close(conn, null, null);
    }
}
