package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.MaterialBomVo4Info;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialBomMapperEx {
    List<MaterialBomVo4Info> selectMaterialBomNullUpper(
            @Param("parent") String parent,
            @Param("project") String project,
            @Param("materialParam") String materialParam,
            @Param("idList") List<Long> idList,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    List<MaterialBomVo4Info> selectMaterialBomWithUpper(
            @Param("parent") String parent,
            @Param("upper") String upper,
            @Param("project") String project,
            @Param("materialParam") String materialParam);

    List<MaterialBomVo4Info> getMaterialBomTree(
            @Param("parent") String parent,
            @Param("barCode") String barCode,
            @Param("project") String project);

    List<MaterialBomVo4Info> getMaterialBomTreeWithUpper(
            @Param("parent") String parent,
            @Param("upper") String upper,
            @Param("barCode") String barCode,
            @Param("project") String project);

    List<MaterialBomVo4Info> selectNextLevelBomByPrefix(
            @Param("parent") String parent,
            @Param("upper") String upper,
            @Param("project") String project);

    List<MaterialBomVo4Info> getMaterialBomByBarCode(
            @Param("barCodeArray") String barCodeArray[]);

    Long countMaterialBomNullUpper(
            @Param("parent") String parent,
            @Param("project") String project,
            @Param("materialParam") String materialParam,
            @Param("idList") List<Long> idList);

    int batchDeleteMaterialBomByIds(
            @Param("ids") String ids[]);
}
