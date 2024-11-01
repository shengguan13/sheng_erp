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
