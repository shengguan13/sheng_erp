package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductSupplierVo4Info;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductSupplierMapperEx {
    List<ProductSupplierVo4Info> selectProductSupplier(@Param("supplierId") String supplierId,
                                                       @Param("keyword") String keyword,
                                                       @Param("offset") Integer offset,
                                                       @Param("rows") Integer rows);

    List<ProductSupplierVo4Info> selectProductSupplierByBarCode(@Param("name") String name,
                                                                @Param("barCode") String barCode);

    List<ProductSupplierVo4Info> selectByIds(@Param("ids") String[] ids);

    Long countProductSupplier(@Param("supplierId") String supplierId,
                              @Param("keyword") String keyword);

    int batchDeleteProductSupplierByIds(@Param("ids") String[] ids);
}
