package com.pethome.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FosterPackage {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal pricePerDay;
    private String services;
    private Integer status;
    private Timestamp createTime;

    public FosterPackage() {}

    public FosterPackage(Integer id, String name, BigDecimal pricePerDay) {
        this.id = id;
        this.name = name;
        this.pricePerDay = pricePerDay;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
