package com.pethome.model;

import java.sql.Timestamp;

public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String realName;
    private String role;
    private Timestamp createTime;

    public Admin() {}

    public Admin(Integer id, String username, String realName) {
        this.id = id;
        this.username = username;
        this.realName = realName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    
    public String getName() { return realName; }
    public void setName(String name) { this.realName = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
