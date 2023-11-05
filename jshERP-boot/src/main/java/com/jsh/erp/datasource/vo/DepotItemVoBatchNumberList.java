package com.jsh.erp.datasource.vo;


import java.math.BigDecimal;
import java.util.Date;

public class DepotItemVoBatchNumberList {

    private String id;
    private String barCode;
    private String name;
    private String internalId;
    private String model;
    private Long unitId;
    private String commodityUnit;
    private String batchNumber;
    private Date expirationDate;
    private String expirationDateStr;
    private Date operTime;
    private String snList;
    private BigDecimal totalNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getCommodityUnit() {
        return commodityUnit;
    }

    public void setCommodityUnit(String commodityUnit) {
        this.commodityUnit = commodityUnit;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getExpirationDateStr() {
        return expirationDateStr;
    }

    public void setExpirationDateStr(String expirationDateStr) {
        this.expirationDateStr = expirationDateStr;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getSnList() {
        return snList;
    }

    public void setSnList(String snList) {
        this.snList = snList;
    }

    public BigDecimal getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(BigDecimal totalNum) {
        this.totalNum = totalNum;
    }

    @Override
    public String toString() {
        return "id: " + id + "; barCode: " + barCode
                + "; name: " + name + "; internalId: " + internalId
                + "; model: " + model + "; batchNumber: " + batchNumber + "; operTime: " + operTime
                + "; snList: " + snList
                + "; expirationDate: " + expirationDate + "; expirationDateStr: " + expirationDateStr
                + "; totalNum: " + totalNum ;
    }
}
