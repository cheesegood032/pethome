package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.FosterOrderDao;
import com.pethome.mock.MockData;
import com.pethome.model.FosterOrder;
import com.pethome.model.FosterPackage;
import com.pethome.util.IDUtil;

import java.sql.Date;
import java.util.List;

public class FosterOrderService {
    private FosterOrderDao orderDao = new FosterOrderDao();
    private FosterPackageService packageService = new FosterPackageService();

    public FosterOrder createOrder(Integer userId, Integer petId, Integer packageId,
                                   Date startDate, Date endDate, String remark) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.createFosterOrder(userId, petId, packageId, startDate, endDate);
        }
        
        try {
            if (endDate.before(startDate)) {
                throw new RuntimeException("结束日期不能早于开始日期");
            }

            long diff = endDate.getTime() - startDate.getTime();
            int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;

            FosterPackage pkg = packageService.getPackageById(packageId);
            if (pkg == null) {
                throw new RuntimeException("套餐不存在");
            }

            FosterOrder order = new FosterOrder();
            order.setOrderNo(IDUtil.generateFosterOrderNo());
            order.setUserId(userId);
            order.setPetId(petId);
            order.setPackageId(packageId);
            order.setStartDate(startDate);
            order.setEndDate(endDate);
            order.setTotalDays(days);
            order.setStatus(1);
            order.setRemark(remark);

            orderDao.insert(order);
            return orderDao.findByOrderNo(order.getOrderNo());
        } catch (Exception e) {
            throw new RuntimeException("创建寄养订单失败: " + e.getMessage());
        }
    }

    public FosterOrder getOrderById(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getFosterOrderById(id);
        }
        
        try {
            return orderDao.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取订单信息失败");
        }
    }

    public List<FosterOrder> getOrdersByUserId(Integer userId) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getFosterOrdersByUserId(userId, null);
        }
        
        try {
            return orderDao.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<FosterOrder> getOrdersByUserIdAndStatus(Integer userId, Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            String statusStr = status == 1 ? "pending" : status == 2 ? "approved" : 
                status == 3 ? "paid" : status == 4 ? "fostering" : "completed";
            return MockData.getFosterOrdersByUserId(userId, statusStr);
        }
        
        try {
            return orderDao.findByUserIdAndStatus(userId, status);
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<FosterOrder> getAllOrders() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getFosterOrdersByUserId(0, null);
        }
        
        try {
            return orderDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<FosterOrder> getOrdersByStatus(Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            String statusStr = status == 1 ? "pending" : status == 2 ? "approved" : 
                status == 3 ? "paid" : status == 4 ? "fostering" : "completed";
            return MockData.getFosterOrdersByUserId(0, statusStr);
        }
        
        try {
            return orderDao.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public int getOrderCount() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getFosterOrderCount(null, null, null, null);
        }
        
        try {
            return orderDao.count();
        } catch (Exception e) {
            throw new RuntimeException("获取订单数量失败");
        }
    }

    public boolean cancelOrder(Integer orderId, Integer userId) {
        if (AppConfig.USE_MOCK_DATA) {
            FosterOrder order = MockData.getFosterOrderById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("无权取消此订单");
            }
            if (!"pending".equals(order.getStatus())) {
                throw new RuntimeException("只能取消待审核的订单");
            }
            return MockData.updateFosterOrderStatus(orderId, "cancelled");
        }
        
        try {
            FosterOrder order = orderDao.findById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("无权取消此订单");
            }
            if (order.getStatus() != 1) {
                throw new RuntimeException("只能取消待审核的订单");
            }
            return orderDao.updateStatus(orderId, 5) > 0;
        } catch (Exception e) {
            throw new RuntimeException("取消订单失败");
        }
    }

    public boolean auditOrder(Integer orderId, boolean approved, String rejectReason) {
        if (AppConfig.USE_MOCK_DATA) {
            String status = approved ? "approved" : "cancelled";
            return MockData.updateFosterOrderStatus(orderId, status);
        }
        
        try {
            FosterOrder order = orderDao.findById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            if (order.getStatus() != 1) {
                throw new RuntimeException("只能审核待审核的订单");
            }
            int status = approved ? 2 : 5;
            return orderDao.audit(orderId, status, approved ? null : rejectReason) > 0;
        } catch (Exception e) {
            throw new RuntimeException("审核订单失败");
        }
    }

    public boolean completeOrder(Integer orderId) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updateFosterOrderStatus(orderId, "completed");
        }
        
        try {
            return orderDao.complete(orderId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("完成订单失败");
        }
    }

    public List<FosterOrder> searchOrders(Integer userId, Integer status, Date startDate, Date endDate) {
        if (AppConfig.USE_MOCK_DATA) {
            String statusStr = status == null ? null : 
                status == 1 ? "pending" : status == 2 ? "approved" : 
                status == 3 ? "paid" : status == 4 ? "fostering" : "completed";
            return MockData.searchFosterOrders(null, statusStr, startDate, endDate);
        }
        
        try {
            return orderDao.search(userId, status, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("搜索订单失败");
        }
    }
}