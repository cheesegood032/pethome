package com.pethome.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private Integer id;
    private String name;
    private String category;
    private String petType;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String description;
    private String spec;
    private Integer status;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Product() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPetType() { return petType; }
    public void setPetType(String petType) { this.petType = petType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}
