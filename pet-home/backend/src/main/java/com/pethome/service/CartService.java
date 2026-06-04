package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.CartDao;
import com.pethome.mock.MockData;
import com.pethome.model.Cart;
import com.pethome.model.Product;

import java.util.List;

public class CartService {
    private CartDao cartDao = new CartDao();
    private ProductService productService = new ProductService();

    public boolean addToCart(Integer userId, Integer productId, Integer quantity, String spec) {
        if (AppConfig.USE_MOCK_DATA) {
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            MockData.addCart(userId, productId, quantity, spec);
            return true;
        }
        
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }

            Cart existing = cartDao.findByUserIdAndProductId(userId, productId);
            if (existing != null) {
                int newQty = existing.getQuantity() + quantity;
                if (product.getStock() < newQty) {
                    throw new RuntimeException("库存不足");
                }
                return cartDao.updateQuantity(existing.getId(), newQty) > 0;
            }

            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setSpec(spec);
            return cartDao.insert(null, cart) > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加购物车失败: " + e.getMessage());
        }
    }

    public List<Cart> getCartByUserId(Integer userId) {
        if (AppConfig.USE_MOCK_DATA) {
            List<Cart> carts = MockData.getCartsByUserId(userId);
            for (Cart cart : carts) {
                cart.setProduct(productService.getProductById(cart.getProductId()));
            }
            return carts;
        }
        
        try {
            List<Cart> carts = cartDao.findByUserId(userId);
            for (Cart cart : carts) {
                cart.setProduct(productService.getProductById(cart.getProductId()));
            }
            return carts;
        } catch (Exception e) {
            throw new RuntimeException("获取购物车失败");
        }
    }

    public boolean updateQuantity(Integer cartId, Integer quantity) {
        if (AppConfig.USE_MOCK_DATA) {
            MockData.updateCartQuantity(cartId, quantity);
            return true;
        }
        
        try {
            Cart cart = cartDao.findById(cartId);
            if (cart == null) {
                throw new RuntimeException("购物车项不存在");
            }
            Product product = productService.getProductById(cart.getProductId());
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            return cartDao.updateQuantity(cartId, quantity) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新数量失败");
        }
    }

    public boolean removeFromCart(Integer cartId) {
        if (AppConfig.USE_MOCK_DATA) {
            MockData.deleteCart(cartId);
            return true;
        }
        
        try {
            return cartDao.delete(cartId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除购物车项失败");
        }
    }

    public boolean clearCart(Integer userId) {
        if (AppConfig.USE_MOCK_DATA) {
            List<Cart> carts = MockData.getCartsByUserId(userId);
            for (Cart cart : carts) {
                MockData.deleteCart(cart.getId());
            }
            return true;
        }
        
        try {
            return cartDao.deleteByUserId(userId) >= 0;
        } catch (Exception e) {
            throw new RuntimeException("清空购物车失败");
        }
    }

    public boolean batchRemove(List<Integer> cartIds) {
        if (AppConfig.USE_MOCK_DATA) {
            MockData.deleteCarts(cartIds);
            return true;
        }
        
        try {
            return cartDao.deleteByIds(cartIds) >= 0;
        } catch (Exception e) {
            throw new RuntimeException("批量删除失败");
        }
    }
}