package com.pethome;

import com.pethome.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReadProducts {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();
            String q = "SELECT id, name, category FROM product";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            System.out.println("Products in DB:");
            while(rs.next()) {
                System.out.println(rs.getInt(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
            }
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
