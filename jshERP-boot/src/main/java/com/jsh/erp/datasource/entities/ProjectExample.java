package com.jsh.erp.datasource.entities;

import java.util.ArrayList;
import java.util.List;

public class ProjectExample {

    protected String orderByClause;

    protected boolean distinct;

    protected List<ProjectExample.Criteria> oredCriteria;

    public ProjectExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<ProjectExample.Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(ProjectExample.Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public ProjectExample.Criteria or() {
        ProjectExample.Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public ProjectExample.Criteria createCriteria() {
        ProjectExample.Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected ProjectExample.Criteria createCriteriaInternal() {
        ProjectExample.Criteria criteria = new ProjectExample.Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<ProjectExample.Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<ProjectExample.Criterion> getAllCriteria() {
            return criteria;
        }

        public List<ProjectExample.Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new ProjectExample.Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new ProjectExample.Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new ProjectExample.Criterion(condition, value1, value2));
        }

        public ProjectExample.Criteria andIdIsNull() {
            addCriterion("id is null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeGreaterThan(String value) {
            addCriterion("type >", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("type >=", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeLessThan(String value) {
            addCriterion("type <", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("type <=", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeBetween(String value1, String value2) {
            addCriterion("type between", value1, value2, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("type not between", value1, value2, "type");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameIsNull() {
            addCriterion("name is null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdIsNull() {
            addCriterion("tenant_id is null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdIsNotNull() {
            addCriterion("tenant_id is not null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdEqualTo(Long value) {
            addCriterion("tenant_id =", value, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdNotEqualTo(Long value) {
            addCriterion("tenant_id <>", value, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdGreaterThan(Long value) {
            addCriterion("tenant_id >", value, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdGreaterThanOrEqualTo(Long value) {
            addCriterion("tenant_id >=", value, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdLessThan(Long value) {
            addCriterion("tenant_id <", value, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdLessThanOrEqualTo(Long value) {
            addCriterion("tenant_id <=", value, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdIn(List<Long> values) {
            addCriterion("tenant_id in", values, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdNotIn(List<Long> values) {
            addCriterion("tenant_id not in", values, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdBetween(Long value1, Long value2) {
            addCriterion("tenant_id between", value1, value2, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andTenantIdNotBetween(Long value1, Long value2) {
            addCriterion("tenant_id not between", value1, value2, "tenantId");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagIsNull() {
            addCriterion("delete_flag is null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagIsNotNull() {
            addCriterion("delete_flag is not null");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagEqualTo(String value) {
            addCriterion("delete_flag =", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagNotEqualTo(String value) {
            addCriterion("delete_flag <>", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagGreaterThan(String value) {
            addCriterion("delete_flag >", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagGreaterThanOrEqualTo(String value) {
            addCriterion("delete_flag >=", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagLessThan(String value) {
            addCriterion("delete_flag <", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagLessThanOrEqualTo(String value) {
            addCriterion("delete_flag <=", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagLike(String value) {
            addCriterion("delete_flag like", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagNotLike(String value) {
            addCriterion("delete_flag not like", value, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagIn(List<String> values) {
            addCriterion("delete_flag in", values, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagNotIn(List<String> values) {
            addCriterion("delete_flag not in", values, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagBetween(String value1, String value2) {
            addCriterion("delete_flag between", value1, value2, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }

        public ProjectExample.Criteria andDeleteFlagNotBetween(String value1, String value2) {
            addCriterion("delete_flag not between", value1, value2, "deleteFlag");
            return (ProjectExample.Criteria) this;
        }
    }

    public static class Criteria extends ProjectExample.GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
