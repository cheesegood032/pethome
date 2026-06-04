package com.pethome.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ProductOrder {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private BigDecimal totalPrice;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private Timestamp createTime;
    private Timestamp payTime;
    private Timestamp shipTime;
    private Timestamp receiveTime;
    private List<OrderItem> items;
    private User user;

    public ProductOrder() {}

    public ProductOrder(Integer id, String orderNo, Integer userId, BigDecimal totalPrice, Integer status) {
        this.id = id;
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getStatusStr() {
        if (status == null) return "unpaid";
        switch (status) {
            case 1: return "unpaid";
            case 2: return "paid";
            case 3: return "shipped";
            case 4: return "received";
            default: return "unpaid";
        }
    }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getPayTime() { return payTime; }
    public void setPayTime(Timestamp payTime) { this.payTime = payTime; }

    public Timestamp getShipTime() { return shipTime; }
    public void setShipTime(Timestamp shipTime) { this.shipTime = shipTime; }

    public Timestamp getReceiveTime() { return receiveTime; }
    public void setReceiveTime(Timestamp receiveTime) { this.receiveTime = receiveTime; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
