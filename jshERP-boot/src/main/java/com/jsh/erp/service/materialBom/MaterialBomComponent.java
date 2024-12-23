package com.jsh.erp.service.materialBom;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.ICommonQuery;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.QueryUtils;
import com.jsh.erp.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service(value = "materialBom_component")
@MaterialBomResource
public class MaterialBomComponent implements ICommonQuery {

    @Resource
    private MaterialBomService materialBomService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return materialBomService.getMaterialBom(id);
    }

    @Override
    public List<?> select(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String categoryId = StringUtil.getInfo(search, "categoryId");
        String parent = StringUtil.getInfo(search, "parent");
        String project = StringUtil.getInfo(search, "project");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        return materialBomService.select(categoryId, parent, project, materialParam,
                QueryUtils.offset(parameterMap), QueryUtils.rows(parameterMap));
    }

    @Override
    public Long counts(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String categoryId = StringUtil.getInfo(search, "categoryId");
        String parent = StringUtil.getInfo(search, "parent");
        String upper = StringUtil.getInfo(search, "upper");
        String project = StringUtil.getInfo(search, "project");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        return materialBomService.countMaterialBom(categoryId, parent, upper, project, materialParam);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception {
        return materialBomService.insertMaterialBom(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request) throws Exception {
        return materialBomService.updateMaterialBom(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request) throws Exception {
        return materialBomService.deleteMaterialBom(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request) throws Exception {
        return materialBomService.batchDeleteMaterialBom(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name) throws Exception {
        return materialBomService.checkIsNameExist(id, name);
    }
}
