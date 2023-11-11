package com.jsh.erp.service.depotAllocation;

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

@Service(value = "depotAllocation_component")
@DepotAllocationResource
public class DepotAllocationComponent implements ICommonQuery {
    @Resource
    private DepotAllocationService depotAllocationService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return depotAllocationService.getDepotAllocation(id);
    }

    @Override
    public List<?> select(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String depotId = StringUtil.getInfo(search, "depotId");
        String type = StringUtil.getInfo(search, "type");
        String allocation = StringUtil.getInfo(search, "allocation");
        return depotAllocationService.select(depotId, type, allocation, QueryUtils.offset(parameterMap), QueryUtils.rows(parameterMap));
    }

    @Override
    public Long counts(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String depotId = StringUtil.getInfo(search, "depotId");
        String type = StringUtil.getInfo(search, "type");
        String allocation = StringUtil.getInfo(search, "allocation");
        return depotAllocationService.countDepotAllocation(depotId, type, allocation);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception {
        return depotAllocationService.insertDepotAllocation(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request) throws Exception {
        return depotAllocationService.updateDepotAllocation(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request) throws Exception {
        return depotAllocationService.deleteDepotAllocation(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request) throws Exception {
        return depotAllocationService.batchDeleteDepotAllocation(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name) throws Exception {
        return depotAllocationService.checkIsNameExist(id, name);
    }
}
