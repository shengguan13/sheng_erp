package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class MaterialBom {

    private Long id;

    private String barCode;

    private String parent;

    private String parentStr;

    private String upper;

    private String project;

    private String partNo;

    private String department;

    private String source;

    private BigDecimal processUsage;

    private String unit;

    private String remark;

    private Long tenantId;

    private String deleteFlag;

    private String sendEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParentStr() {
        return parentStr;
    }

    public void setParentStr(String parentStr) {
        this.parentStr = parentStr;
    }

    public String getUpper() {
        return upper;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getProcessUsage() {
        return processUsage;
    }

    public void setProcessUsage(BigDecimal processUsage) {
        this.processUsage = processUsage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public MaterialBom duplicate() {
        MaterialBom newBom = new MaterialBom();
        // Id 设置成null防止和原对象冲突
        newBom.setId(null);
        newBom.setBarCode(this.barCode);
        newBom.setParent(this.parent);
        newBom.setParentStr(this.parentStr);
        newBom.setUpper(this.upper);
        newBom.setProject(this.project);
        newBom.setDepartment(this.department);
        newBom.setSource(this.source);
        newBom.setProcessUsage(this.processUsage);
        newBom.setUnit(this.unit);
        newBom.setRemark(this.remark);
        newBom.setDeleteFlag(this.deleteFlag);
        newBom.setSendEmail(this.sendEmail);
        return newBom;
    }
}
