package com.pethome.mock;

import com.pethome.model.*;
import java.util.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class MockData {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    // 用户数据
    private static List<User> users = new ArrayList<>();
    // 管理员数据
    private static List<Admin> admins = new ArrayList<>();
    // 商品数据
    private static List<Product> products = new ArrayList<>();
    // 购物车数据
    private static List<Cart> carts = new ArrayList<>();
    // 订单数据
    private static List<ProductOrder> orders = new ArrayList<>();
    // 订单项数据
    private static List<OrderItem> orderItems = new ArrayList<>();
    // 宠物数据
    private static List<Pet> pets = new ArrayList<>();
    // 寄养套餐数据
    private static List<FosterPackage> fosterPackages = new ArrayList<>();
    // 寄养订单数据
    private static List<FosterOrder> fosterOrders = new ArrayList<>();
    
    private static int userIdCounter = 1;
    private static int productIdCounter = 1;
    private static int cartIdCounter = 1;
    private static int orderIdCounter = 1;
    private static int petIdCounter = 1;
    private static int fosterOrderIdCounter = 1;
    
    static {
        initMockData();
    }
    
    private static void initMockData() {
        // 初始化管理员
        Admin admin = new Admin();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setName("管理员");
        admin.setPhone("13800138000");
        admins.add(admin);
        
        // 初始化商品
        addProduct("皇家猫粮", "猫粮", "猫", "59.90", 100, "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cute%20cat%20food%20bag%20cartoon%20style&image_size=landscape_4_3", "优质猫粮，营养丰富", "2kg");
        addProduct("宝路狗粮", "狗粮", "狗", "69.90", 80, "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cute%20dog%20food%20bag%20cartoon%20style&image_size=landscape_4_3", "美味狗粮，狗狗最爱", "3kg");
        addProduct("宠物玩具球", "玩具", "通用", "19.90", 200, "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cute%20pet%20toy%20ball%20colorful%20cartoon&image_size=landscape_4_3", "互动玩具，增进感情", "直径8cm");
        addProduct("宠物窝", "窝垫", "通用", "99.90", 50, "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cute%20pet%20bed%20soft%20comfortable%20cartoon&image_size=landscape_4_3", "舒适宠物窝", "中号");
        addProduct("猫砂", "日用品", "猫", "29.90", 150, "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cat%20litter%20box%20clean%20cartoon%20style&image_size=landscape_4_3", "高效结团猫砂", "5kg");
        addProduct("宠物梳", "美容", "通用", "15.90", 300, "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cute%20pet%20grooming%20brush%20cartoon&image_size=landscape_4_3", "专业宠物梳子", "针梳");
        
        // 初始化寄养套餐
        addFosterPackage("基础套餐", "日常喂食、清洁", "30", "每日喂食2次，早晚各一次");
        addFosterPackage("舒适套餐", "喂食、清洁、散步", "50", "每日喂食2次，早晚散步各一次");
        addFosterPackage("豪华套餐", "喂食、清洁、散步、玩耍", "80", "每日喂食2次，早晚散步，额外玩耍时间");
    }
    
    private static void addProduct(String name, String category, String petType, String price, int stock, String image, String description, String spec) {
        Product p = new Product();
        p.setId(productIdCounter++);
        p.setName(name);
        p.setCategory(category);
        p.setPetType(petType);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setImage(image);
        p.setDescription(description);
        p.setSpec(spec);
        p.setStatus(1);
        products.add(p);
    }
    
    private static void addFosterPackage(String name, String description, String price, String services) {
        FosterPackage fp = new FosterPackage();
        fp.setId(fosterPackages.size() + 1);
        fp.setName(name);
        fp.setDescription(description);
        fp.setPricePerDay(new BigDecimal(price));
        fp.setServices(services);
        fosterPackages.add(fp);
    }
    
    // ===== 用户相关 =====
    public static User register(String username, String password, String phone, String email) {
        User user = new User();
        user.setId(userIdCounter++);
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setEmail(email);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        users.add(user);
        return user;
    }
    
    public static User login(String loginName, String password) {
        for (User user : users) {
            if ((user.getUsername().equals(loginName) || 
                (user.getPhone() != null && user.getPhone().equals(loginName))) && 
                user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    
    public static User getUserById(int userId) {
        return users.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);
    }
    
    public static boolean updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return true;
            }
        }
        return false;
    }
    
    public static boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
    
    public static List<User> getUsersByPage(int page, int limit) {
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, users.size());
        if (start >= users.size()) return new ArrayList<>();
        return users.subList(start, end);
    }
    
    public static int getUserCount() {
        return users.size();
    }
    
    public static List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return users;
        }
        return users.stream()
            .filter(u -> (u.getUsername() != null && u.getUsername().contains(keyword)) || 
                         (u.getPhone() != null && u.getPhone().contains(keyword)))
            .collect(Collectors.toList());
    }
    
    // ===== 管理员相关 =====
    public static Admin loginAdmin(String loginName, String password) {
        for (Admin admin : admins) {
            if ((admin.getUsername().equals(loginName) || 
                (admin.getPhone() != null && admin.getPhone().equals(loginName))) && 
                admin.getPassword().equals(password)) {
                return admin;
            }
        }
        return null;
    }
    
    // ===== 商品相关 =====
    public static List<Product> getProductsByPage(int page, int limit) {
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, products.size());
        if (start >= products.size()) return new ArrayList<>();
        return products.subList(start, end);
    }
    
    public static int getProductCount() {
        return products.size();
    }
    
    public static Product getProductById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
    
    public static List<Product> searchProducts(String keyword, String category, String petType) {
        return products.stream()
            .filter(p -> (keyword == null || keyword.isEmpty() || 
                         (p.getName() != null && p.getName().contains(keyword))) &&
                         (category == null || category.isEmpty() || 
                         (p.getCategory() != null && p.getCategory().equals(category))) &&
                         (petType == null || petType.isEmpty() || 
                         (p.getPetType() != null && p.getPetType().equals(petType))))
            .collect(Collectors.toList());
    }
    
    public static void addProduct(Product product) {
        product.setId(productIdCounter++);
        product.setCreateTime(new Timestamp(System.currentTimeMillis()));
        product.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        products.add(product);
    }
    
    public static boolean updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                product.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                products.set(i, product);
                return true;
            }
        }
        return false;
    }
    
    public static boolean deleteProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }
    
    public static void updateProductStock(int productId, int quantity) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setStock(product.getStock() - quantity);
        }
    }
    
    // ===== 购物车相关 =====
    public static List<Cart> getCartsByUserId(int userId) {
        return carts.stream().filter(c -> c.getUserId() == userId).collect(Collectors.toList());
    }
    
    public static void addCart(int userId, int productId, int quantity, String spec) {
        // 检查是否已存在相同商品
        for (Cart cart : carts) {
            if (cart.getUserId() == userId && cart.getProductId() == productId && 
                (spec == null ? cart.getSpec() == null : spec.equals(cart.getSpec()))) {
                cart.setQuantity(cart.getQuantity() + quantity);
                cart.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                return;
            }
        }
        Cart cart = new Cart();
        cart.setId(cartIdCounter++);
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setSpec(spec);
        cart.setCreateTime(new Timestamp(System.currentTimeMillis()));
        cart.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        carts.add(cart);
    }
    
    public static void updateCartQuantity(int cartId, int quantity) {
        for (Cart cart : carts) {
            if (cart.getId() == cartId) {
                cart.setQuantity(quantity);
                cart.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                return;
            }
        }
    }
    
    public static void deleteCart(int cartId) {
        carts.removeIf(c -> c.getId() == cartId);
    }
    
    public static void deleteCarts(List<Integer> cartIds) {
        carts.removeIf(c -> cartIds.contains(c.getId()));
    }
    
    // ===== 订单相关 =====
    public static List<ProductOrder> getOrdersByUserId(int userId, String status) {
        return orders.stream()
            .filter(o -> o.getUserId() == userId && 
                         (status == null || status.isEmpty() || o.getStatusStr().equals(status)))
            .collect(Collectors.toList());
    }
    
    public static ProductOrder createOrder(int userId, String receiverName, String receiverPhone, 
                                          String receiverAddress, String remark, List<Integer> cartIds) {
        List<Cart> selectedCarts = carts.stream().filter(c -> cartIds.contains(c.getId())).collect(Collectors.toList());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Cart cart : selectedCarts) {
            Product product = getProductById(cart.getProductId());
            if (product != null) {
                BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cart.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        
        ProductOrder order = new ProductOrder();
        order.setId(orderIdCounter++);
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setTotalPrice(totalAmount);
        order.setStatus(1); // 1=未支付
        order.setRemark(remark);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        orders.add(order);
        
        // 创建订单项
        for (Cart cart : selectedCarts) {
            Product product = getProductById(cart.getProductId());
            if (product != null) {
                OrderItem item = new OrderItem();
                item.setId(orderItems.size() + 1);
                item.setOrderId(order.getId());
                item.setProductId(cart.getProductId());
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setSpec(cart.getSpec());
                item.setImage(product.getImage());
                orderItems.add(item);
            }
        }
        
        // 删除已下单的购物车项
        deleteCarts(cartIds);
        
        return order;
    }
    
    public static ProductOrder getOrderById(int orderId) {
        return orders.stream().filter(o -> o.getId() == orderId).findFirst().orElse(null);
    }
    
    public static List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return orderItems.stream().filter(oi -> oi.getOrderId() == orderId).collect(Collectors.toList());
    }
    
    public static boolean updateOrderStatus(int orderId, String status) {
        ProductOrder order = getOrderById(orderId);
        if (order != null) {
            // 字符串状态转整数
            switch (status) {
                case "unpaid": order.setStatus(1); break;
                case "paid": order.setStatus(2); order.setPayTime(new Timestamp(System.currentTimeMillis())); break;
                case "shipped": order.setStatus(3); order.setShipTime(new Timestamp(System.currentTimeMillis())); break;
                case "received": order.setStatus(4); order.setReceiveTime(new Timestamp(System.currentTimeMillis())); break;
                default: order.setStatus(1);
            }
            return true;
        }
        return false;
    }
    
    public static List<ProductOrder> searchOrders(String keyword, String status, int page, int limit) {
        List<ProductOrder> filtered = orders.stream()
            .filter(o -> (keyword == null || keyword.isEmpty() || 
                         (o.getReceiverName() != null && o.getReceiverName().contains(keyword))) &&
                         (status == null || status.isEmpty() || o.getStatusStr().equals(status)))
            .collect(Collectors.toList());
        
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, filtered.size());
        if (start >= filtered.size()) return new ArrayList<>();
        return filtered.subList(start, end);
    }
    
    public static int getOrderCount(String keyword, String status) {
        return (int) orders.stream()
            .filter(o -> (keyword == null || keyword.isEmpty() || 
                         (o.getReceiverName() != null && o.getReceiverName().contains(keyword))) &&
                         (status == null || status.isEmpty() || o.getStatusStr().equals(status)))
            .count();
    }
    
    // ===== 宠物相关 =====
    public static List<Pet> getPetsByUserId(int userId) {
        return pets.stream().filter(p -> p.getUserId() == userId).collect(Collectors.toList());
    }
    
    public static Pet getPetById(int id) {
        return pets.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
    
    public static void addPet(Pet pet) {
        pet.setId(petIdCounter++);
        pet.setCreateTime(new Timestamp(System.currentTimeMillis()));
        pets.add(pet);
    }
    
    public static boolean updatePet(Pet pet) {
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getId() == pet.getId()) {
                pets.set(i, pet);
                return true;
            }
        }
        return false;
    }
    
    public static boolean deletePet(int id) {
        return pets.removeIf(p -> p.getId() == id);
    }
    
    // ===== 寄养套餐相关 =====
    public static List<FosterPackage> getAllFosterPackages() {
        return fosterPackages;
    }
    
    public static FosterPackage getFosterPackageById(int id) {
        return fosterPackages.stream().filter(fp -> fp.getId() == id).findFirst().orElse(null);
    }
    
    // ===== 寄养订单相关 =====
    public static List<FosterOrder> getFosterOrdersByUserId(int userId, String status) {
        return fosterOrders.stream()
            .filter(o -> o.getUserId() == userId && 
                         (status == null || status.isEmpty() || o.getStatusStr().equals(status)))
            .collect(Collectors.toList());
    }
    
    public static FosterOrder createFosterOrder(int userId, int petId, int packageId, 
                                                Date startDate, Date endDate) {
        Pet pet = getPetById(petId);
        FosterPackage fp = getFosterPackageById(packageId);
        
        if (pet == null || fp == null) {
            return null;
        }
        
        // 确保日期是 java.sql.Date 类型
        java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
        
        long days = (sqlEndDate.getTime() - sqlStartDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
        BigDecimal totalAmount = fp.getPricePerDay().multiply(new BigDecimal((int) days));
        
        FosterOrder order = new FosterOrder();
        order.setId(fosterOrderIdCounter++);
        order.setUserId(userId);
        order.setPetId(petId);
        order.setPackageId(packageId);
        order.setStartDate(sqlStartDate);
        order.setEndDate(sqlEndDate);
        order.setTotalDays((int) days);
        order.setTotalPrice(totalAmount);
        order.setStatus(1); // 1=待审核
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setPetName(pet.getName());
        order.setPackageName(fp.getName());
        fosterOrders.add(order);
        return order;
    }
    
    public static FosterOrder getFosterOrderById(int orderId) {
        return fosterOrders.stream().filter(o -> o.getId() == orderId).findFirst().orElse(null);
    }
    
    public static boolean updateFosterOrderStatus(int orderId, String status) {
        FosterOrder order = getFosterOrderById(orderId);
        if (order != null) {
            // 字符串状态转整数
            switch (status) {
                case "pending": order.setStatus(1); break;
                case "approved": order.setStatus(2); order.setAuditTime(new Timestamp(System.currentTimeMillis())); break;
                case "paid": order.setStatus(3); order.setPayTime(new Timestamp(System.currentTimeMillis())); break;
                case "fostering": order.setStatus(4); break;
                case "completed": order.setStatus(5); order.setCompleteTime(new Timestamp(System.currentTimeMillis())); break;
                case "cancelled": order.setStatus(6); break;
                default: order.setStatus(1);
            }
            return true;
        }
        return false;
    }
    
    public static List<FosterOrder> searchFosterOrders(String keyword, String status, Date startDate, Date endDate) {
        return fosterOrders.stream()
            .filter(o -> (keyword == null || keyword.isEmpty() || 
                         (o.getPetName() != null && o.getPetName().contains(keyword))) &&
                         (status == null || status.isEmpty() || o.getStatusStr().equals(status)))
            .collect(Collectors.toList());
    }
    
    public static int getFosterOrderCount(String keyword, String status, Date startDate, Date endDate) {
        return (int) fosterOrders.stream()
            .filter(o -> (keyword == null || keyword.isEmpty() || 
                         (o.getPetName() != null && o.getPetName().contains(keyword))) &&
                         (status == null || status.isEmpty() || o.getStatusStr().equals(status)))
            .count();
    }
}