package com.pethome;

import com.pethome.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReadCategories {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();
            String q = "SELECT DISTINCT category FROM product";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            System.out.println("Current categories in DB:");
            while(rs.next()) {
                System.out.println(" - " + rs.getString(1));
            }
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
