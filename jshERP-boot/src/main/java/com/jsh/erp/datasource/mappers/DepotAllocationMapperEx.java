package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.DepotAllocationVo4Depot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepotAllocationMapperEx {

    List<DepotAllocationVo4Depot> selectDepotAllocation(@Param("depotId") String depotId,
                                                        @Param("type") String type,
                                                        @Param("allocation") String allocation,
                                                        @Param("offset") int offset,
                                                        @Param("rows") int rows);

    Long countDepotAllocation(@Param("depotId") String depotId,
                              @Param("type") String type,
                              @Param("allocation") String allocation);

    int batchDeleteDepotAllocationByIds(@Param("ids") String[] ids);
}
