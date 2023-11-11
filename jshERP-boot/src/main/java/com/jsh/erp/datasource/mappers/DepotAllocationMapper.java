package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.DepotAllocationExample;
import com.jsh.erp.datasource.entities.DepotAllocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepotAllocationMapper {
    long countByExample(DepotAllocationExample example);

    int deleteByExample(DepotAllocationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DepotAllocation record);

    int insertSelective(DepotAllocation record);

    List<DepotAllocation> selectByExample(DepotAllocationExample example);

    DepotAllocation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DepotAllocation record, @Param("example") DepotAllocationExample example);

    int updateByExample(@Param("record") DepotAllocation record, @Param("example") DepotAllocationExample example);

    int updateByPrimaryKeySelective(DepotAllocation record);

    int updateByPrimaryKey(DepotAllocation record);
}
