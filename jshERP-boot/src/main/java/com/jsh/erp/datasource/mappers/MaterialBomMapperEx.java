package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.MaterialBomVo4Info;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialBomMapperEx {
    List<MaterialBomVo4Info> selectMaterialBom(
            @Param("process") String process,
            @Param("project") String project,
            @Param("materialParam") String materialParam,
            @Param("idList") List<Long> idList,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    List<MaterialBomVo4Info> selectMaterialBomByPrefix(
            @Param("process") String process,
            @Param("project") String project);

    Long countsByBom(
            @Param("process") String process,
            @Param("project") String project,
            @Param("materialParam") String materialParam,
            @Param("idList") List<Long> idList);

    int batchDeleteMaterialBomByIds(
            @Param("ids") String ids[]);
}
