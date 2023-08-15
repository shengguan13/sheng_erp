package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.MaterialBom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialBomMapperEx {
    List<MaterialBom> selectMaterialBom(
            @Param("process") String process,
            @Param("project") String project,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    List<MaterialBom> selectMaterialBomByPrefix(
            @Param("process") String process,
            @Param("project") String project);

    Long countsByBom(
            @Param("process") String process,
            @Param("project") String project);

    int batchDeleteMaterialBomByIds(
            @Param("ids") String ids[]);
}
