package com.jsh.erp.datasource.vo;


import java.math.BigDecimal;

public class DepotHeadVo4InOutMCount {

    private Long MaterialId;

    private String barCode;

    private String mName;

    private String Model;

    private String supplierModel;

    private String otherField5;

    private String otherField6;

    private String otherField7;

    private String otherField8;

    private String project;

    private String colorCode;

    private String categoryName;

    private String materialUnit;

    private BigDecimal numSum;

    private BigDecimal priceSum;

    private BigDecimal stock;

    private Long tenantId;

    public Long getMaterialId() {
        return MaterialId;
    }

    public void setMaterialId(Long materialId) {
        MaterialId = materialId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getSupplierModel() {
        return supplierModel;
    }

    public void setSupplierModel(String supplierModel) {
        this.supplierModel = supplierModel;
    }

    public String getOtherField5() {
        return otherField5;
    }

    public void setOtherField5(String otherField5) {
        this.otherField5 = otherField5;
    }

    public String getOtherField6() {
        return otherField6;
    }

    public void setOtherField6(String otherField6) {
        this.otherField6 = otherField6;
    }

    public String getOtherField7() {
        return otherField7;
    }

    public void setOtherField7(String otherField7) {
        this.otherField7 = otherField7;
    }

    public String getOtherField8() {
        return otherField8;
    }

    public void setOtherField8(String otherField8) {
        this.otherField8 = otherField8;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit;
    }

    public BigDecimal getNumSum() {
        return numSum;
    }

    public void setNumSum(BigDecimal numSum) {
        this.numSum = numSum;
    }

    public BigDecimal getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(BigDecimal priceSum) {
        this.priceSum = priceSum;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}