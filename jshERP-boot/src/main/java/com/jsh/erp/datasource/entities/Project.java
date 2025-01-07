package com.jsh.erp.datasource.entities;

public class Project {
    private Long id;

    private String type;

    private String name;

    private String customerStr;

    private String usersStr;

    private Long customer;

    private String users;

    private Long tenantId;

    private String deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getCustomer() {
        return customer;
    }

    public void setCustomer(Long customer) {
        this.customer = customer;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getCustomerStr() {
        return customerStr;
    }

    public void setCustomerStr(String customerStr) {
        this.customerStr = customerStr;
    }

    public String getUsersStr() {
        return usersStr;
    }

    public void setUsersStr(String usersStr) {
        this.usersStr = usersStr;
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
