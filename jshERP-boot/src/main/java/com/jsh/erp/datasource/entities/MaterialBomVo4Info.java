package com.jsh.erp.datasource.entities;

import java.util.List;

public class MaterialBomVo4Info extends MaterialBom {

    private Long key;

    private Long value;

    private String title;

    private String state = "open";

    private List<MaterialBomVo4Info> children;

    private boolean checked;

    private String name;

    private String colorCode;

    private String model;

    private String otherField5;

    private String otherField6;

    private String otherField7;

    private String otherField8;

    private String supplierModel;

    private String supplierName;

    private String material;

    private String mUnit;

    private String category;

    private String color;

    private Long meId;

    private Long materialId;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public List<MaterialBomVo4Info> getChildren() {
        return children;
    }

    public void setChildren(List<MaterialBomVo4Info> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSupplierModel() {
        return supplierModel;
    }

    public void setSupplierModel(String supplierModel) {
        this.supplierModel = supplierModel;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getmUnit() {
        return mUnit;
    }

    public void setmUnit(String mUnit) {
        this.mUnit = mUnit;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getMeId() {
        return meId;
    }

    public void setMeId(Long meId) {
        this.meId = meId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }
}
