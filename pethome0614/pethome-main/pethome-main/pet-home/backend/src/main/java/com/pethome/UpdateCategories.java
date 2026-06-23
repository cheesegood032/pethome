package com.pethome;

import com.pethome.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateCategories {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();
            String[] queries = {
                "UPDATE product SET category = '\u4e3b\u7cae\u96f6\u98df' WHERE category IN ('\u732b\u7cae', '\u72d7\u7cae')",
                "UPDATE product SET category = '\u5ba0\u7269\u7a9d\u7b3c' WHERE category IN ('\u5bb6\u5c45\u7b3c\u5177', '\u7a9d\u57ab\u5e8a\u54c1', '\u9910\u5177\u6c34\u5177')",
                "UPDATE product SET category = '\u6d17\u62a4\u7528\u54c1' WHERE category = '\u6e05\u6d01\u62a4\u7406'"
            };
            for (String q : queries) {
                PreparedStatement ps = conn.prepareStatement(q);
                int rows = ps.executeUpdate();
                System.out.println("Executed: " + rows + " rows updated.");
                ps.close();
            }
            conn.close();
            System.out.println("Categories updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
