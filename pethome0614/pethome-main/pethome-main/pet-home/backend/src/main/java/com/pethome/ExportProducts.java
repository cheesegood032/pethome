package com.pethome;

import com.pethome.util.DBUtil;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.nio.charset.StandardCharsets;

public class ExportProducts {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();
            String q = "SELECT id, name, category FROM product";
            PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("products_export.txt"), StandardCharsets.UTF_8));
            writer.println("Products in DB:");
            while(rs.next()) {
                writer.println(rs.getInt(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
            }
            writer.close();
            ps.close();
            conn.close();
            System.out.println("Exported to products_export.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
