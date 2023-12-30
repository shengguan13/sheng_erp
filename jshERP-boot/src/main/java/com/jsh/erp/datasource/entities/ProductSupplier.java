package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class ProductSupplier {
    private Long id;

    private Long supplierId;

    private String supplierType;

    private String model;

    private String barCode;

    private String purchaseCycle;

    private String pack;

    private String unit;

    private BigDecimal priceNoTax;

    private BigDecimal taxRate;

    private Long tenantId;

    private String deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getPurchaseCycle() {
        return purchaseCycle;
    }

    public void setPurchaseCycle(String purchaseCycle) {
        this.purchaseCycle = purchaseCycle;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPriceNoTax() {
        return priceNoTax;
    }

    public void setPriceNoTax(BigDecimal priceNoTax) {
        this.priceNoTax = priceNoTax;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
