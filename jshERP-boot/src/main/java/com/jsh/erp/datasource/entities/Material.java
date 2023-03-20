package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class Material {
    private Long id;

    private Long categoryId;

    private String name;

    private String mfrs;

    private String model;

    private String internalId;

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

    private String otherField11;

    private String otherField12;

    private String otherField13;

    private String otherField14;

    private String enableSerialNumber;

    private String enableBatchNumber;

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

    public String getMfrs() {
        return mfrs;
    }

    public void setMfrs(String mfrs) {
        this.mfrs = mfrs == null ? null : mfrs.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId == null ? null : internalId.trim();
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

    public String getOtherField11() {
        return otherField11;
    }

    public void setOtherField11(String otherField11) {
        this.otherField11 = otherField11 == null ? null : otherField11.trim();
    }

    public String getOtherField12() {
        return otherField12;
    }

    public void setOtherField12(String otherField12) {
        this.otherField12 = otherField12 == null ? null : otherField12.trim();
    }

    public String getOtherField13() {
        return otherField13;
    }

    public void setOtherField13(String otherField13) {
        this.otherField13 = otherField13 == null ? null : otherField13.trim();
    }

    public String getOtherField14() {
        return otherField14;
    }

    public void setOtherField14(String otherField14) {
        this.otherField14 = otherField14 == null ? null : otherField14.trim();
    }

    public String getEnableSerialNumber() {
        return enableSerialNumber;
    }

    public void setEnableSerialNumber(String enableSerialNumber) {
        this.enableSerialNumber = enableSerialNumber == null ? null : enableSerialNumber.trim();
    }

    public String getEnableBatchNumber() {
        return enableBatchNumber;
    }

    public void setEnableBatchNumber(String enableBatchNumber) {
        this.enableBatchNumber = enableBatchNumber == null ? null : enableBatchNumber.trim();
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