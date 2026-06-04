package com.pethome.model;

import java.sql.Timestamp;

public class Cart {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String spec;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Product product;

    public Cart() {}

    public Cart(Integer id, Integer userId, Integer productId, Integer quantity, String spec) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.spec = spec;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
