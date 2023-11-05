package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsh.erp.datasource.entities.Depot;
import com.jsh.erp.datasource.entities.DepotEx;
import com.jsh.erp.datasource.entities.MaterialInitialStock;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.userBusiness.UserBusinessService;
import com.jsh.erp.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author ji sheng hua 752*718*920
 */
@RestController
@RequestMapping(value = "/depot")
@Api(tags = {"仓库管理"})
public class DepotController {
    private static final String ALLOCATION = "{\"depotAllocation\":[{\"allocation\":\"A1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"A1-2\",\"type\":\"辅料\"},{\"allocation\":\"A1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"A2-1\",\"type\":\"辅料\"},{\"allocation\":\"A2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"A2-3\",\"type\":\"辅料\"},{\"allocation\":\"A3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"A3-2\",\"type\":\"辅料\"},{\"allocation\":\"A3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"A4-1\",\"type\":\"辅料\"},{\"allocation\":\"A4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"A4-3\",\"type\":\"辅料\"},{\"allocation\":\"B1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"B1-2\",\"type\":\"辅料\"},{\"allocation\":\"B1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"B2-1\",\"type\":\"辅料\"},{\"allocation\":\"B2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"B2-3\",\"type\":\"辅料\"},{\"allocation\":\"B3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"B3-2\",\"type\":\"辅料\"},{\"allocation\":\"B3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"B4-1\",\"type\":\"辅料\"},{\"allocation\":\"B4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"B4-3\",\"type\":\"辅料\"},{\"allocation\":\"C1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"C1-2\",\"type\":\"辅料\"},{\"allocation\":\"C1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"C2-1\",\"type\":\"辅料\"},{\"allocation\":\"C2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"C2-3\",\"type\":\"辅料\"},{\"allocation\":\"C3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"C3-2\",\"type\":\"辅料\"},{\"allocation\":\"C3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"C4-1\",\"type\":\"辅料\"},{\"allocation\":\"C4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"C4-3\",\"type\":\"辅料\"},{\"allocation\":\"D1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"D1-2\",\"type\":\"辅料\"},{\"allocation\":\"D1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"D2-1\",\"type\":\"辅料\"},{\"allocation\":\"D2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"D2-3\",\"type\":\"辅料\"},{\"allocation\":\"D3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"D3-2\",\"type\":\"辅料\"},{\"allocation\":\"D3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"D4-1\",\"type\":\"辅料\"},{\"allocation\":\"D4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"D4-3\",\"type\":\"辅料\"},{\"allocation\":\"E1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"E1-2\",\"type\":\"辅料\"},{\"allocation\":\"E1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"E2-1\",\"type\":\"辅料\"},{\"allocation\":\"E2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"E2-3\",\"type\":\"辅料\"},{\"allocation\":\"E3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"E3-2\",\"type\":\"辅料\"},{\"allocation\":\"E3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"E4-1\",\"type\":\"辅料\"},{\"allocation\":\"E4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"E4-3\",\"type\":\"辅料\"},{\"allocation\":\"F1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"F1-2\",\"type\":\"辅料\"},{\"allocation\":\"F1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"F2-1\",\"type\":\"辅料\"},{\"allocation\":\"F2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"F2-3\",\"type\":\"辅料\"},{\"allocation\":\"F3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"F3-2\",\"type\":\"辅料\"},{\"allocation\":\"F3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"F4-1\",\"type\":\"辅料\"},{\"allocation\":\"F4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"F4-3\",\"type\":\"辅料\"},{\"allocation\":\"G1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"G1-2\",\"type\":\"辅料\"},{\"allocation\":\"G1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"G2-1\",\"type\":\"辅料\"},{\"allocation\":\"G2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"G2-3\",\"type\":\"辅料\"},{\"allocation\":\"G3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"G3-2\",\"type\":\"辅料\"},{\"allocation\":\"G3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"G4-1\",\"type\":\"辅料\"},{\"allocation\":\"G4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"G4-3\",\"type\":\"辅料\"},{\"allocation\":\"H1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"H1-2\",\"type\":\"辅料\"},{\"allocation\":\"H1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"H2-1\",\"type\":\"辅料\"},{\"allocation\":\"H2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"H2-3\",\"type\":\"辅料\"},{\"allocation\":\"H3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"H3-2\",\"type\":\"辅料\"},{\"allocation\":\"H3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"H4-1\",\"type\":\"辅料\"},{\"allocation\":\"H4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"H4-3\",\"type\":\"辅料\"},{\"allocation\":\"I1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"I1-2\",\"type\":\"辅料\"},{\"allocation\":\"I1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"I2-1\",\"type\":\"辅料\"},{\"allocation\":\"I2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"I2-3\",\"type\":\"辅料\"},{\"allocation\":\"I3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"I3-2\",\"type\":\"辅料\"},{\"allocation\":\"I3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"I4-1\",\"type\":\"辅料\"},{\"allocation\":\"I4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"I4-3\",\"type\":\"辅料\"},{\"allocation\":\"J1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"J1-2\",\"type\":\"辅料\"},{\"allocation\":\"J1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"J2-1\",\"type\":\"辅料\"},{\"allocation\":\"J2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"J2-3\",\"type\":\"辅料\"},{\"allocation\":\"J3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"J3-2\",\"type\":\"辅料\"},{\"allocation\":\"J3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"J4-1\",\"type\":\"辅料\"},{\"allocation\":\"J4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"J4-3\",\"type\":\"辅料\"},{\"allocation\":\"K1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"K1-2\",\"type\":\"辅料\"},{\"allocation\":\"K1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"K2-1\",\"type\":\"辅料\"},{\"allocation\":\"K2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"K2-3\",\"type\":\"辅料\"},{\"allocation\":\"K3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"K3-2\",\"type\":\"辅料\"},{\"allocation\":\"K3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"K4-1\",\"type\":\"辅料\"},{\"allocation\":\"K4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"K4-3\",\"type\":\"辅料\"},{\"allocation\":\"L1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"L1-2\",\"type\":\"辅料\"},{\"allocation\":\"L1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"L2-1\",\"type\":\"辅料\"},{\"allocation\":\"L2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"L2-3\",\"type\":\"辅料\"},{\"allocation\":\"L3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"L3-2\",\"type\":\"辅料\"},{\"allocation\":\"L3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"L4-1\",\"type\":\"辅料\"},{\"allocation\":\"L4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"L4-3\",\"type\":\"辅料\"},{\"allocation\":\"M1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"M1-2\",\"type\":\"辅料\"},{\"allocation\":\"M1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"M2-1\",\"type\":\"辅料\"},{\"allocation\":\"M2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"M2-3\",\"type\":\"辅料\"},{\"allocation\":\"M3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"M3-2\",\"type\":\"辅料\"},{\"allocation\":\"M3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"M4-1\",\"type\":\"辅料\"},{\"allocation\":\"M4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"M4-3\",\"type\":\"辅料\"},{\"allocation\":\"N1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"N1-2\",\"type\":\"辅料\"},{\"allocation\":\"N1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"N2-1\",\"type\":\"辅料\"},{\"allocation\":\"N2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"N2-3\",\"type\":\"辅料\"},{\"allocation\":\"N3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"N3-2\",\"type\":\"辅料\"},{\"allocation\":\"N3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"N4-1\",\"type\":\"辅料\"},{\"allocation\":\"N4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"N4-3\",\"type\":\"辅料\"},{\"allocation\":\"O1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"O1-2\",\"type\":\"辅料\"},{\"allocation\":\"O1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"O2-1\",\"type\":\"辅料\"},{\"allocation\":\"O2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"O2-3\",\"type\":\"辅料\"},{\"allocation\":\"O3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"O3-2\",\"type\":\"辅料\"},{\"allocation\":\"O3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"O4-1\",\"type\":\"辅料\"},{\"allocation\":\"O4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"O4-3\",\"type\":\"辅料\"},{\"allocation\":\"P1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"P1-2\",\"type\":\"辅料\"},{\"allocation\":\"P1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"P2-1\",\"type\":\"辅料\"},{\"allocation\":\"P2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"P2-3\",\"type\":\"辅料\"},{\"allocation\":\"P3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"P3-2\",\"type\":\"辅料\"},{\"allocation\":\"P3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"P4-1\",\"type\":\"辅料\"},{\"allocation\":\"P4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"P4-3\",\"type\":\"辅料\"},{\"allocation\":\"Q1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"Q1-2\",\"type\":\"辅料\"},{\"allocation\":\"Q1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"Q2-1\",\"type\":\"辅料\"},{\"allocation\":\"Q2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"Q2-3\",\"type\":\"辅料\"},{\"allocation\":\"Q3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"Q3-2\",\"type\":\"辅料\"},{\"allocation\":\"Q3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"Q4-1\",\"type\":\"辅料\"},{\"allocation\":\"Q4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"Q4-3\",\"type\":\"辅料\"},{\"allocation\":\"R1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"R1-2\",\"type\":\"辅料\"},{\"allocation\":\"R1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"R2-1\",\"type\":\"辅料\"},{\"allocation\":\"R2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"R2-3\",\"type\":\"辅料\"},{\"allocation\":\"R3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"R3-2\",\"type\":\"辅料\"},{\"allocation\":\"R3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"R4-1\",\"type\":\"辅料\"},{\"allocation\":\"R4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"R4-3\",\"type\":\"辅料\"},{\"allocation\":\"S1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"S1-2\",\"type\":\"辅料\"},{\"allocation\":\"S1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"S2-1\",\"type\":\"辅料\"},{\"allocation\":\"S2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"S2-3\",\"type\":\"辅料\"},{\"allocation\":\"S3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"S3-2\",\"type\":\"辅料\"},{\"allocation\":\"S3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"S4-1\",\"type\":\"辅料\"},{\"allocation\":\"S4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"S4-3\",\"type\":\"辅料\"},{\"allocation\":\"T1-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"T1-2\",\"type\":\"辅料\"},{\"allocation\":\"T1-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"T2-1\",\"type\":\"辅料\"},{\"allocation\":\"T2-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"T2-3\",\"type\":\"辅料\"},{\"allocation\":\"T3-1\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"T3-2\",\"type\":\"辅料\"},{\"allocation\":\"T3-3\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"T4-1\",\"type\":\"辅料\"},{\"allocation\":\"T4-2\",\"type\":\"辅料\"}," +
            "{\"allocation\":\"T4-3\",\"type\":\"辅料\"},{\"allocation\":\"隔离\",\"type\":\"隔离\"}," +
            "{\"allocation\":\"外检区\",\"type\":\"外检区\"},{\"allocation\":\"供应商\",\"type\":\"供应商\"}," +
            "{\"allocation\":\"项目\",\"type\":\"项目\"},{\"allocation\":\"A&B\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B&C\",\"type\":\"原材料\"},{\"allocation\":\"C&D\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-1\",\"type\":\"原材料\"},{\"allocation\":\"A1-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-3\",\"type\":\"原材料\"},{\"allocation\":\"A1-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-5\",\"type\":\"原材料\"},{\"allocation\":\"A1-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-7\",\"type\":\"原材料\"},{\"allocation\":\"A1-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-9\",\"type\":\"原材料\"},{\"allocation\":\"A1-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-11\",\"type\":\"原材料\"},{\"allocation\":\"A1-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-13\",\"type\":\"原材料\"},{\"allocation\":\"A1-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-15\",\"type\":\"原材料\"},{\"allocation\":\"A1-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-17\",\"type\":\"原材料\"},{\"allocation\":\"A1-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A1-19\",\"type\":\"原材料\"},{\"allocation\":\"A1-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-1\",\"type\":\"原材料\"},{\"allocation\":\"A2-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-3\",\"type\":\"原材料\"},{\"allocation\":\"A2-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-5\",\"type\":\"原材料\"},{\"allocation\":\"A2-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-7\",\"type\":\"原材料\"},{\"allocation\":\"A2-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-9\",\"type\":\"原材料\"},{\"allocation\":\"A2-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-11\",\"type\":\"原材料\"},{\"allocation\":\"A2-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-13\",\"type\":\"原材料\"},{\"allocation\":\"A2-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-15\",\"type\":\"原材料\"},{\"allocation\":\"A2-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-17\",\"type\":\"原材料\"},{\"allocation\":\"A2-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A2-19\",\"type\":\"原材料\"},{\"allocation\":\"A2-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-1\",\"type\":\"原材料\"},{\"allocation\":\"A3-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-3\",\"type\":\"原材料\"},{\"allocation\":\"A3-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-5\",\"type\":\"原材料\"},{\"allocation\":\"A3-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-7\",\"type\":\"原材料\"},{\"allocation\":\"A3-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-9\",\"type\":\"原材料\"},{\"allocation\":\"A3-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-11\",\"type\":\"原材料\"},{\"allocation\":\"A3-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-13\",\"type\":\"原材料\"},{\"allocation\":\"A3-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-15\",\"type\":\"原材料\"},{\"allocation\":\"A3-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-17\",\"type\":\"原材料\"},{\"allocation\":\"A3-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A3-19\",\"type\":\"原材料\"},{\"allocation\":\"A3-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-1\",\"type\":\"原材料\"},{\"allocation\":\"A4-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-3\",\"type\":\"原材料\"},{\"allocation\":\"A4-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-5\",\"type\":\"原材料\"},{\"allocation\":\"A4-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-7\",\"type\":\"原材料\"},{\"allocation\":\"A4-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-9\",\"type\":\"原材料\"},{\"allocation\":\"A4-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-11\",\"type\":\"原材料\"},{\"allocation\":\"A4-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-13\",\"type\":\"原材料\"},{\"allocation\":\"A4-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-15\",\"type\":\"原材料\"},{\"allocation\":\"A4-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-17\",\"type\":\"原材料\"},{\"allocation\":\"A4-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"A4-19\",\"type\":\"原材料\"},{\"allocation\":\"A4-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-1\",\"type\":\"原材料\"},{\"allocation\":\"B1-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-3\",\"type\":\"原材料\"},{\"allocation\":\"B1-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-5\",\"type\":\"原材料\"},{\"allocation\":\"B1-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-7\",\"type\":\"原材料\"},{\"allocation\":\"B1-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-9\",\"type\":\"原材料\"},{\"allocation\":\"B1-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-11\",\"type\":\"原材料\"},{\"allocation\":\"B1-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-13\",\"type\":\"原材料\"},{\"allocation\":\"B1-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-15\",\"type\":\"原材料\"},{\"allocation\":\"B1-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-17\",\"type\":\"原材料\"},{\"allocation\":\"B1-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B1-19\",\"type\":\"原材料\"},{\"allocation\":\"B1-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-1\",\"type\":\"原材料\"},{\"allocation\":\"B2-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-3\",\"type\":\"原材料\"},{\"allocation\":\"B2-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-5\",\"type\":\"原材料\"},{\"allocation\":\"B2-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-7\",\"type\":\"原材料\"},{\"allocation\":\"B2-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-9\",\"type\":\"原材料\"},{\"allocation\":\"B2-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-11\",\"type\":\"原材料\"},{\"allocation\":\"B2-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-13\",\"type\":\"原材料\"},{\"allocation\":\"B2-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-15\",\"type\":\"原材料\"},{\"allocation\":\"B2-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-17\",\"type\":\"原材料\"},{\"allocation\":\"B2-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B2-19\",\"type\":\"原材料\"},{\"allocation\":\"B2-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-1\",\"type\":\"原材料\"},{\"allocation\":\"B3-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-3\",\"type\":\"原材料\"},{\"allocation\":\"B3-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-5\",\"type\":\"原材料\"},{\"allocation\":\"B3-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-7\",\"type\":\"原材料\"},{\"allocation\":\"B3-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-9\",\"type\":\"原材料\"},{\"allocation\":\"B3-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-11\",\"type\":\"原材料\"},{\"allocation\":\"B3-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-13\",\"type\":\"原材料\"},{\"allocation\":\"B3-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-15\",\"type\":\"原材料\"},{\"allocation\":\"B3-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-17\",\"type\":\"原材料\"},{\"allocation\":\"B3-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B3-19\",\"type\":\"原材料\"},{\"allocation\":\"B3-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-1\",\"type\":\"原材料\"},{\"allocation\":\"B4-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-3\",\"type\":\"原材料\"},{\"allocation\":\"B4-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-5\",\"type\":\"原材料\"},{\"allocation\":\"B4-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-7\",\"type\":\"原材料\"},{\"allocation\":\"B4-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-9\",\"type\":\"原材料\"},{\"allocation\":\"B4-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-11\",\"type\":\"原材料\"},{\"allocation\":\"B4-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-13\",\"type\":\"原材料\"},{\"allocation\":\"B4-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-15\",\"type\":\"原材料\"},{\"allocation\":\"B4-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-17\",\"type\":\"原材料\"},{\"allocation\":\"B4-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"B4-19\",\"type\":\"原材料\"},{\"allocation\":\"B4-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-1\",\"type\":\"原材料\"},{\"allocation\":\"C1-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-3\",\"type\":\"原材料\"},{\"allocation\":\"C1-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-5\",\"type\":\"原材料\"},{\"allocation\":\"C1-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-7\",\"type\":\"原材料\"},{\"allocation\":\"C1-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-9\",\"type\":\"原材料\"},{\"allocation\":\"C1-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-11\",\"type\":\"原材料\"},{\"allocation\":\"C1-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-13\",\"type\":\"原材料\"},{\"allocation\":\"C1-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-15\",\"type\":\"原材料\"},{\"allocation\":\"C1-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-17\",\"type\":\"原材料\"},{\"allocation\":\"C1-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C1-19\",\"type\":\"原材料\"},{\"allocation\":\"C1-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-1\",\"type\":\"原材料\"},{\"allocation\":\"C2-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-3\",\"type\":\"原材料\"},{\"allocation\":\"C2-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-5\",\"type\":\"原材料\"},{\"allocation\":\"C2-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-7\",\"type\":\"原材料\"},{\"allocation\":\"C2-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-9\",\"type\":\"原材料\"},{\"allocation\":\"C2-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-11\",\"type\":\"原材料\"},{\"allocation\":\"C2-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-13\",\"type\":\"原材料\"},{\"allocation\":\"C2-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-15\",\"type\":\"原材料\"},{\"allocation\":\"C2-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-17\",\"type\":\"原材料\"},{\"allocation\":\"C2-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C2-19\",\"type\":\"原材料\"},{\"allocation\":\"C2-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-1\",\"type\":\"原材料\"},{\"allocation\":\"C3-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-3\",\"type\":\"原材料\"},{\"allocation\":\"C3-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-5\",\"type\":\"原材料\"},{\"allocation\":\"C3-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-7\",\"type\":\"原材料\"},{\"allocation\":\"C3-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-9\",\"type\":\"原材料\"},{\"allocation\":\"C3-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-11\",\"type\":\"原材料\"},{\"allocation\":\"C3-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-13\",\"type\":\"原材料\"},{\"allocation\":\"C3-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-15\",\"type\":\"原材料\"},{\"allocation\":\"C3-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-17\",\"type\":\"原材料\"},{\"allocation\":\"C3-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C3-19\",\"type\":\"原材料\"},{\"allocation\":\"C3-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-1\",\"type\":\"原材料\"},{\"allocation\":\"C4-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-3\",\"type\":\"原材料\"},{\"allocation\":\"C4-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-5\",\"type\":\"原材料\"},{\"allocation\":\"C4-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-7\",\"type\":\"原材料\"},{\"allocation\":\"C4-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-9\",\"type\":\"原材料\"},{\"allocation\":\"C4-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-11\",\"type\":\"原材料\"},{\"allocation\":\"C4-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-13\",\"type\":\"原材料\"},{\"allocation\":\"C4-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-15\",\"type\":\"原材料\"},{\"allocation\":\"C4-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-17\",\"type\":\"原材料\"},{\"allocation\":\"C4-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"C4-19\",\"type\":\"原材料\"},{\"allocation\":\"C4-20\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-1\",\"type\":\"原材料\"},{\"allocation\":\"D1-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-3\",\"type\":\"原材料\"},{\"allocation\":\"D1-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-5\",\"type\":\"原材料\"},{\"allocation\":\"D1-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-7\",\"type\":\"原材料\"},{\"allocation\":\"D1-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-9\",\"type\":\"原材料\"},{\"allocation\":\"D1-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-11\",\"type\":\"原材料\"},{\"allocation\":\"D1-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-13\",\"type\":\"原材料\"},{\"allocation\":\"D1-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-15\",\"type\":\"原材料\"},{\"allocation\":\"D1-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D1-17\",\"type\":\"原材料\"},{\"allocation\":\"D1-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-1\",\"type\":\"原材料\"},{\"allocation\":\"D2-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-3\",\"type\":\"原材料\"},{\"allocation\":\"D2-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-5\",\"type\":\"原材料\"},{\"allocation\":\"D2-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-7\",\"type\":\"原材料\"},{\"allocation\":\"D2-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-9\",\"type\":\"原材料\"},{\"allocation\":\"D2-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-11\",\"type\":\"原材料\"},{\"allocation\":\"D2-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-13\",\"type\":\"原材料\"},{\"allocation\":\"D2-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-15\",\"type\":\"原材料\"},{\"allocation\":\"D2-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D2-17\",\"type\":\"原材料\"},{\"allocation\":\"D2-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-1\",\"type\":\"原材料\"},{\"allocation\":\"D3-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-3\",\"type\":\"原材料\"},{\"allocation\":\"D3-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-5\",\"type\":\"原材料\"},{\"allocation\":\"D3-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-7\",\"type\":\"原材料\"},{\"allocation\":\"D3-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-9\",\"type\":\"原材料\"},{\"allocation\":\"D3-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-11\",\"type\":\"原材料\"},{\"allocation\":\"D3-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-13\",\"type\":\"原材料\"},{\"allocation\":\"D3-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-15\",\"type\":\"原材料\"},{\"allocation\":\"D3-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D3-17\",\"type\":\"原材料\"},{\"allocation\":\"D3-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-1\",\"type\":\"原材料\"},{\"allocation\":\"D4-2\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-3\",\"type\":\"原材料\"},{\"allocation\":\"D4-4\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-5\",\"type\":\"原材料\"},{\"allocation\":\"D4-6\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-7\",\"type\":\"原材料\"},{\"allocation\":\"D4-8\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-9\",\"type\":\"原材料\"},{\"allocation\":\"D4-10\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-11\",\"type\":\"原材料\"},{\"allocation\":\"D4-12\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-13\",\"type\":\"原材料\"},{\"allocation\":\"D4-14\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-15\",\"type\":\"原材料\"},{\"allocation\":\"D4-16\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"D4-17\",\"type\":\"原材料\"},{\"allocation\":\"D4-18\",\"type\":\"原材料\"}," +
            "{\"allocation\":\"门卫室\",\"type\":\"门卫室\"}]}";
    private Logger logger = LoggerFactory.getLogger(DepotController.class);


    @Resource
    private DepotService depotService;

    @Resource
    private UserBusinessService userBusinessService;

    @Resource
    private MaterialService materialService;

    /**
     * 仓库列表
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getAllList")
    @ApiOperation(value = "仓库列表")
    public BaseResponseInfo getAllList(HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<Depot> depotList = depotService.getAllList();
            res.code = 200;
            res.data = depotList;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 用户对应仓库显示
     * @param type
     * @param keyId
     * @param request
     * @return
     */
    @GetMapping(value = "/findUserDepot")
    @ApiOperation(value = "用户对应仓库显示")
    public JSONArray findUserDepot(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId,
                                 HttpServletRequest request) throws Exception{
        JSONArray arr = new JSONArray();
        try {
            //获取权限信息
            String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, keyId);
            List<Depot> dataList = depotService.findUserDepot();
            //开始拼接json数据
            JSONObject outer = new JSONObject();
            outer.put("id", 0);
            outer.put("key", 0);
            outer.put("value", 0);
            outer.put("title", "仓库列表");
            outer.put("attributes", "仓库列表");
            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataList) {
                for (Depot depot : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", depot.getId());
                    item.put("key", depot.getId());
                    item.put("value", depot.getId());
                    item.put("title", depot.getName());
                    item.put("attributes", depot.getName());
                    Boolean flag = ubValue.contains("[" + depot.getId().toString() + "]");
                    if (flag) {
                        item.put("checked", true);
                    }
                    dataArray.add(item);
                }
            }
            outer.put("children", dataArray);
            arr.add(outer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 获取当前用户拥有权限的仓库列表
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/findDepotByCurrentUser")
    @ApiOperation(value = "获取当前用户拥有权限的仓库列表")
    public BaseResponseInfo findDepotByCurrentUser(HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONArray arr = depotService.findDepotByCurrentUser();
            res.code = 200;
            res.data = arr;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取所有的库位
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/findAllocation")
    @ApiOperation(value = "获取所有的库位")
    public BaseResponseInfo findAllocation(HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONArray arr = new JSONArray();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> data = objectMapper.readValue(ALLOCATION, Map.class);
            List<Map<String, String>> allocations = (List<Map<String, String>>) data.get("depotAllocation");
            for (int i = 0; i < allocations.size(); i++) {
                JSONObject item = new JSONObject();
                item.put("allocation", allocations.get(i).get("allocation"));
                item.put("type", allocations.get(i).get("type"));
                arr.add(item);
            }
            res.code = 200;
            res.data = arr;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 更新默认仓库
     * @param object
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/updateIsDefault")
    @ApiOperation(value = "更新默认仓库")
    public String updateIsDefault(@RequestBody JSONObject object,
                                       HttpServletRequest request) throws Exception{
        Long depotId = object.getLong("id");
        Map<String, Object> objectMap = new HashMap<>();
        int res = depotService.updateIsDefault(depotId);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 仓库列表-带库存
     * @param mId
     * @param request
     * @return
     */
    @GetMapping(value = "/getAllListWithStock")
    @ApiOperation(value = "仓库列表-带库存")
    public BaseResponseInfo getAllList(@RequestParam("mId") Long mId,
                                       HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<Depot> list = depotService.getAllList();
            List<DepotEx> depotList = new ArrayList<DepotEx>();
            for(Depot depot: list) {
                DepotEx de = new DepotEx();
                if(mId!=0) {
                    BigDecimal initStock = materialService.getInitStock(mId, depot.getId());
                    BigDecimal currentStock = materialService.getCurrentStockByMaterialIdAndDepotId(mId, depot.getId());
                    de.setInitStock(initStock);
                    de.setCurrentStock(currentStock);
                    MaterialInitialStock materialInitialStock = materialService.getSafeStock(mId, depot.getId());
                    de.setLowSafeStock(materialInitialStock.getLowSafeStock());
                    de.setHighSafeStock(materialInitialStock.getHighSafeStock());
                } else {
                    de.setInitStock(BigDecimal.ZERO);
                    de.setCurrentStock(BigDecimal.ZERO);
                }
                de.setId(depot.getId());
                de.setName(depot.getName());
                depotList.add(de);
            }
            res.code = 200;
            res.data = depotList;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Boolean status = jsonObject.getBoolean("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = depotService.batchSetStatus(status, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
