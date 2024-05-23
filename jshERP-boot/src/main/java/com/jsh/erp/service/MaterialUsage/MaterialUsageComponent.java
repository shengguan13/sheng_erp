package com.jsh.erp.service.MaterialUsage;

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

@Service(value = "materialUsage_component")
@MaterialUsageResource
public class MaterialUsageComponent implements ICommonQuery {
    @Resource
    private MaterialUsageService materialUsageService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return materialUsageService.getMaterialUsage(id);
    }

    @Override
    public List<?> select(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String categoryId = StringUtil.getInfo(search, "categoryId");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        return materialUsageService.select(categoryId, materialParam,
                QueryUtils.offset(parameterMap), QueryUtils.rows(parameterMap));
    }

    @Override
    public Long counts(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String categoryId = StringUtil.getInfo(search, "categoryId");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        return materialUsageService.countMaterialUsage(categoryId, materialParam);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception {
        return materialUsageService.insertMaterialUsage(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request) throws Exception {
        return materialUsageService.updateMaterialUsage(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request) throws Exception {
        return materialUsageService.deleteMaterialUsage(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request) throws Exception {
        return materialUsageService.batchDeleteMaterialUsage(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name) throws Exception {
        return 0;
    }
}
