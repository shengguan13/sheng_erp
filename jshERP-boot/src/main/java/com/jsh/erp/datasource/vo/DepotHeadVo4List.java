package com.jsh.erp.datasource.vo;

import com.jsh.erp.datasource.entities.DepotHead;

import java.math.BigDecimal;
import java.util.Date;

public class DepotHeadVo4List extends DepotHead{

    private String projectName;

    private String organName;

    private String userName;

    private String accountName;

    private String allocationProjectName;

    private String materialsList;

    private String salesManStr;

    private String operTimeStr;

    private String createTimeStr;

    private String planStartTimeStr;

    private String planFinishTimeStr;

    private Long workHour;

    private BigDecimal finishDebt;

    private String depotHeadType;

    private String creatorName;

    private String contacts;

    private String telephone;

    private String address;

    private BigDecimal finishDeposit;

    private BigDecimal needDebt;

    private BigDecimal debt;

    private BigDecimal materialCount;

    /**
     * 是否有二阶关联
     */
    private Boolean hasFinancialFlag;
    /**
     * 是否有一阶关联
     */
    private Boolean hasBackFlag;

    /**
     * 实际欠款
     */
    private BigDecimal realNeedDebt;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAllocationProjectName() {
        return allocationProjectName;
    }

    public void setAllocationProjectName(String allocationProjectName) {
        this.allocationProjectName = allocationProjectName;
    }

    public String getMaterialsList() {
        return materialsList;
    }

    public void setMaterialsList(String materialsList) {
        this.materialsList = materialsList;
    }

    public String getSalesManStr() {
        return salesManStr;
    }

    public void setSalesManStr(String salesManStr) {
        this.salesManStr = salesManStr;
    }

    public String getOperTimeStr() {
        return operTimeStr;
    }

    public void setOperTimeStr(String operTimeStr) {
        this.operTimeStr = operTimeStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getPlanStartTimeStr() {
        return planStartTimeStr;
    }

    public void setPlanStartTimeStr(String planStartTimeStr) {
        this.planStartTimeStr = planStartTimeStr;
    }

    public String getPlanFinishTimeStr() {
        return planFinishTimeStr;
    }

    public void setPlanFinishTimeStr(String planFinishTimeStr) {
        this.planFinishTimeStr = planFinishTimeStr;
    }

    public Long getWorkHour() {
        return workHour;
    }

    public void setWorkHour(Long workHour) {
        this.workHour = workHour;
    }

    public BigDecimal getFinishDebt() {
        return finishDebt;
    }

    public void setFinishDebt(BigDecimal finishDebt) {
        this.finishDebt = finishDebt;
    }

    public String getDepotHeadType() {
        return depotHeadType;
    }

    public void setDepotHeadType(String depotHeadType) {
        this.depotHeadType = depotHeadType;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getFinishDeposit() {
        return finishDeposit;
    }

    public void setFinishDeposit(BigDecimal finishDeposit) {
        this.finishDeposit = finishDeposit;
    }

    public BigDecimal getNeedDebt() {
        return needDebt;
    }

    public void setNeedDebt(BigDecimal needDebt) {
        this.needDebt = needDebt;
    }

    public BigDecimal getDebt() {
        return debt;
    }

    public void setDebt(BigDecimal debt) {
        this.debt = debt;
    }

    public BigDecimal getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(BigDecimal materialCount) {
        this.materialCount = materialCount;
    }

    public Boolean getHasFinancialFlag() {
        return hasFinancialFlag;
    }

    public void setHasFinancialFlag(Boolean hasFinancialFlag) {
        this.hasFinancialFlag = hasFinancialFlag;
    }

    public Boolean getHasBackFlag() {
        return hasBackFlag;
    }

    public void setHasBackFlag(Boolean hasBackFlag) {
        this.hasBackFlag = hasBackFlag;
    }

    public BigDecimal getRealNeedDebt() {
        return realNeedDebt;
    }

    public void setRealNeedDebt(BigDecimal realNeedDebt) {
        this.realNeedDebt = realNeedDebt;
    }
}