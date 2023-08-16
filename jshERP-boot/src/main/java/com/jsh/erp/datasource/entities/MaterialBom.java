package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class MaterialBom {

    private Long id;

    private String barCode;

    private String process;

    private String project;

    private String partNo;

    private String department;

    private BigDecimal processUsage;

    private String unit;

    private String remark;

    private BigDecimal amountPerCar;

    private Long tenantId;

    private String deleteFlag;

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

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
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

    public BigDecimal getAmountPerCar() {
        return amountPerCar;
    }

    public void setAmountPerCar(BigDecimal amountPerCar) {
        this.amountPerCar = amountPerCar;
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
