package com.pethome.model;

import java.math.BigDecimal;

public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String spec;
    private String image;

    public OrderItem() {}

    public OrderItem(Integer id, Integer orderId, Integer productId, String productName, BigDecimal price, Integer quantity, String spec) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.spec = spec;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
