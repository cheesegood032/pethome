package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.CartDao;
import com.pethome.dao.OrderItemDao;
import com.pethome.dao.ProductDao;
import com.pethome.dao.ProductOrderDao;
import com.pethome.mock.MockData;
import com.pethome.model.*;
import com.pethome.util.DBUtil;
import com.pethome.util.IDUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class ProductOrderService {
    private ProductOrderDao orderDao = new ProductOrderDao();
    private OrderItemDao itemDao = new OrderItemDao();
    private CartDao cartDao = new CartDao();
    private ProductDao productDao = new ProductDao();
    private ProductService productService = new ProductService();

    public ProductOrder createOrder(Integer userId, String receiverName, String receiverPhone,
                                    String receiverAddress, String remark, List<Integer> cartIds) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.createOrder(userId, receiverName, receiverPhone, receiverAddress, remark, cartIds);
        }
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            List<Cart> carts = cartDao.findByUserId(userId);
            if (carts.isEmpty()) {
                throw new RuntimeException("购物车为空");
            }

            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Cart cart : carts) {
                Product product = productDao.findById(cart.getProductId());
                if (product.getStock() < cart.getQuantity()) {
                    throw new RuntimeException("商品[" + product.getName() + "]库存不足");
                }
                totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            }

            ProductOrder order = new ProductOrder();
            order.setOrderNo(IDUtil.generateOrderNo());
            order.setUserId(userId);
            order.setTotalPrice(totalPrice);
            order.setStatus(1);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setReceiverAddress(receiverAddress);
            order.setRemark(remark);

            orderDao.insert(conn, order);

            ProductOrder savedOrder = orderDao.findByOrderNo(order.getOrderNo());

            for (Cart cart : carts) {
                Product product = productDao.findById(cart.getProductId());
                OrderItem item = new OrderItem();
                item.setOrderId(savedOrder.getId());
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setSpec(cart.getSpec());
                itemDao.insert(conn, item);

                productDao.updateStock(product.getId(), cart.getQuantity());
                cartDao.delete(cart.getId());
            }

            conn.commit();
            return savedOrder;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            throw new RuntimeException("创建订单失败: " + e.getMessage());
        } finally {
            DBUtil.close(conn);
        }
    }

    public ProductOrder getOrderById(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            ProductOrder order = MockData.getOrderById(id);
            if (order != null) {
                order.setItems(MockData.getOrderItemsByOrderId(id));
            }
            return order;
        }
        
        try {
            ProductOrder order = orderDao.findById(id);
            if (order != null) {
                order.setItems(itemDao.findByOrderId(id));
            }
            return order;
        } catch (Exception e) {
            throw new RuntimeException("获取订单失败");
        }
    }

    public List<ProductOrder> getOrdersByUserId(Integer userId) {
        if (AppConfig.USE_MOCK_DATA) {
            List<ProductOrder> orders = MockData.getOrdersByUserId(userId, null);
            for (ProductOrder order : orders) {
                order.setItems(MockData.getOrderItemsByOrderId(order.getId()));
            }
            return orders;
        }
        
        try {
            List<ProductOrder> orders = orderDao.findByUserId(userId);
            for (ProductOrder order : orders) {
                order.setItems(itemDao.findByOrderId(order.getId()));
            }
            return orders;
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<ProductOrder> getOrdersByUserIdAndStatus(Integer userId, Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            String statusStr = status == 1 ? "unpaid" : status == 2 ? "paid" : status == 3 ? "shipped" : "received";
            List<ProductOrder> orders = MockData.getOrdersByUserId(userId, statusStr);
            for (ProductOrder order : orders) {
                order.setItems(MockData.getOrderItemsByOrderId(order.getId()));
            }
            return orders;
        }
        
        try {
            List<ProductOrder> orders = orderDao.findByUserIdAndStatus(userId, status);
            for (ProductOrder order : orders) {
                order.setItems(itemDao.findByOrderId(order.getId()));
            }
            return orders;
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<ProductOrder> getAllOrders() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getOrdersByUserId(0, null);
        }
        
        try {
            return orderDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<ProductOrder> getOrdersByStatus(Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            String statusStr = status == 1 ? "unpaid" : status == 2 ? "paid" : status == 3 ? "shipped" : "received";
            return MockData.getOrdersByUserId(0, statusStr);
        }
        
        try {
            return orderDao.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public List<ProductOrder> getOrdersByPage(int page, int limit) {
        if (AppConfig.USE_MOCK_DATA) {
            // Mock模式简化处理
            List<ProductOrder> allOrders = MockData.getOrdersByUserId(0, null);
            int start = (page - 1) * limit;
            int end = Math.min(start + limit, allOrders.size());
            return allOrders.subList(start, end);
        }
        
        try {
            return orderDao.findByPage((page - 1) * limit, limit);
        } catch (Exception e) {
            throw new RuntimeException("获取订单列表失败");
        }
    }

    public int getOrderCount() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getOrderCount(null, null);
        }
        
        try {
            return orderDao.count();
        } catch (Exception e) {
            throw new RuntimeException("获取订单数量失败");
        }
    }

    public boolean payOrder(Integer orderId) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updateOrderStatus(orderId, "paid");
        }
        
        try {
            return orderDao.updatePayTime(orderId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("支付订单失败");
        }
    }

    public boolean shipOrder(Integer orderId) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updateOrderStatus(orderId, "shipped");
        }
        
        try {
            return orderDao.updateShipTime(orderId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("发货失败");
        }
    }

    public boolean receiveOrder(Integer orderId) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updateOrderStatus(orderId, "received");
        }
        
        try {
            return orderDao.updateReceiveTime(orderId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("确认收货失败");
        }
    }

    public List<ProductOrder> searchOrders(String keyword, Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            String statusStr = status == null ? null : 
                status == 1 ? "unpaid" : status == 2 ? "paid" : status == 3 ? "shipped" : "received";
            return MockData.searchOrders(keyword, statusStr, 1, Integer.MAX_VALUE);
        }
        
        try {
            return orderDao.search(keyword, status);
        } catch (Exception e) {
            throw new RuntimeException("搜索订单失败");
        }
    }
}