package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.MaterialInitialStockVo4Info;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialUsageMapper {
    MaterialInitialStockVo4Info selectByPrimaryKey(long id);

    List<MaterialInitialStockVo4Info> selectAllUsage();

    List<MaterialInitialStockVo4Info> selectMaterialUsage(@Param("materialParam") String materialParam,
                                                          @Param("idList") List<Long> idList,
                                                          @Param("offset") int offset,
                                                          @Param("rows") int rows);

    Long countMaterialUsage(@Param("materialParam") String materialParam,
                            @Param("idList") List<Long> idList);

    int batchDeleteMaterialUsageByIds(@Param("ids") String[] ids);
}
