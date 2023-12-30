package com.jsh.erp.service.productSupplier;

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

@Service(value = "productSupplier_component")
@ProductSupplierResource
public class ProductSupplierComponent implements ICommonQuery {
    @Resource
    private ProductSupplierService productSupplierService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return productSupplierService.getProductSupplier(id);
    }

    @Override
    public List<?> select(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String supplierId = StringUtil.getInfo(search, "supplierId");
        String keyword = StringUtil.getInfo(search, "keyword");
        return productSupplierService.select(supplierId, keyword, QueryUtils.offset(parameterMap), QueryUtils.rows(parameterMap));
    }

    @Override
    public Long counts(Map<String, String> parameterMap) throws Exception {
        String search = parameterMap.get(Constants.SEARCH);
        String supplierId = StringUtil.getInfo(search, "supplierId");
        String keyword = StringUtil.getInfo(search, "keyword");
        return productSupplierService.countProductSupplier(supplierId, keyword);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception {
        return productSupplierService.insertProductSupplier(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request) throws Exception {
        return productSupplierService.updateProductSupplier(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request) throws Exception {
        return productSupplierService.deleteProductSupplier(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request) throws Exception {
        return productSupplierService.batchDeleteProductSupplier(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name) throws Exception {
        return productSupplierService.checkIsNameExist(id, name);
    }
}
