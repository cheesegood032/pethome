package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.ProductDao;
import com.pethome.mock.MockData;
import com.pethome.model.Product;

import java.util.List;

public class ProductService {
    private ProductDao productDao = new ProductDao();

    public boolean addProduct(Product product) {
        if (AppConfig.USE_MOCK_DATA) {
            MockData.addProduct(product);
            return true;
        }
        
        try {
            return productDao.insert(product) > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加商品失败: " + e.getMessage());
        }
    }

    public Product getProductById(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getProductById(id);
        }
        
        try {
            return productDao.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取商品信息失败");
        }
    }

    public List<Product> getAllProducts() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getProductsByPage(1, Integer.MAX_VALUE);
        }
        
        try {
            return productDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("获取商品列表失败");
        }
    }

    public List<Product> getProductsByPage(int page, int limit) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getProductsByPage(page, limit);
        }
        
        try {
            return productDao.findByPage((page - 1) * limit, limit);
        } catch (Exception e) {
            throw new RuntimeException("获取商品列表失败");
        }
    }

    public int getProductCount() {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getProductCount();
        }
        
        try {
            return productDao.count();
        } catch (Exception e) {
            throw new RuntimeException("获取商品数量失败");
        }
    }

    public List<Product> getProductsByStatus(Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getProductsByPage(1, Integer.MAX_VALUE);
        }
        
        try {
            return productDao.findAllWithStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("获取商品列表失败");
        }
    }

    public int getProductCountByStatus(Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getProductCount();
        }
        
        try {
            return productDao.countWithStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("获取商品数量失败");
        }
    }

    public List<Product> searchProducts(String keyword, String category, String petType) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.searchProducts(keyword, category, petType);
        }
        
        try {
            return productDao.search(keyword, category, petType);
        } catch (Exception e) {
            throw new RuntimeException("搜索商品失败");
        }
    }

    public boolean updateProduct(Product product) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updateProduct(product);
        }
        
        try {
            return productDao.update(product) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新商品失败");
        }
    }

    public boolean updateStock(Integer productId, int quantity) {
        if (AppConfig.USE_MOCK_DATA) {
            MockData.updateProductStock(productId, quantity);
            return true;
        }
        
        try {
            return productDao.updateStock(productId, quantity) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新库存失败");
        }
    }

    public boolean deleteProduct(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.deleteProduct(id);
        }
        
        try {
            return productDao.delete(id) > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除商品失败");
        }
    }

    public boolean updateStatus(Integer id, Integer status) {
        if (AppConfig.USE_MOCK_DATA) {
            Product product = MockData.getProductById(id);
            if (product != null) {
                product.setStatus(status);
                return true;
            }
            return false;
        }
        
        try {
            return productDao.updateStatus(id, status) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新商品状态失败");
        }
    }
}