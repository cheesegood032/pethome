package com.pethome.service;

import com.pethome.config.AppConfig;
import com.pethome.dao.PetDao;
import com.pethome.mock.MockData;
import com.pethome.model.Pet;

import java.util.List;

public class PetService {
    private PetDao petDao = new PetDao();

    public boolean addPet(Pet pet) {
        if (AppConfig.USE_MOCK_DATA) {
            MockData.addPet(pet);
            return true;
        }
        
        try {
            return petDao.insert(pet) > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加宠物失败: " + e.getMessage());
        }
    }

    public Pet getPetById(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getPetById(id);
        }
        
        try {
            return petDao.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("获取宠物信息失败");
        }
    }

    public List<Pet> getPetsByUserId(Integer userId) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.getPetsByUserId(userId);
        }
        
        try {
            return petDao.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("获取宠物列表失败");
        }
    }

    public List<Pet> getAllPets() {
        if (AppConfig.USE_MOCK_DATA) {
            // Mock模式返回所有宠物（简化处理）
            return MockData.getPetsByUserId(0);
        }
        
        try {
            return petDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("获取宠物列表失败");
        }
    }

    public int getPetCount() {
        if (AppConfig.USE_MOCK_DATA) {
            return getPetsByUserId(0).size();
        }
        
        try {
            return petDao.count();
        } catch (Exception e) {
            throw new RuntimeException("获取宠物数量失败");
        }
    }

    public boolean updatePet(Pet pet) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.updatePet(pet);
        }
        
        try {
            return petDao.update(pet) > 0;
        } catch (Exception e) {
            throw new RuntimeException("更新宠物信息失败");
        }
    }

    public boolean deletePet(Integer id) {
        if (AppConfig.USE_MOCK_DATA) {
            return MockData.deletePet(id);
        }
        
        try {
            return petDao.delete(id) > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除宠物失败");
        }
    }
}