package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.MaterialBom;
import com.jsh.erp.datasource.entities.MaterialBomExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialBomMapper {
    long countByExample(MaterialBomExample example);

    int deleteByExample(MaterialBomExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MaterialBom record);

    int insertSelective(MaterialBom record);

    List<MaterialBom> selectByExample(MaterialBomExample example);

    MaterialBom selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MaterialBom record, @Param("example") MaterialBomExample example);

    int updateByExample(@Param("record") MaterialBom record, @Param("example") MaterialBomExample example);

    int updateByPrimaryKeySelective(MaterialBom record);

    int updateByPrimaryKey(MaterialBom record);
}
