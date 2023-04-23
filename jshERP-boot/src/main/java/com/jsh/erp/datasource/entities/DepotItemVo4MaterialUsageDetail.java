package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class DepotItemVo4MaterialUsageDetail {

    private Long materialId;

    private String barCode;

    private String materialName;

    private BigDecimal useNum;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public BigDecimal getUseNum() {
        return useNum;
    }

    public void setUseNum(BigDecimal useNum) {
        this.useNum = useNum;
    }

    @Override
    public String toString() {
        return materialId+":"+materialName+":"+barCode+":"+useNum;
    }
}
