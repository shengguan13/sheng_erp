/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50704 (5.7.4-m14)
 Source Host           : localhost:3306
 Source Schema         : jsh_erp

 Target Server Type    : MySQL
 Target Server Version : 50704 (5.7.4-m14)
 File Encoding         : 65001

 Date: 25/04/2023 11:30:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for jsh_account
-- ----------------------------
DROP TABLE IF EXISTS `jsh_account`;
CREATE TABLE `jsh_account`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `serial_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
  `initial_amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '期初金额',
  `current_amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '当前余额',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `is_default` bit(1) NULL DEFAULT NULL COMMENT '是否默认',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '账户信息' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_account
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_account_head
-- ----------------------------
DROP TABLE IF EXISTS `jsh_account_head`;
CREATE TABLE `jsh_account_head`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型(支出/收入/收款/付款/转账)',
  `organ_id` bigint(20) NULL DEFAULT NULL COMMENT '单位Id(收款/付款单位)',
  `hands_person_id` bigint(20) NULL DEFAULT NULL COMMENT '经手人id',
  `creator` bigint(20) NULL DEFAULT NULL COMMENT '操作员',
  `change_amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '变动金额(优惠/收款/付款/实付)',
  `discount_money` decimal(24, 6) NULL DEFAULT NULL COMMENT '优惠金额',
  `total_price` decimal(24, 6) NULL DEFAULT NULL COMMENT '合计金额',
  `account_id` bigint(20) NULL DEFAULT NULL COMMENT '账户(收款/付款)',
  `bill_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单据编号',
  `bill_time` datetime NULL DEFAULT NULL COMMENT '单据日期',
  `remark` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `file_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件名称',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态，0未审核、1已审核、9审核中',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK9F4C0D8DB610FC06`(`organ_id`) USING BTREE,
  INDEX `FK9F4C0D8DAAE50527`(`account_id`) USING BTREE,
  INDEX `FK9F4C0D8DC4170B37`(`hands_person_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 138 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '财务主表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_account_head
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_account_item
-- ----------------------------
DROP TABLE IF EXISTS `jsh_account_item`;
CREATE TABLE `jsh_account_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `header_id` bigint(20) NOT NULL COMMENT '表头Id',
  `account_id` bigint(20) NULL DEFAULT NULL COMMENT '账户Id',
  `in_out_item_id` bigint(20) NULL DEFAULT NULL COMMENT '收支项目Id',
  `bill_id` bigint(20) NULL DEFAULT NULL COMMENT '单据id',
  `need_debt` decimal(24, 6) NULL DEFAULT NULL COMMENT '应收欠款',
  `finish_debt` decimal(24, 6) NULL DEFAULT NULL COMMENT '已收欠款',
  `each_amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '单项金额',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单据备注',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK9F4CBAC0AAE50527`(`account_id`) USING BTREE,
  INDEX `FK9F4CBAC0C5FE6007`(`header_id`) USING BTREE,
  INDEX `FK9F4CBAC0D203EDC5`(`in_out_item_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 168 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '财务子表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_account_item
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_depot
-- ----------------------------
DROP TABLE IF EXISTS `jsh_depot`;
CREATE TABLE `jsh_depot`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库名称',
  `address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '仓库地址',
  `warehousing` decimal(24, 6) NULL DEFAULT NULL COMMENT '仓储费',
  `truckage` decimal(24, 6) NULL DEFAULT NULL COMMENT '搬运费',
  `type` int(10) NULL DEFAULT NULL COMMENT '类型',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `principal` bigint(20) NULL DEFAULT NULL COMMENT '负责人',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_Flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `is_default` bit(1) NULL DEFAULT NULL COMMENT '是否默认',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '仓库表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_depot
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_depot_head
-- ----------------------------
DROP TABLE IF EXISTS `jsh_depot_head`;
CREATE TABLE `jsh_depot_head`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型(出库/入库)',
  `sub_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '出入库分类',
  `default_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '初始票据号',
  `number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '票据号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '出入库时间',
  `organ_id` bigint(20) NULL DEFAULT NULL COMMENT '供应商id',
  `creator` bigint(20) NULL DEFAULT NULL COMMENT '操作员',
  `account_id` bigint(20) NULL DEFAULT NULL COMMENT '账户id',
  `change_amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '变动金额(收款/付款)',
  `back_amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '找零金额',
  `total_price` decimal(24, 6) NULL DEFAULT NULL COMMENT '合计金额',
  `pay_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '付款类型(现金、记账等)',
  `bill_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单据类型',
  `remark` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `file_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件名称',
  `sales_man` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务员（可以多个）',
  `account_id_list` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多账户ID列表',
  `account_money_list` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多账户金额列表',
  `discount` decimal(24, 6) NULL DEFAULT NULL COMMENT '优惠率',
  `discount_money` decimal(24, 6) NULL DEFAULT NULL COMMENT '优惠金额',
  `discount_last_money` decimal(24, 6) NULL DEFAULT NULL COMMENT '优惠后金额',
  `other_money` decimal(24, 6) NULL DEFAULT NULL COMMENT '销售或采购费用合计',
  `deposit` decimal(24, 6) NULL DEFAULT NULL COMMENT '订金',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态，0未审核、1已审核、2完成采购|销售、3部分采购|销售、9审核中',
  `purchase_status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '采购状态，0未采购、2完成采购、3部分采购',
  `link_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联订单号',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `plan_start_time` datetime NULL DEFAULT NULL COMMENT '计划开始日期',
  `plan_finish_time` datetime NULL DEFAULT NULL COMMENT '计划完成日期',
  `work_hour` bigint(20) NULL DEFAULT NULL COMMENT '日工时',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK2A80F214B610FC06`(`organ_id`) USING BTREE,
  INDEX `FK2A80F214AAE50527`(`account_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 325 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单据主表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_depot_head
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_depot_item
-- ----------------------------
DROP TABLE IF EXISTS `jsh_depot_item`;
CREATE TABLE `jsh_depot_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `header_id` bigint(20) NOT NULL COMMENT '表头Id',
  `material_id` bigint(20) NOT NULL COMMENT '商品Id',
  `material_extend_id` bigint(20) NULL DEFAULT NULL COMMENT '商品扩展id',
  `material_unit` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品计量单位',
  `sku` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多属性',
  `oper_number` decimal(24, 6) NULL DEFAULT NULL COMMENT '数量',
  `basic_number` decimal(24, 6) NULL DEFAULT NULL COMMENT '基础数量，如kg、瓶',
  `unit_price` decimal(24, 6) NULL DEFAULT NULL COMMENT '单价',
  `purchase_unit_price` decimal(24, 6) NULL DEFAULT NULL COMMENT '采购单价',
  `tax_unit_price` decimal(24, 6) NULL DEFAULT NULL COMMENT '含税单价',
  `all_price` decimal(24, 6) NULL DEFAULT NULL COMMENT '金额',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `depot_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库ID',
  `another_depot_id` bigint(20) NULL DEFAULT NULL COMMENT '调拨时，对方仓库Id',
  `tax_rate` decimal(24, 6) NULL DEFAULT NULL COMMENT '税率',
  `tax_money` decimal(24, 6) NULL DEFAULT NULL COMMENT '税额',
  `tax_last_money` decimal(24, 6) NULL DEFAULT NULL COMMENT '价税合计',
  `material_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品类型',
  `sn_list` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序列号列表',
  `batch_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '批号',
  `expiration_date` datetime NULL DEFAULT NULL COMMENT '有效日期',
  `link_id` bigint(20) NULL DEFAULT NULL COMMENT '关联明细id',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK2A819F475D61CCF7`(`material_id`) USING BTREE,
  INDEX `FK2A819F474BB6190E`(`header_id`) USING BTREE,
  INDEX `FK2A819F479485B3F5`(`depot_id`) USING BTREE,
  INDEX `FK2A819F47729F5392`(`another_depot_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 398 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单据子表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_depot_item
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_function
-- ----------------------------
DROP TABLE IF EXISTS `jsh_function`;
CREATE TABLE `jsh_function`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `parent_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级编号',
  `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接',
  `component` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件',
  `state` bit(1) NULL DEFAULT NULL COMMENT '收缩',
  `sort` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `push_btn` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能按钮',
  `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `url`(`url`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 267 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '功能模块表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_function
-- ----------------------------
INSERT INTO `jsh_function` VALUES (1, '0001', '系统管理', '0', '/system', '/layouts/TabLayout', b'1', '0910', b'1', '电脑版', '', 'setting', '0');
INSERT INTO `jsh_function` VALUES (13, '000102', '角色管理', '0001', '/system/role', '/system/RoleList', b'0', '0130', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (14, '000103', '用户管理', '0001', '/system/user', '/system/UserList', b'0', '0140', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (15, '000104', '日志管理', '0001', '/system/log', '/system/LogList', b'0', '0160', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (16, '000105', '功能管理', '0001', '/system/function', '/system/FunctionList', b'0', '0166', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (18, '000109', '租户管理', '0001', '/system/tenant', '/system/TenantList', b'0', '0167', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (21, '0101', '商品管理', '0', '/material', '/layouts/TabLayout', b'0', '0620', b'1', '电脑版', NULL, 'shopping', '0');
INSERT INTO `jsh_function` VALUES (22, '010101', '商品类别', '0101', '/material/material_category', '/material/MaterialCategoryList', b'0', '0230', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (23, '010102', '商品信息', '0101', '/material/material', '/material/MaterialList', b'0', '0240', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (24, '0102', '基本资料', '0', '/systemA', '/layouts/TabLayout', b'0', '0750', b'1', '电脑版', NULL, 'appstore', '0');
INSERT INTO `jsh_function` VALUES (25, '01020101', '供应商信息', '0102', '/system/vendor', '/system/VendorList', b'0', '0260', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (26, '010202', '仓库信息', '0102', '/system/depot', '/system/DepotList', b'0', '0270', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (31, '010206', '经手人管理', '0102', '/system/person', '/system/PersonList', b'0', '0284', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (32, '0502', '采购管理', '0', '/bill', '/layouts/TabLayout', b'0', '0330', b'1', '电脑版', '', 'retweet', '0');
INSERT INTO `jsh_function` VALUES (33, '050201', '采购入库', '0502', '/bill/purchase_in', '/bill/PurchaseInList', b'0', '0340', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (38, '0603', '销售管理', '0', '/billB', '/layouts/TabLayout', b'0', '0390', b'1', '电脑版', '', 'shopping-cart', '0');
INSERT INTO `jsh_function` VALUES (40, '080107', '调拨出库', '0801', '/bill/allocation_out', '/bill/AllocationOutList', b'0', '0807', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (41, '060303', '销售出库', '0603', '/bill/sale_out', '/bill/SaleOutList', b'0', '0394', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (44, '0704', '财务管理', '0', '/financial', '/layouts/TabLayout', b'0', '0450', b'1', '电脑版', '', 'money-collect', '0');
INSERT INTO `jsh_function` VALUES (59, '030101', '进销存统计', '0301', '/report/in_out_stock_report', '/report/InOutStockReport', b'0', '0658', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (194, '010204', '收支项目', '0102', '/system/in_out_item', '/system/InOutItemList', b'0', '0282', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (195, '010205', '结算账户', '0102', '/system/account', '/system/AccountList', b'0', '0283', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (197, '070402', '收入单', '0704', '/financial/item_in', '/financial/ItemInList', b'0', '0465', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (198, '0301', '报表查询', '0', '/report', '/layouts/TabLayout', b'0', '0570', b'1', '电脑版', NULL, 'pie-chart', '0');
INSERT INTO `jsh_function` VALUES (199, '050204', '采购退货', '0502', '/bill/purchase_back', '/bill/PurchaseBackList', b'0', '0345', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (200, '060305', '销售退货', '0603', '/bill/sale_back', '/bill/SaleBackList', b'0', '0396', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (201, '080103', '其它入库', '0801', '/bill/other_in', '/bill/OtherInList', b'0', '0803', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (202, '080105', '其它出库', '0801', '/bill/other_out', '/bill/OtherOutList', b'0', '0805', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (203, '070403', '支出单', '0704', '/financial/item_out', '/financial/ItemOutList', b'0', '0470', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (204, '070404', '客户款项', '0704', '/financial/money_in', '/financial/MoneyInList', b'0', '0475', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (205, '070405', '供应商款项', '0704', '/financial/money_out', '/financial/MoneyOutList', b'0', '0480', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (206, '070406', '转账单', '0704', '/financial/giro', '/financial/GiroList', b'0', '0490', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (207, '030102', '账户统计', '0301', '/report/account_report', '/report/AccountReport', b'0', '0610', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (208, '030103', '采购统计', '0301', '/report/buy_in_report', '/report/BuyInReport', b'0', '0620', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (209, '030104', '销售统计', '0301', '/report/sale_out_report', '/report/SaleOutReport', b'0', '0630', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (210, '040102', '零售出库', '0401', '/bill/retail_out', '/bill/RetailOutList', b'0', '0415', b'1', '电脑版', '1,2,7', 'profile', '1');
INSERT INTO `jsh_function` VALUES (211, '040104', '零售退货', '0401', '/bill/retail_back', '/bill/RetailBackList', b'0', '0417', b'1', '电脑版', '1,2,7', 'profile', '1');
INSERT INTO `jsh_function` VALUES (212, '070407', '收预付款', '0704', '/financial/advance_in', '/financial/AdvanceInList', b'0', '0495', b'1', '电脑版', '1,2,7', 'profile', '1');
INSERT INTO `jsh_function` VALUES (217, '01020102', '客户信息', '0102', '/system/customer', '/system/CustomerList', b'0', '0262', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (218, '01020103', '会员信息', '0102', '/system/member', '/system/MemberList', b'0', '0263', b'1', '电脑版', '1', 'profile', '1');
INSERT INTO `jsh_function` VALUES (220, '010103', '计量单位', '0101', '/system/unit', '/system/UnitList', b'0', '0245', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (225, '0401', '零售管理', '0', '/billC', '/layouts/TabLayout', b'0', '0101', b'1', '电脑版', '', 'gift', '1');
INSERT INTO `jsh_function` VALUES (226, '030106', '入库明细', '0301', '/report/in_detail', '/report/InDetail', b'0', '0640', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (227, '030107', '出库明细', '0301', '/report/out_detail', '/report/OutDetail', b'0', '0645', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (228, '030108', '入库汇总', '0301', '/report/in_material_count', '/report/InMaterialCount', b'0', '0650', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (229, '030109', '出库汇总', '0301', '/report/out_material_count', '/report/OutMaterialCount', b'0', '0655', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (232, '080109', '组装单', '0801', '/bill/assemble', '/bill/AssembleList', b'0', '0809', b'1', '电脑版', '1,2,7', 'profile', '1');
INSERT INTO `jsh_function` VALUES (233, '080111', '拆卸单', '0801', '/bill/disassemble', '/bill/DisassembleList', b'0', '0811', b'1', '电脑版', '1,2,7', 'profile', '1');
INSERT INTO `jsh_function` VALUES (234, '000105', '系统配置', '0001', '/system/system_config', '/system/SystemConfigList', b'0', '0165', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (235, '030110', '客户对账', '0301', '/report/customer_account', '/report/CustomerAccount', b'0', '0660', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (236, '000106', '商品属性', '0001', '/material/material_property', '/material/MaterialPropertyList', b'0', '0168', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (237, '030111', '供应商对账', '0301', '/report/vendor_account', '/report/VendorAccount', b'0', '0665', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (239, '0801', '仓库管理', '0', '/billD', '/layouts/TabLayout', b'0', '0420', b'1', '电脑版', '', 'hdd', '0');
INSERT INTO `jsh_function` VALUES (241, '050202', '采购订单', '0502', '/bill/purchase_order', '/bill/PurchaseOrderList', b'0', '0335', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (242, '060301', '销售订单', '0603', '/bill/sale_order', '/bill/SaleOrderList', b'0', '0392', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (243, '000108', '机构管理', '0001', '/system/organization', '/system/OrganizationList', b'1', '0150', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (244, '030112', '库存预警', '0301', '/report/stock_warning_report', '/report/StockWarningReport', b'0', '0670', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (245, '000107', '插件管理', '0001', '/system/plugin', '/system/PluginList', b'0', '0170', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (246, '030113', '商品库存', '0301', '/report/material_stock', '/report/MaterialStock', b'0', '0605', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (247, '010105', '多属性', '0101', '/material/material_attribute', '/material/MaterialAttributeList', b'0', '0250', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (248, '030150', '调拨明细', '0301', '/report/allocation_detail', '/report/AllocationDetail', b'0', '0646', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (258, '000112', '平台配置', '0001', '/system/platform_config', '/system/PlatformConfigList', b'0', '0175', b'1', '电脑版', '', 'profile', '0');
INSERT INTO `jsh_function` VALUES (259, '0901', '生产管理', '0', '/production', '/layouts/TabLayout', b'0', '0400', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (260, '090101', '生产计划', '0901', '/bill/production_plan', '/bill/ProductionPlanList', b'0', '0401', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (261, '090102', '生产单', '0901', '/bill/production_order', '/bill/ProductionOrderList', b'0', '0402', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (262, '090103', '领料出库', '0901', '/bill/material_pick', '/bill/MaterialPickList', b'0', '0403', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (263, '090104', '退料入库', '0901', '/bill/material_return', '/bill/MaterialReturnList', b'0', '0404', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (264, '090105', '生产入库', '0901', '/bill/production_in', '/bill/ProductionInList', b'0', '0405', b'1', '电脑版', '1,2,7', 'profile', '0');
INSERT INTO `jsh_function` VALUES (265, '050205', '采购款项申请', '0502', '/bill/payment_application', '/bill/PaymentApplicationList', b'0', '0350', b'1', '电脑版', '1', 'profile', '0');
INSERT INTO `jsh_function` VALUES (266, '060306', '销售款项申请', '0603', '/bill/payment_application_b', '/bill/PaymentApplicationListB', b'0', '0397', b'1', '电脑版', '1', 'profile', '0');

-- ----------------------------
-- Table structure for jsh_in_out_item
-- ----------------------------
DROP TABLE IF EXISTS `jsh_in_out_item`;
CREATE TABLE `jsh_in_out_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收支项目' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_in_out_item
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_log
-- ----------------------------
DROP TABLE IF EXISTS `jsh_log`;
CREATE TABLE `jsh_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `operation` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作模块名称',
  `client_ip` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户端IP',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '操作状态 0==成功，1==失败',
  `content` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详情',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKF2696AA13E226853`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7914 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '操作日志' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_log
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material`;
CREATE TABLE `jsh_material`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '产品类型id',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `mfrs` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '制造商',
  `model` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '型号',
  `internal_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内部零件号',
  `color` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `project` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目（多个项目用|隔开）',
  `unit` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位-单个',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `img_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片名称',
  `unit_id` bigint(20) NULL DEFAULT NULL COMMENT '计量单位Id',
  `expiry_num` int(10) NULL DEFAULT NULL COMMENT '保质期天数',
  `weight` decimal(24, 6) NULL DEFAULT NULL COMMENT '基础重量(kg)',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用 0-禁用  1-启用',
  `other_field1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工艺类别',
  `other_field2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置',
  `other_field3` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义（目前为空）',
  `other_field4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '材料牌号',
  `other_field5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '材料类型/标准',
  `other_field6` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原材料厂家',
  `other_field7` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外协件厂家',
  `other_field8` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '尺寸',
  `other_field9` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检具',
  `other_field10` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用量/车（件）',
  `other_field11` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '料道（kg）',
  `other_field12` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表面处理纹理',
  `other_field13` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表面积（m²）',
  `other_field14` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组装等级关系',
  `enable_serial_number` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否开启序列号，0否，1是',
  `enable_batch_number` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否开启批号，0否，1是',
  `outsource` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否为外协件，0否，1是',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK675951272AB6672C`(`category_id`) USING BTREE,
  INDEX `UnitId`(`unit_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 628 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material_attribute
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material_attribute`;
CREATE TABLE `jsh_material_attribute`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性类型',
  `attribute_value` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性值',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品属性表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material_attribute
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material_category
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material_category`;
CREATE TABLE `jsh_material_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `category_level` smallint(6) NULL DEFAULT NULL COMMENT '等级',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '上级id',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示顺序',
  `serial_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
  `remark` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK3EE7F725237A77D8`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品类型表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material_category
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material_current_stock
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material_current_stock`;
CREATE TABLE `jsh_material_current_stock`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) NULL DEFAULT NULL COMMENT '产品id',
  `depot_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `current_number` decimal(24, 6) NULL DEFAULT NULL COMMENT '当前库存数量',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品当前库存' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material_current_stock
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material_extend
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material_extend`;
CREATE TABLE `jsh_material_extend`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) NULL DEFAULT NULL COMMENT '商品id',
  `bar_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品条码',
  `commodity_unit` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品单位',
  `sku` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多属性',
  `purchase_decimal` decimal(24, 6) NULL DEFAULT NULL COMMENT '采购价格',
  `commodity_decimal` decimal(24, 6) NULL DEFAULT NULL COMMENT '零售价格',
  `wholesale_decimal` decimal(24, 6) NULL DEFAULT NULL COMMENT '销售价格',
  `low_decimal` decimal(24, 6) NULL DEFAULT NULL COMMENT '最低售价',
  `default_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '是否为默认单位，1是，0否',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `create_serial` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人编码',
  `update_serial` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人编码',
  `update_time` bigint(20) NULL DEFAULT NULL COMMENT '更新时间戳',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_Flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品价格扩展' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material_extend
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material_initial_stock
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material_initial_stock`;
CREATE TABLE `jsh_material_initial_stock`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) NULL DEFAULT NULL COMMENT '产品id',
  `depot_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `number` decimal(24, 6) NULL DEFAULT NULL COMMENT '初始库存数量',
  `low_safe_stock` decimal(24, 6) NULL DEFAULT NULL COMMENT '最低库存数量',
  `high_safe_stock` decimal(24, 6) NULL DEFAULT NULL COMMENT '最高库存数量',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 205 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品初始库存' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material_initial_stock
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_material_property
-- ----------------------------
DROP TABLE IF EXISTS `jsh_material_property`;
CREATE TABLE `jsh_material_property`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `native_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始名称',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '是否启用',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `another_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '产品扩展字段表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_material_property
-- ----------------------------
INSERT INTO `jsh_material_property` VALUES (1, '制造商', b'1', '01', '制造商', '0');
INSERT INTO `jsh_material_property` VALUES (2, '工艺类别', b'1', '02', '工艺类别', '0');
INSERT INTO `jsh_material_property` VALUES (3, '配置', b'1', '03', '配置', '0');
INSERT INTO `jsh_material_property` VALUES (4, '自定义1', b'1', '04', '自定义1', '0');
INSERT INTO `jsh_material_property` VALUES (5, '材料牌号', b'1', '05', '材料牌号', '0');
INSERT INTO `jsh_material_property` VALUES (6, '材料类型/标准', b'1', '06', '材料类型/标准', '0');
INSERT INTO `jsh_material_property` VALUES (7, '原材料厂家', b'1', '07', '原材料厂家', '0');
INSERT INTO `jsh_material_property` VALUES (8, '外协件厂家', b'1', '08', '外协件厂家', '0');
INSERT INTO `jsh_material_property` VALUES (9, '尺寸', b'1', '09', '尺寸', '0');
INSERT INTO `jsh_material_property` VALUES (10, '检具', b'1', '10', '检具', '0');
INSERT INTO `jsh_material_property` VALUES (11, '用量/车（件）', b'1', '11', '用量/车（件）', '0');
INSERT INTO `jsh_material_property` VALUES (12, '料道（kg）', b'1', '12', '料道（kg）', '0');
INSERT INTO `jsh_material_property` VALUES (13, '表面处理纹理', b'1', '13', '表面处理纹理', '0');
INSERT INTO `jsh_material_property` VALUES (14, '表面积（m²）', b'1', '14', '表面积（m²）', '0');
INSERT INTO `jsh_material_property` VALUES (15, '组装等级关系', b'1', '15', '组装等级关系', '0');

-- ----------------------------
-- Table structure for jsh_msg
-- ----------------------------
DROP TABLE IF EXISTS `jsh_msg`;
CREATE TABLE `jsh_msg`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg_title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息标题',
  `msg_content` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息类型',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '接收人id',
  `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态，1未读 2已读',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_Flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '消息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_msg
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_orga_user_rel
-- ----------------------------
DROP TABLE IF EXISTS `jsh_orga_user_rel`;
CREATE TABLE `jsh_orga_user_rel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orga_id` bigint(20) NOT NULL COMMENT '机构id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_blng_orga_dspl_seq` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户在所属机构中显示顺序',
  `delete_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `updater` bigint(20) NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机构用户关系表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_orga_user_rel
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_organization
-- ----------------------------
DROP TABLE IF EXISTS `jsh_organization`;
CREATE TABLE `jsh_organization`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_no` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构编号',
  `org_abr` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构简称',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父机构id',
  `sort` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构显示顺序',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机构表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_organization
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_person
-- ----------------------------
DROP TABLE IF EXISTS `jsh_person`;
CREATE TABLE `jsh_person`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '经手人表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_person
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_platform_config
-- ----------------------------
DROP TABLE IF EXISTS `jsh_platform_config`;
CREATE TABLE `jsh_platform_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `platform_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键词',
  `platform_key_info` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键词名称',
  `platform_value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '平台参数' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_platform_config
-- ----------------------------
INSERT INTO `jsh_platform_config` VALUES (1, 'platform_name', '平台名称', '宏泽ERP');
INSERT INTO `jsh_platform_config` VALUES (2, 'activation_code', '激活码', '');
INSERT INTO `jsh_platform_config` VALUES (3, 'platform_url', '官方网站', 'http://www.huaxiaerp.com/');
INSERT INTO `jsh_platform_config` VALUES (4, 'bill_print_flag', '三联打印启用标记', '');
INSERT INTO `jsh_platform_config` VALUES (5, 'bill_print_url', '三联打印地址', '');
INSERT INTO `jsh_platform_config` VALUES (6, 'pay_fee_url', '租户续费地址', '');
INSERT INTO `jsh_platform_config` VALUES (7, 'register_flag', '注册启用标记', '');
INSERT INTO `jsh_platform_config` VALUES (8, 'app_activation_code', '手机端激活码', '');
INSERT INTO `jsh_platform_config` VALUES (9, 'send_workflow_url', '发起流程地址', '');

-- ----------------------------
-- Table structure for jsh_role
-- ----------------------------
DROP TABLE IF EXISTS `jsh_role`;
CREATE TABLE `jsh_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `price_limit` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '价格屏蔽 1-屏蔽采购价 2-屏蔽零售价 3-屏蔽销售价',
  `value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值',
  `description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_role
-- ----------------------------
INSERT INTO `jsh_role` VALUES (4, '管理员', '全部数据', NULL, NULL, NULL, b'1', NULL, NULL, '0');
INSERT INTO `jsh_role` VALUES (10, '租户', '全部数据', NULL, NULL, '', b'1', NULL, NULL, '0');

-- ----------------------------
-- Table structure for jsh_sequence
-- ----------------------------
DROP TABLE IF EXISTS `jsh_sequence`;
CREATE TABLE `jsh_sequence`  (
  `seq_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '序列名称',
  `min_value` bigint(20) NOT NULL COMMENT '最小值',
  `max_value` bigint(20) NOT NULL COMMENT '最大值',
  `current_val` bigint(20) NOT NULL COMMENT '当前值',
  `increment_val` int(11) NOT NULL DEFAULT 1 COMMENT '增长步数',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`seq_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单据编号表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_sequence
-- ----------------------------
INSERT INTO `jsh_sequence` VALUES ('depot_number_seq', 1, 999999999999999999, 897, 1, '单据编号sequence');

-- ----------------------------
-- Table structure for jsh_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `jsh_serial_number`;
CREATE TABLE `jsh_serial_number`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint(20) NULL DEFAULT NULL COMMENT '产品表id',
  `depot_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
  `serial_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序列号',
  `is_sell` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否卖出，0未卖出，1卖出',
  `remark` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `creator` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `updater` bigint(20) NULL DEFAULT NULL COMMENT '更新人',
  `in_bill_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '入库单号',
  `out_bill_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '出库单号',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '序列号表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_serial_number
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_supplier
-- ----------------------------
DROP TABLE IF EXISTS `jsh_supplier`;
CREATE TABLE `jsh_supplier`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `supplier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '供应商名称',
  `contacts` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone_num` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `isystem` tinyint(4) NULL DEFAULT NULL COMMENT '是否系统自带 0==系统 1==非系统',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `advance_in` decimal(24, 6) NULL DEFAULT 0.000000 COMMENT '预收款',
  `begin_need_get` decimal(24, 6) NULL DEFAULT NULL COMMENT '期初应收',
  `begin_need_pay` decimal(24, 6) NULL DEFAULT NULL COMMENT '期初应付',
  `all_need_get` decimal(24, 6) NULL DEFAULT NULL COMMENT '累计应收',
  `all_need_pay` decimal(24, 6) NULL DEFAULT NULL COMMENT '累计应付',
  `fax` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '传真',
  `telephone` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机',
  `address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `tax_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '纳税人识别号',
  `bank_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开户行',
  `account_number` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `tax_rate` decimal(24, 6) NULL DEFAULT NULL COMMENT '税率',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '供应商/客户信息表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_supplier
-- ----------------------------

-- ----------------------------
-- Table structure for jsh_system_config
-- ----------------------------
DROP TABLE IF EXISTS `jsh_system_config`;
CREATE TABLE `jsh_system_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
  `company_contacts` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司联系人',
  `company_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司地址',
  `company_tel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司电话',
  `company_fax` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司传真',
  `company_post_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司邮编',
  `sale_agreement` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售协议',
  `depot_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '仓库启用标记，0未启用，1启用',
  `customer_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '客户启用标记，0未启用，1启用',
  `minus_stock_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '负库存启用标记，0未启用，1启用',
  `purchase_by_sale_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '以销定购启用标记，0未启用，1启用',
  `multi_level_approval_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '多级审核启用标记，0未启用，1启用',
  `multi_bill_type` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程类型，可多选',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统参数' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_system_config
-- ----------------------------
INSERT INTO `jsh_system_config` VALUES (11, '吉林宏泽汽车部件有限公司', '', '', '', NULL, NULL, '', '0', '0', '0', '0', '0', '', 63, '0');

-- ----------------------------
-- Table structure for jsh_tenant
-- ----------------------------
DROP TABLE IF EXISTS `jsh_tenant`;
CREATE TABLE `jsh_tenant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id',
  `login_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录名',
  `user_num_limit` int(11) NULL DEFAULT NULL COMMENT '用户数量限制',
  `type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '租户类型，0免费租户，1付费租户',
  `enabled` bit(1) NULL DEFAULT b'1' COMMENT '启用 0-禁用  1-启用',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_tenant
-- ----------------------------
INSERT INTO `jsh_tenant` VALUES (13, 63, 'guansheng', 2000, '1', b'1', '2021-02-17 23:19:17', '2099-02-17 23:19:17', NULL);

-- ----------------------------
-- Table structure for jsh_unit
-- ----------------------------
DROP TABLE IF EXISTS `jsh_unit`;
CREATE TABLE `jsh_unit`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称，支持多单位',
  `basic_unit` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '基础单位',
  `other_unit` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副单位',
  `other_unit_two` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副单位2',
  `other_unit_three` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副单位3',
  `ratio` int(11) NULL DEFAULT NULL COMMENT '比例',
  `ratio_two` int(11) NULL DEFAULT NULL COMMENT '比例2',
  `ratio_three` int(11) NULL DEFAULT NULL COMMENT '比例3',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '多单位表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_unit
-- ----------------------------
INSERT INTO `jsh_unit` VALUES (22, '瓶/(箱=12瓶)', '瓶', '箱', NULL, NULL, 12, NULL, NULL, b'1', 63, '0');

-- ----------------------------
-- Table structure for jsh_user
-- ----------------------------
DROP TABLE IF EXISTS `jsh_user`;
CREATE TABLE `jsh_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户姓名--例如张三',
  `login_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登陆密码',
  `leader_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否经理，0否，1是',
  `position` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位',
  `department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `phonenum` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `ismanager` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否为管理者 0==管理者 1==员工',
  `isystem` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否系统自带数据 ',
  `Status` tinyint(4) NULL DEFAULT 0 COMMENT '状态，0：正常，1：删除，2封禁',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户描述信息',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 148 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_user
-- ----------------------------
INSERT INTO `jsh_user` VALUES (63, '测试', 'guansheng', 'd5c64a6be9b950778506c0647fa592da', '0', '', NULL, '', '', 1, 1, 0, '', NULL, 63);
INSERT INTO `jsh_user` VALUES (120, '管理员', 'admin', 'd5c64a6be9b950778506c0647fa592da', '0', NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, 0);

-- ----------------------------
-- Table structure for jsh_user_business
-- ----------------------------
DROP TABLE IF EXISTS `jsh_user_business`;
CREATE TABLE `jsh_user_business`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类别',
  `key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主id',
  `value` varchar(10000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值',
  `btn_str` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '按钮权限',
  `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户/角色/模块关系表' ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of jsh_user_business
-- ----------------------------
INSERT INTO `jsh_user_business` VALUES (5, 'RoleFunctions', '4', '[241][32][33][199][265][242][41][200][260][259][261][262][263][264][201][239][202][40][197][44][203][204][205][206][246][198][207][208][209][226][227][248][228][229][59][235][237][244][22][21][23][220][247][25][24][217][218][26][194][195][31][13][1][14][243][15][234][16][18][236][245][258][266][38]', '[{\"funId\":13,\"btnStr\":\"1\"},{\"funId\":14,\"btnStr\":\"1\"},{\"funId\":243,\"btnStr\":\"1\"},{\"funId\":234,\"btnStr\":\"1\"},{\"funId\":16,\"btnStr\":\"1\"},{\"funId\":18,\"btnStr\":\"1\"},{\"funId\":236,\"btnStr\":\"1\"},{\"funId\":245,\"btnStr\":\"1\"},{\"funId\":22,\"btnStr\":\"1\"},{\"funId\":23,\"btnStr\":\"1\"},{\"funId\":220,\"btnStr\":\"1\"},{\"funId\":247,\"btnStr\":\"1\"},{\"funId\":25,\"btnStr\":\"1\"},{\"funId\":217,\"btnStr\":\"1\"},{\"funId\":218,\"btnStr\":\"1\"},{\"funId\":26,\"btnStr\":\"1\"},{\"funId\":194,\"btnStr\":\"1\"},{\"funId\":195,\"btnStr\":\"1\"},{\"funId\":31,\"btnStr\":\"1\"},{\"funId\":241,\"btnStr\":\"1,2,7\"},{\"funId\":33,\"btnStr\":\"1,2,7\"},{\"funId\":199,\"btnStr\":\"1,2,7\"},{\"funId\":265,\"btnStr\":\"1\"},{\"funId\":242,\"btnStr\":\"1,2,7\"},{\"funId\":41,\"btnStr\":\"1,2,7\"},{\"funId\":200,\"btnStr\":\"1,2,7\"},{\"funId\":266,\"btnStr\":\"1\"},{\"funId\":259,\"btnStr\":\"1,2,7\"},{\"funId\":260,\"btnStr\":\"1,2,7\"},{\"funId\":261,\"btnStr\":\"1,2,7\"},{\"funId\":262,\"btnStr\":\"1,2,7\"},{\"funId\":263,\"btnStr\":\"1,2,7\"},{\"funId\":264,\"btnStr\":\"1,2,7\"},{\"funId\":197,\"btnStr\":\"1,2,7\"},{\"funId\":203,\"btnStr\":\"1,2,7\"},{\"funId\":204,\"btnStr\":\"1,2,7\"},{\"funId\":205,\"btnStr\":\"1,2,7\"},{\"funId\":206,\"btnStr\":\"1,2,7\"},{\"funId\":201,\"btnStr\":\"1,2,7\"},{\"funId\":202,\"btnStr\":\"1,2,7\"},{\"funId\":40,\"btnStr\":\"1,2,7\"}]', NULL, '0');
INSERT INTO `jsh_user_business` VALUES (6, 'RoleFunctions', '5', '[22][23][25][26][194][195][31][33][200][201][41][199][202]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (7, 'RoleFunctions', '6', '[22][23][220][240][25][217][218][26][194][195][31][59][207][208][209][226][227][228][229][235][237][210][211][241][33][199][242][41][200][201][202][40][232][233][197][203][204][205][206][212]', '[{\"funId\":\"33\",\"btnStr\":\"4\"}]', NULL, '0');
INSERT INTO `jsh_user_business` VALUES (9, 'RoleFunctions', '7', '[168][13][12][16][14][15][189][18][19][132]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (10, 'RoleFunctions', '8', '[168][13][12][16][14][15][189][18][19][132][22][23][25][26][27][157][158][155][156][125][31][127][126][128][33][34][35][36][37][39][40][41][42][43][46][47][48][49][50][51][52][53][54][55][56][57][192][59][60][61][62][63][65][66][68][69][70][71][73][74][76][77][79][191][81][82][83][85][89][161][86][176][165][160][28][134][91][92][29][94][95][97][104][99][100][101][102][105][107][108][110][111][113][114][116][117][118][120][121][131][135][123][122][20][130][146][147][138][148][149][153][140][145][184][152][143][170][171][169][166][167][163][164][172][173][179][178][181][182][183][186][187][247]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (11, 'RoleFunctions', '9', '[168][13][12][16][14][15][189][18][19][132][22][23][25][26][27][157][158][155][156][125][31][127][126][128][33][34][35][36][37][39][40][41][42][43][46][47][48][49][50][51][52][53][54][55][56][57][192][59][60][61][62][63][65][66][68][69][70][71][73][74][76][77][79][191][81][82][83][85][89][161][86][176][165][160][28][134][91][92][29][94][95][97][104][99][100][101][102][105][107][108][110][111][113][114][116][117][118][120][121][131][135][123][122][20][130][146][147][138][148][149][153][140][145][184][152][143][170][171][169][166][167][163][164][172][173][179][178][181][182][183][186][187][188]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (12, 'UserRole', '1', '[5]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (13, 'UserRole', '2', '[6][7]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (14, 'UserDepot', '2', '[1][2][6][7]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (15, 'UserDepot', '1', '[1][2][5][6][7][10][12][14][15][17]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (16, 'UserRole', '63', '[10]', NULL, 63, '0');
INSERT INTO `jsh_user_business` VALUES (18, 'UserDepot', '63', '[14][15][19][20][20][22]', NULL, 63, '0');
INSERT INTO `jsh_user_business` VALUES (19, 'UserDepot', '5', '[6][45][46][50]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (20, 'UserRole', '5', '[5]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (21, 'UserRole', '64', '[13]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (22, 'UserDepot', '64', '[1]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (23, 'UserRole', '65', '[5]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (24, 'UserDepot', '65', '[1]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (25, 'UserCustomer', '64', '[5][2]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (26, 'UserCustomer', '65', '[6]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (27, 'UserCustomer', '63', '[58][92][94][96]', NULL, 63, '0');
INSERT INTO `jsh_user_business` VALUES (28, 'UserDepot', '96', '[7]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (29, 'UserRole', '96', '[6]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (30, 'UserRole', '113', '[10]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (32, 'RoleFunctions', '10', '[241][32][33][199][265][242][41][200][260][259][261][262][263][264][201][239][202][40][197][44][203][204][205][206][246][198][207][208][209][226][227][248][228][229][59][235][237][244][22][21][23][220][247][25][24][217][218][26][194][195][31][13][1][14][243][15][234][16][18][236][245][258][266][38]', '[{\"funId\":13,\"btnStr\":\"1\"},{\"funId\":14,\"btnStr\":\"1\"},{\"funId\":243,\"btnStr\":\"1\"},{\"funId\":234,\"btnStr\":\"1\"},{\"funId\":16,\"btnStr\":\"1\"},{\"funId\":18,\"btnStr\":\"1\"},{\"funId\":236,\"btnStr\":\"1\"},{\"funId\":245,\"btnStr\":\"1\"},{\"funId\":22,\"btnStr\":\"1\"},{\"funId\":23,\"btnStr\":\"1\"},{\"funId\":220,\"btnStr\":\"1\"},{\"funId\":247,\"btnStr\":\"1\"},{\"funId\":25,\"btnStr\":\"1\"},{\"funId\":217,\"btnStr\":\"1\"},{\"funId\":218,\"btnStr\":\"1\"},{\"funId\":26,\"btnStr\":\"1\"},{\"funId\":194,\"btnStr\":\"1\"},{\"funId\":195,\"btnStr\":\"1\"},{\"funId\":31,\"btnStr\":\"1\"},{\"funId\":241,\"btnStr\":\"1,2,7\"},{\"funId\":33,\"btnStr\":\"1,2,7\"},{\"funId\":199,\"btnStr\":\"1,2,7\"},{\"funId\":265,\"btnStr\":\"1\"},{\"funId\":242,\"btnStr\":\"1,2,7\"},{\"funId\":41,\"btnStr\":\"1,2,7\"},{\"funId\":200,\"btnStr\":\"1,2,7\"},{\"funId\":266,\"btnStr\":\"1\"},{\"funId\":259,\"btnStr\":\"1,2,7\"},{\"funId\":260,\"btnStr\":\"1,2,7\"},{\"funId\":261,\"btnStr\":\"1,2,7\"},{\"funId\":262,\"btnStr\":\"1,2,7\"},{\"funId\":263,\"btnStr\":\"1,2,7\"},{\"funId\":264,\"btnStr\":\"1,2,7\"},{\"funId\":197,\"btnStr\":\"1,2,7\"},{\"funId\":203,\"btnStr\":\"1,2,7\"},{\"funId\":204,\"btnStr\":\"1,2,7\"},{\"funId\":205,\"btnStr\":\"1,2,7\"},{\"funId\":206,\"btnStr\":\"1,2,7\"},{\"funId\":201,\"btnStr\":\"1,2,7\"},{\"funId\":202,\"btnStr\":\"1,2,7\"},{\"funId\":40,\"btnStr\":\"1,2,7\"}]', NULL, '0');
INSERT INTO `jsh_user_business` VALUES (34, 'UserRole', '115', '[10]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (35, 'UserRole', '117', '[10]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (36, 'UserDepot', '117', '[8][9]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (37, 'UserCustomer', '117', '[52]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (38, 'UserRole', '120', '[4]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (41, 'RoleFunctions', '12', '', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (48, 'RoleFunctions', '13', '[59][207][208][209][226][227][228][229][235][237][210][211][241][33][199][242][41][200]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (51, 'UserRole', '74', '[10]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (52, 'UserDepot', '121', '[13]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (54, 'UserDepot', '115', '[13]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (56, 'UserCustomer', '115', '[56]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (57, 'UserCustomer', '121', '[56]', NULL, NULL, '0');
INSERT INTO `jsh_user_business` VALUES (67, 'UserRole', '131', '[17]', NULL, 63, '0');
INSERT INTO `jsh_user_business` VALUES (68, 'RoleFunctions', '16', '[239][201][202][40][232][233][21][22][23][220][247]', '[{\"funId\":22,\"btnStr\":\"1,2,7\"},{\"funId\":23,\"btnStr\":\"1,2,7\"},{\"funId\":220,\"btnStr\":\"1,2,7\"},{\"funId\":247,\"btnStr\":\"1,2,7\"},{\"funId\":201,\"btnStr\":\"1,2,7\"},{\"funId\":202,\"btnStr\":\"1,2,7\"},{\"funId\":40,\"btnStr\":\"1,2,7\"},{\"funId\":232,\"btnStr\":\"1,2,7\"},{\"funId\":233,\"btnStr\":\"1,2,7\"}]', 63, '0');
INSERT INTO `jsh_user_business` VALUES (69, 'RoleFunctions', '17', '[32][241][33][199][21][22][23][220][247]', '[{\"funId\":22,\"btnStr\":\"1,7\"},{\"funId\":23,\"btnStr\":\"1,7\"},{\"funId\":220,\"btnStr\":\"1,7\"},{\"funId\":247,\"btnStr\":\"1,7\"},{\"funId\":241,\"btnStr\":\"2,1,7\"},{\"funId\":33,\"btnStr\":\"2,1,7\"},{\"funId\":199,\"btnStr\":\"2,1,7\"}]', 63, '0');
INSERT INTO `jsh_user_business` VALUES (83, 'UserRole', '146', '[17]', NULL, 63, '0');
INSERT INTO `jsh_user_business` VALUES (84, 'UserRole', '147', '[16]', NULL, 63, '0');

SET FOREIGN_KEY_CHECKS = 1;
