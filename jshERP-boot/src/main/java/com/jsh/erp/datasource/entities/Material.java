package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class Material {
    private Long id;

    private Long categoryId;

    private String name;

    private String mat;

    private String model;

    private String colorCode;

    private String color;

    private String unit;

    private String remark;

    private String imgName;

    private Long unitId;

    private Integer expiryNum;

    private BigDecimal weight;

    private Boolean enabled;

    private String otherField1;

    private String otherField2;

    private String otherField3;

    private String otherField4;

    private String otherField5;

    private String otherField6;

    private String otherField7;

    private String otherField8;

    private String otherField9;

    private String otherField10;

    private Long tenantId;

    private String deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat == null ? null : mat.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode == null ? null : colorCode.trim();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName == null ? null : imgName.trim();
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getExpiryNum() {
        return expiryNum;
    }

    public void setExpiryNum(Integer expiryNum) {
        this.expiryNum = expiryNum;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getOtherField1() {
        return otherField1;
    }

    public void setOtherField1(String otherField1) {
        this.otherField1 = otherField1 == null ? null : otherField1.trim();
    }

    public String getOtherField2() {
        return otherField2;
    }

    public void setOtherField2(String otherField2) {
        this.otherField2 = otherField2 == null ? null : otherField2.trim();
    }

    public String getOtherField3() {
        return otherField3;
    }

    public void setOtherField3(String otherField3) {
        this.otherField3 = otherField3 == null ? null : otherField3.trim();
    }

    public String getOtherField4() {
        return otherField4;
    }

    public void setOtherField4(String otherField4) {
        this.otherField4 = otherField4 == null ? null : otherField4.trim();
    }

    public String getOtherField5() {
        return otherField5;
    }

    public void setOtherField5(String otherField5) {
        this.otherField5 = otherField5 == null ? null : otherField5.trim();
    }

    public String getOtherField6() {
        return otherField6;
    }

    public void setOtherField6(String otherField6) {
        this.otherField6 = otherField6 == null ? null : otherField6.trim();
    }

    public String getOtherField7() {
        return otherField7;
    }

    public void setOtherField7(String otherField7) {
        this.otherField7 = otherField7 == null ? null : otherField7.trim();
    }

    public String getOtherField8() {
        return otherField8;
    }

    public void setOtherField8(String otherField8) {
        this.otherField8 = otherField8 == null ? null : otherField8.trim();
    }

    public String getOtherField9() {
        return otherField9;
    }

    public void setOtherField9(String otherField9) {
        this.otherField9 = otherField9 == null ? null : otherField9.trim();
    }

    public String getOtherField10() {
        return otherField10;
    }

    public void setOtherField10(String otherField10) {
        this.otherField10 = otherField10 == null ? null : otherField10.trim();
    }
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag == null ? null : deleteFlag.trim();
    }
}