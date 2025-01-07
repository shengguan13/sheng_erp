package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectMapperEx {
    List<Project> selectByConditionProject(
            @Param("name") String name,
            @Param("type") String type,
            @Param("customer") String customer,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByProject(
            @Param("name") String name,
            @Param("type") String type,
            @Param("customer") String customer);

    int batchDeleteProjectByIds(@Param("ids") String ids[]);
}
