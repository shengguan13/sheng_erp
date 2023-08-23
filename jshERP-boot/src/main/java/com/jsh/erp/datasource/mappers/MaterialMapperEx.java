package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @Author: cjl
 * @Date: 2019/1/22 14:54
 */
public interface MaterialMapperEx {

    List<MaterialVo4Unit> selectByConditionMaterial(
            @Param("materialParam") String materialParam,
            @Param("color") String color,
            @Param("project") String project,
            @Param("materialOther") String materialOther,
            @Param("weight") String weight,
            @Param("expiryNum") String expiryNum,
            @Param("enabled") String enabled,
            @Param("remark") String remark,
            @Param("idList") List<Long> idList,
            @Param("mpList") String mpList,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByMaterial(
            @Param("materialParam") String materialParam,
            @Param("color") String color,
            @Param("project") String project,
            @Param("materialOther") String materialOther,
            @Param("weight") String weight,
            @Param("expiryNum") String expiryNum,
            @Param("enabled") String enabled,
            @Param("remark") String remark,
            @Param("idList") List<Long> idList,
            @Param("mpList") String mpList);

    Long insertSelectiveEx(Material record);

    List<Unit> findUnitList(@Param("mId") Long mId);

    List<MaterialVo4Unit> findById(@Param("id") Long id);

    List<MaterialVo4Unit> findByIdWithBarCode(@Param("meId") Long meId);

    List<MaterialVo4Unit> findBySelectWithBarCode(@Param("idList") List<Long> idList,
                                                  @Param("q") String q,
                                                  @Param("offset") Integer offset,
                                                  @Param("rows") Integer rows);

    int findBySelectWithBarCodeCount(@Param("idList") List<Long> idList,
                                     @Param("q") String q);

    List<MaterialVo4Unit> exportExcel(
            @Param("materialParam") String materialParam,
            @Param("color") String color,
            @Param("project") String project,
            @Param("weight") String weight,
            @Param("expiryNum") String expiryNum,
            @Param("enabled") String enabled,
            @Param("remark") String remark,
            @Param("idList") List<Long> idList);
    /**
     * 通过产品名称查询产品信息
     * */
    List<Material> findByMaterialName(@Param("name") String name);

    int batchDeleteMaterialByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String ids[]);

    List<MaterialVo4Unit> getMaterialListAll();

    List<Material> getMaterialListByCategoryIds(@Param("categoryIds") String[] categoryIds);

    List<Material> getMaterialListByUnitIds(@Param("unitIds") String[] unitIds);

    List<MaterialVo4Unit> getMaterialByMeIdList(@Param("meIdList") List<Long> meIdList);

    List<MaterialVo4Unit> getMaterialByMeId(
            @Param("meId") Long meId);

    List<String> getMaterialNameList();

    int setUnitIdToNull(@Param("id") Long id);

    int setExpiryNumToNull(@Param("id") Long id);

    List<MaterialVo4Unit> getMaterialByBarCode(@Param("barCodeArray") String [] barCodeArray);

    List<MaterialVo4Unit> getMaterialByBarCodeAndWithOutMId(
            @Param("barCodeArray") String [] barCodeArray,
            @Param("mId") Long mId);

    List<MaterialInitialStockWithMaterial> getInitialStockWithMaterial(
            @Param("depotList") List<Long> depotList);

    List<MaterialVo4Unit> getListWithStock(
            @Param("depotList") List<Long> depotList,
            @Param("idList") List<Long> idList,
            @Param("materialParam") String materialParam,
            @Param("zeroStock") Integer zeroStock,
            @Param("column") String column,
            @Param("order") String order,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    int getListWithStockCount(
            @Param("depotList") List<Long> depotList,
            @Param("idList") List<Long> idList,
            @Param("materialParam") String materialParam,
            @Param("zeroStock") Integer zeroStock);

    MaterialVo4Unit getTotalStockAndPrice(
            @Param("depotList") List<Long> depotList,
            @Param("idList") List<Long> idList,
            @Param("materialParam") String materialParam);

    int checkIsExist(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("model") String model,
            @Param("color") String color,
            @Param("project") String project,
            @Param("internalId") String internalId,
            @Param("mfrs") String mfrs,
            @Param("otherField1") String otherField1,
            @Param("otherField2") String otherField2,
            @Param("otherField3") String otherField3,
            @Param("otherField4") String otherField4,
            @Param("otherField5") String otherField5,
            @Param("otherField6") String otherField6,
            @Param("otherField7") String otherField7,
            @Param("otherField8") String otherField8,
            @Param("otherField9") String otherField9,
            @Param("otherField10") String otherField10,
            @Param("otherField11") String otherField11,
            @Param("otherField12") String otherField12,
            @Param("unit") String unit,
            @Param("unitId") Long unitId);
}
