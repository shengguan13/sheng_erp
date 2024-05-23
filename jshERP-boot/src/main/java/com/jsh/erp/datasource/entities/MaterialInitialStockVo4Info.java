package com.jsh.erp.datasource.entities;

public class MaterialInitialStockVo4Info extends MaterialInitialStock {
    private String barCode;
    private String name;
    private String model;
    private String supplierModel;
    private String colorCode;
    private String otherField5;
    private String color;
    private String unit;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplierModel() {
        return supplierModel;
    }

    public void setSupplierModel(String supplierModel) {
        this.supplierModel = supplierModel;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getOtherField5() {
        return otherField5;
    }

    public void setOtherField5(String otherField5) {
        this.otherField5 = otherField5;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
