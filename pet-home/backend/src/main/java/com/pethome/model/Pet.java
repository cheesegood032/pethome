package com.pethome.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pet {
    private Integer id;
    private Integer userId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private BigDecimal weight;
    private String image;
    private String healthStatus;
    private String remark;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Pet() {}

    public Pet(Integer id, Integer userId, String name, String species) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.species = species;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}
