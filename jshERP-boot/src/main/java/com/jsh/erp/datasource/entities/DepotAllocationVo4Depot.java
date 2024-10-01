package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

public class DepotAllocationVo4Depot extends DepotAllocation {
    private String depotName;

    private BigDecimal totalNumber;

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }
}
