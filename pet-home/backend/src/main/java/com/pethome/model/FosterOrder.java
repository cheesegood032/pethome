package com.pethome.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class FosterOrder {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer petId;
    private Integer packageId;
    private Date startDate;
    private Date endDate;
    private Integer totalDays;
    private BigDecimal totalPrice;
    private Integer status;
    private String remark;
    private String rejectReason;
    private Timestamp createTime;
    private Timestamp auditTime;
    private Timestamp payTime;
    private Timestamp completeTime;
    private User user;
    private Pet pet;
    private FosterPackage fosterPackage;
    
    // 新增字段，方便前后端交互
    private String petName;
    private String packageName;

    public FosterOrder() {}

    public FosterOrder(Integer id, String orderNo, Integer userId, Integer petId, Integer packageId,
                       Date startDate, Date endDate, Integer totalDays, BigDecimal totalPrice, Integer status) {
        this.id = id;
        this.orderNo = orderNo;
        this.userId = userId;
        this.petId = petId;
        this.packageId = packageId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPetId() { return petId; }
    public void setPetId(Integer petId) { this.petId = petId; }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getAuditTime() { return auditTime; }
    public void setAuditTime(Timestamp auditTime) { this.auditTime = auditTime; }

    public Timestamp getPayTime() { return payTime; }
    public void setPayTime(Timestamp payTime) { this.payTime = payTime; }

    public Timestamp getCompleteTime() { return completeTime; }
    public void setCompleteTime(Timestamp completeTime) { this.completeTime = completeTime; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public FosterPackage getFosterPackage() { return fosterPackage; }
    public void setFosterPackage(FosterPackage fosterPackage) { this.fosterPackage = fosterPackage; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getStatusStr() {
        if (status == null) return "pending";
        switch (status) {
            case 1: return "pending";
            case 2: return "approved";
            case 3: return "paid";
            case 4: return "fostering";
            case 5: return "completed";
            case 6: return "cancelled";
            default: return "pending";
        }
    }
}