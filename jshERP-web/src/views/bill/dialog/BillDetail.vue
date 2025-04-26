<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :maskClosable="false"
    :keyboard="false"
    :forceRender="true"
    :style="modalStyle"
    @cancel="handleCancel"
    wrapClassName="ant-modal-cust-warp">
    <template slot="footer">
      <a-button v-if="billPrintFlag" @click="handlePrint">三联打印预览</a-button>
      <!--此处为解决缓存问题-->
      <a-button v-if="billType === '采购申请'" v-print="'#purchaseApplicationPrint'">普通打印</a-button>
      <a-button v-if="billType === '采购订单'" v-print="'#purchaseOrderPrint'">普通打印</a-button>
      <a-button v-if="billType === '采购入库'" v-print="'#purchaseInPrint'">普通打印</a-button>
      <a-button v-if="billType === '采购退货出库'" v-print="'#purchaseBackPrint'">普通打印</a-button>
      <a-button v-if="billType === '销售订单'" v-print="'#saleOrderPrint'">普通打印</a-button>
      <a-button v-if="billType === '销售出库'" v-print="'#saleOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '销售退货入库'" v-print="'#saleBackPrint'">普通打印</a-button>
      <a-button v-if="billType === '生产计划'" v-print="'#productionPlanPrint'">普通打印</a-button>
      <a-button v-if="billType === '生产单'" v-print="'#productionOrderPrint'">普通打印</a-button>
      <a-button v-if="billType === '备料'" v-print="'#materialPreparePrint'">普通打印</a-button>
      <a-button v-if="billType === '计划备料'" v-print="'#materialPreparePrint'">普通打印</a-button>
      <a-button v-if="billType === '领料出库'" v-print="'#materialPickPrint'">普通打印</a-button>
      <a-button v-if="billType === '退料入库'" v-print="'#materialReturnPrint'">普通打印</a-button>
      <a-button v-if="billType === '生产入库'" v-print="'#productionInPrint'">普通打印</a-button>
      <a-button v-if="billType === '返修入库'" v-print="'#repairInPrint'">普通打印</a-button>
      <a-button v-if="billType === '返修出库'" v-print="'#repairOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '隔离出库'" v-print="'#isolatePrint'">普通打印</a-button>
      <a-button v-if="billType === '差异入库'" v-print="'#diffInPrint'">普通打印</a-button>
      <a-button v-if="billType === '其它入库'" v-print="'#otherInPrint'">普通打印</a-button>
      <a-button v-if="billType === '其它出库'" v-print="'#otherOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '调拨出库'" v-print="'#allocationOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '盘点复盘'" v-print="'#stockCheckReplayPrint'">普通打印</a-button>
      <!--导出Excel-->
      <a-button v-if="billType === '采购申请'" @click="applicationExportExcel()">导出</a-button>
      <a-button v-if="billType === '采购订单'||billType === '销售订单'" @click="orderExportExcel()">导出</a-button>
      <a-button v-if="billType === '采购入库'||billType === '销售出库'" @click="purchaseSaleExportExcel()">导出</a-button>
      <a-button v-if="billType === '采购退货出库'||billType === '销售退货入库'" @click="purchaseSaleBackExportExcel()">导出</a-button>
      <a-button v-if="billType === '生产单'" @click="productionOrderExportExcel()">导出</a-button>
      <a-button v-if="billType === '备料'" @click="materialPrepareExportExcel()">导出</a-button>
      <a-button v-if="billType === '计划备料'" @click="materialPrepareExportExcel()">导出</a-button>
      <a-button v-if="billType === '领料出库'" @click="materialPickExportExcel()">导出</a-button>
      <a-button v-if="billType === '退料入库'" @click="materialReturnExportExcel()">导出</a-button>
      <a-button v-if="billType === '生产入库'" @click="productionInExportExcel()">导出</a-button>
      <a-button v-if="billType === '其它入库'||billType === '其它出库'" @click="otherExportExcel()">导出</a-button>
      <a-button v-if="billType === '调拨出库'" @click="allocationOutExportExcel()">导出</a-button>
      <a-button v-if="billType === '盘点复盘'" @click="stockCheckReplayExportExcel()">导出</a-button>
      <!--审核/反审核-->
      <a-button v-if="checkFlag && isCanBackCheck && model.status==='0'" @click="handleCheck()">审核</a-button>
      <a-button v-if="checkFlag && isCanBackCheck && model.status==='1'" @click="handleBackCheck()">反审核</a-button>
      <a-button v-if="btnEnableList.indexOf(1)>-1" @click="handleReceipt()">结算</a-button>
      <a-button v-if="btnEnableList.indexOf(1)>-1" @click="handleBackReceipt()">反结算</a-button>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <a-form :form="form">
      <!--生产单-->
      <template v-if="billType === '生产单'">
        <section ref="print" id="productionOrderPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联计划">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束日期">
                <a-input v-decorator="['id']" hidden/>
                {{model.planStartTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--生产计划-->
      <template v-if="billType === '生产计划'">
        <section ref="print" id="productionPlanPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联预测">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划开始日期">
                <a-input v-decorator="['id']" hidden/>
                {{model.planStartTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划完成日期">
                {{model.planFinishTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--备料-->
        <template v-else-if="billType === '备料'">
          <section ref="print" id="materialPreparePrint">
            <a-row class="form-row" :gutter="24">
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                  <a-input v-decorator="['id']" hidden/>
                  {{model.organName}}
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                  {{model.operTimeStr}}
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                  {{model.number}}
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联计划">
                  <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row class="form-row" :gutter="24">
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开工时间">
                  <a-input v-decorator="['id']" hidden/>
                  {{model.planStartTimeStr}}
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产工时">
                  {{model.workHour}}
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                  {{model.creatorName}}
                </a-form-item>
              </a-col>
            </a-row>
            <div :style="tableWidth">
              <a-table
                ref="table"
                size="middle"
                bordered
                rowKey="id"
                :pagination="false"
                :columns="columns"
                :dataSource="dataSource">
              </a-table>
            </div>
            <a-row class="form-row" :gutter="24">
              <a-col :lg="24" :md="24" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                  {{model.remark}}
                </a-form-item>
              </a-col>
            </a-row>
          </section>
        </template>
      <!--领料出库-->
      <template v-else-if="billType === '领料出库'">
        <section ref="print" id="materialPickPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料人员">
                <a-input v-decorator="['id']" hidden/>
                {{model.salesManStr}}
                <!-- TODO: 不知道这样能不能显示领料人 -->
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="来源追溯">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number + ": " + item.model}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--退料入库-->
      <template v-else-if="billType === '退料入库'">
        <section ref="print" id="materialReturnPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料人员">
                <a-input v-decorator="['id']" hidden/>
                {{model.salesManStr}}
                <!-- TODO: 不知道这样能不能显示领料人 -->
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--生产入库-->
      <template v-else-if="billType === '生产入库'">
        <section ref="print" id="productionInPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="去向追溯">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number + ": " + item.model}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--采购申请-->
      <template v-else-if="billType === '采购申请'">
        <section ref="print" id="purchaseApplicationPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请人">
                {{model.salesManStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col v-if="model.hasBackFlag" :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="采购单号">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--采购订单-->
      <template v-else-if="billType === '采购订单'">
        <section ref="print" id="purchaseOrderPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="供应商">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="采购申请">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同编号">
                {{model.payType}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col v-if="model.hasBackFlag" :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库单号">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--采购入库-->
      <template v-else-if="billType === '采购入库'">
        <section ref="print" id="purchaseInPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="供应商">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="采购订单">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同编号">
                {{model.payType}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="去向追溯">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number + ": " + item.model}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col v-if="model.hasBackFlag" :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="退货单号">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--采购退货-->
      <template v-else-if="billType === '采购退货出库'">
        <section ref="print" id="purchaseBackPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="供应商">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--销售订单-->
      <template v-else-if="billType === '销售订单'">
        <section ref="print" id="saleOrderPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col v-if="model.hasBackFlag" :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出库单号">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--销售出库-->
      <template v-else-if="billType === '销售出库'">
        <section ref="print" id="saleOutPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联订单">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户订单号">
                {{model.payType}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="来源追溯">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number + ": " + item.model}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col v-if="model.hasBackFlag" :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="退货单号">
                <template v-for="(item, index) in linkNumberList">
                  <a @click="myHandleDetail(item.number)">{{item.number}}</a><br/>
                </template>
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--销售退货-->
      <template v-else-if="billType === '销售退货入库'">
        <section ref="print" id="saleBackPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--返修入库-->
      <template v-else-if="billType === '返修入库'">
        <section ref="print" id="repairInPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--返修出库-->
      <template v-else-if="billType === '返修出库'">
        <section ref="print" id="repairOutPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--隔离出库-->
      <template v-else-if="billType === '隔离出库'">
        <section ref="print" id="isolatePrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申报人">
                <a-input v-decorator="['id']" hidden/>
                {{model.salesManStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--差异入库-->
      <template v-else-if="billType === '差异入库'">
        <section ref="print" id="diffInPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="盘点人">
                <a-input v-decorator="['id']" hidden/>
                {{model.salesManStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--其它入库-->
      <template v-else-if="billType === '其它入库'">
        <section ref="print" id="anotherInPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--其它出库-->
      <template v-else-if="billType === '其它出库'">
        <section ref="print" id="otherOutPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
                <a-input v-decorator="['id']" hidden/>
                {{model.organName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--调拨出库-->
      <template v-else-if="billType === '调拨出库'">
        <section ref="print" id="allocationOutPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建日期">
                {{model.createTimeStr}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <!--盘点复盘-->
      <template v-else-if="billType === '盘点复盘'">
        <section ref="print" id="stockCheckReplayPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
                {{model.operTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号">
                {{model.number}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联单据">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制单人">
                {{model.creatorName}}
              </a-form-item>
            </a-col>
          </a-row>
          <div :style="tableWidth">
            <a-table
              ref="table"
              size="middle"
              bordered
              rowKey="id"
              :pagination="false"
              :columns="columns"
              :dataSource="dataSource">
            </a-table>
          </div>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="24" :md="24" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="" style="padding:20px 10px;">
                {{model.remark}}
              </a-form-item>
            </a-col>
          </a-row>
        </section>
      </template>
      <template v-if="fileList && fileList.length>0">
        <a-row class="form-row" :gutter="24">
          <a-col :span="10">
            <a-form-item :labelCol="{xs: { span: 24 },sm: { span: 3 }}" :wrapperCol="{xs: { span: 24 },sm: { span: 21 }}" label="附件">
              <j-upload v-model="fileList" bizPath="bill" :disabled="true" :buttonVisible="false"></j-upload>
            </a-form-item>
          </a-col>
          <a-col :span="14"></a-col>
        </a-row>
      </template>
    </a-form>
    <bill-print-iframe ref="modalDetail"></bill-print-iframe>
    <workflow-iframe ref="modalWorkflow"></workflow-iframe>
    <financial-detail ref="financialDetailModal"></financial-detail>
  </j-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import { getAction, postAction } from '@/api/manage'
  import { findBillDetailByNumber, findFinancialDetailByNumber, getPlatformConfigByKey, getCurrentSystemConfig} from '@/api/api'
  import { getMpListShort, getCheckFlag, openDownloadDialog, sheet2blob } from "@/utils/util"
  import BillPrintIframe from './BillPrintIframe'
  import WorkflowIframe from '@/components/tools/WorkflowIframe'
  import FinancialDetail from '../../financial/dialog/FinancialDetail'
  import JUpload from '@/components/jeecg/JUpload'
  import Vue from 'vue'
  export default {
    name: 'BillDetail',
    components: {
      BillPrintIframe,
      WorkflowIframe,
      FinancialDetail,
      JUpload
    },
    data () {
      return {
        title: "详情",
        width: '1600px',
        visible: false,
        modalStyle: '',
        model: {},
        isCanBackCheck: true,
        showPrice: false,
        billType: '',
        billPrintFlag: false,
        fileList: [],
        purchaseBySaleFlag: false,
        linkNumberList: [],
        relatedNumberList: [],
        financialBillNoList: [],
        btnEnableList: '',
        /* 原始反审核是否开启 */
        checkFlag: true,
        tableWidth: {
          'width': '1500px'
        },
        tableWidthRetail: {
          'width': '1150px'
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        form: this.$form.createForm(this),
        loading: false,
        dataSource: [],
        url: {
          detailList: '/depotItem/getDetailList',
          materialPrepareList: '/depotItem/getMaterialPrepareList',
          batchSetStatusUrl: "/depotHead/batchSetStatus",
          batchSetCheckStatusUrl: "/depotHead/batchSetCheckStatus"
        },
        //表头
        columns:[],
        //列定义
        defColumns: [],
        retailOutColumns: [

        ],
        productionPlanColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '已下产单', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '项目', dataIndex: 'project'},
          { title: '备注', dataIndex: 'remark'}
        ],
        productionOrderColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '产单数量', dataIndex: 'operNumber'},
          { title: '已入库', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '项目', dataIndex: 'project'},
          { title: '备注', dataIndex: 'remark'}
        ],
        materialPrepareColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '需求', dataIndex: 'operNumber'},
          { title: '库存', dataIndex: 'stock'},
          { title: '已领', dataIndex: 'materialPick'},
          { title: '已退', dataIndex: 'materialReturn'},
          { title: '单位', dataIndex: 'unit'},
          { title: '备注', dataIndex: 'remark'}
        ],
        materialPickColumns: [
          { title: '供应商', dataIndex: 'supplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '领料批号', dataIndex: 'batchNumber'},
          { title: '领料货位', dataIndex: 'snListStr'},
          { title: '领料数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        materialReturnColumns: [
          { title: '供应商', dataIndex: 'supplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '退料数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        productionInColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '入库数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '项目', dataIndex: 'project'},
          { title: '备注', dataIndex: 'remark'}
        ],
        retailBackColumns: [

        ],
        purchaseApplicationColumns: [
          { title: '供应商', dataIndex: 'applicationSupplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '申请数量', dataIndex: 'operNumber'},
          { title: '已下单', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '收货地', dataIndex: 'anotherDepotName'},
          { title: '到货日期', dataIndex: 'expirationDate'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseOrderColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '下单数量', dataIndex: 'operNumber'},
          { title: '已入库', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '收货地', dataIndex: 'anotherDepotName'},
          { title: '到货日期', dataIndex: 'expirationDate'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseInColumns: [
          { title: '供应商', dataIndex: 'supplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '入库数量', dataIndex: 'operNumber'},
          { title: '退货数量', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseBackColumns: [
          { title: '供应商', dataIndex: 'supplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '退货数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        saleOrderColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '销单数量', dataIndex: 'operNumber'},
          { title: '已出库', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '项目', dataIndex: 'project'},
          { title: '备注', dataIndex: 'remark'}
        ],
        saleOutColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '出库数量', dataIndex: 'operNumber'},
          { title: '退货数量', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        saleBackColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '退货数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        repairInColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '新货位', dataIndex: 'materialTypeStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '调出仓库', dataIndex: 'depotName'},
          { title: '调入仓库', dataIndex: 'anotherDepotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        repairOutColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '新货位', dataIndex: 'materialTypeStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '调出仓库', dataIndex: 'depotName'},
          { title: '调入仓库', dataIndex: 'anotherDepotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        isolateColumns: [
          { title: '供应商', dataIndex: 'supplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '新货位', dataIndex: 'materialTypeStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        diffInColumns: [
          { title: '供应商', dataIndex: 'supplier'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        otherInColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '项目', dataIndex: 'project'},
          { title: '备注', dataIndex: 'remark'}
        ],
        otherOutColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        allocationOutColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '零件号', dataIndex: 'model'},
          { title: '客/供型号', dataIndex: 'supplierModel'},
          { title: '名称', dataIndex: 'name'},
          { title: '颜色', dataIndex: 'color'},
          { title: '颜色代码', dataIndex: 'colorCode'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '货位', dataIndex: 'snListStr'},
          { title: '新货位', dataIndex: 'materialTypeStr'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '库存', dataIndex: 'stock'},
          { title: '调出仓库', dataIndex: 'depotName'},
          { title: '调入仓库', dataIndex: 'anotherDepotName'},
          { title: '备注', dataIndex: 'remark'}
        ],
        assembleColumns: [

        ],
        disassembleColumns: [

        ],
        stockCheckReplayColumns: [

        ]
      }
    },
    created () {
      let realScreenWidth = window.screen.width
      this.width = realScreenWidth<1500?'1200px':'1550px'
      this.tableWidth = {
        'width': realScreenWidth<1500?'1150px':'1500px'
      }
      this.tableWidthRetail = {
        'width': realScreenWidth<1500?'800px':'1100px'
      }
    },
    methods: {
      initSetting(record, type, ds) {
        if (type === '零售出库') {
          this.defColumns = this.retailOutColumns
        } else if (type === '零售退货入库') {
          this.defColumns = this.retailBackColumns
        } else if (type === '生产单') {
          this.defColumns = this.productionOrderColumns
        } else if (type === '生产计划') {
          this.defColumns = this.productionPlanColumns
        } else if (type === '备料') {
          this.defColumns = this.materialPrepareColumns
        } else if (type === '计划备料') {
          this.defColumns = this.materialPrepareColumns
        } else if (type === '领料出库') {
          this.defColumns = this.materialPickColumns
        } else if (type === '退料入库') {
          this.defColumns = this.materialReturnColumns
        } else if (type === '生产入库') {
          this.defColumns = this.productionInColumns
        } else if (type === '采购申请') {
          this.defColumns = this.purchaseApplicationColumns
        } else if (type === '采购订单') {
          this.defColumns = this.purchaseOrderColumns
        } else if (type === '采购入库') {
          this.defColumns = this.purchaseInColumns
        } else if (type === '采购退货出库') {
          this.defColumns = this.purchaseBackColumns
        } else if (type === '销售订单') {
          this.defColumns = this.saleOrderColumns
        } else if (type === '销售出库') {
          this.defColumns = this.saleOutColumns
        } else if (type === '销售退货入库') {
          this.defColumns = this.saleBackColumns
        } else if (type === '返修入库') {
          this.defColumns = this.repairInColumns
        } else if (type === '返修出库') {
          this.defColumns = this.repairOutColumns
        } else if (type === '隔离出库') {
          this.defColumns = this.isolateColumns
        } else if (type === '差异入库') {
          this.defColumns = this.diffInColumns
        } else if (type === '其它入库') {
          this.defColumns = this.otherInColumns
        } else if (type === '其它出库') {
          this.defColumns = this.otherOutColumns
        } else if (type === '调拨出库') {
          this.defColumns = this.allocationOutColumns
        } else if (type === '组装单') {
          this.defColumns = this.assembleColumns
        } else if (type === '拆卸单') {
          this.defColumns = this.disassembleColumns
        } else if (type === '盘点复盘') {
          this.defColumns = this.stockCheckReplayColumns
        }
        //判断货位、批号、货位是否有值
        let needAddkeywords = []
        for (let i = 0; i < ds.length; i++) {
          if(ds[i].batchNumber) {
            needAddkeywords.push('batchNumber')
          }
          if(ds[i].snList) {
            needAddkeywords.push('snList')
          }
          if(ds[i].weight) {
            needAddkeywords.push('weight')
          }
        }
        if(record.status === '3') {
          //部分采购|部分销售的时候显示全部列
          this.columns = this.defColumns
        } else if(record.purchaseStatus === '3') {
          //将已出库的标题转为已采购，针对销售订单转采购订单的场景
          let currentCol = []
          for(let i=0; i<this.defColumns.length; i++){
            let info = {}
            info.title = this.defColumns[i].title
            info.dataIndex = this.defColumns[i].dataIndex
            info.width = this.defColumns[i].width
            if(this.defColumns[i].dataIndex === 'finishNumber') {
              info.title = '已采购'
            }
            currentCol.push(info)
          }
          this.columns = currentCol
        } else {
          let currentCol = []
          for(let i=0; i<this.defColumns.length; i++){
            //移除列
            let needRemoveKeywords = ['batchNumber','snList','weight']
            if(needRemoveKeywords.indexOf(this.defColumns[i].dataIndex)===-1) {
              let info = {}
              info.title = this.defColumns[i].title
              info.dataIndex = this.defColumns[i].dataIndex
              info.width = this.defColumns[i].width
              currentCol.push(info)
            }
            //添加有数据的列
            if(needAddkeywords.indexOf(this.defColumns[i].dataIndex)>-1) {
              let info = {}
              info.title = this.defColumns[i].title
              info.dataIndex = this.defColumns[i].dataIndex
              info.width = this.defColumns[i].width
              currentCol.push(info)
            }
          }
          this.columns = currentCol
        }
      },
      initPlatform() {
        getPlatformConfigByKey({"platformKey": "bill_print_flag"}).then((res)=> {
          if (res && res.code === 200) {
            if(this.billType === '零售出库'||this.billType === '零售退货入库'||
              this.billType === '采购订单'||this.billType === '采购入库'||this.billType === '采购退货出库'||
              this.billType === '生产单'||this.billType === '领料出库'||
              this.billType === '退料入库'||this.billType === '生产入库'||
              this.billType === '销售订单'||this.billType === '销售出库'||this.billType === '销售退货入库') {
              this.billPrintFlag = res.data.platformValue==='1'?true:false
            }
          }
        })
      },
      getSystemConfig() {
        getCurrentSystemConfig().then((res) => {
          if(res.code === 200 && res.data){
            this.purchaseBySaleFlag = res.data.purchaseBySaleFlag==='1'?true:false
            let multiBillType = res.data.multiBillType
            let multiLevelApprovalFlag = res.data.multiLevelApprovalFlag
            this.checkFlag = getCheckFlag(multiBillType, multiLevelApprovalFlag, this.prefixNo)
          }
        })
      },
      getBillListByLinkNumber(number) {
        getAction('/depotHead/getBillListByLinkNumber', {number: number}).then(res => {
          if(res && res.code === 200){
            this.linkNumberList = res.data
          }
        })
      },
      getRelatedInOutBill(number) {
        getAction('/depotHead/getRelatedInOutBill', {number: number}).then(res => {
          if(res && res.code === 200){
            this.linkNumberList = res.data
          }
        })
      },
      getFinancialBillNoByBillId(billId) {
        getAction('/accountHead/getFinancialBillNoByBillId', {billId: billId}).then(res => {
          if(res && res.code === 200){
            this.financialBillNoList = res.data
          }
        })
      },
      show(record, type, prefixNo, price) {
        if (price) {
          this.showPrice = true
        }
        //查询单条单据信息
        findBillDetailByNumber({ number: record.number }).then((res) => {
          if (res && res.code === 200) {
            let item = res.data
            this.billType = type
            this.prefixNo = prefixNo
            //附件下载
            this.fileList = item.fileName
            this.visible = true
            this.modalStyle = 'top:20px;height: 95%;'
            this.model = Object.assign({}, item)
            if (this.model.backAmount) {
              this.model.getAmount = (this.model.changeAmount + this.model.backAmount).toFixed(2)
            } else {
              this.model.getAmount = this.model.changeAmount
            }
            this.model.debt = (this.model.discountLastMoney + this.model.otherMoney - (this.model.deposit + this.model.changeAmount)).toFixed(2)
            this.$nextTick(() => {
              this.form.setFieldsValue(pick(this.model, 'id'))
            });
            let showType = 'basic'
            if (item.status === '3') {
              showType = 'basic'
            } else if (item.purchaseStatus === '3') {
              showType = 'purchase'
            }
            let params = {
              headerId: this.model.id,
              mpList: getMpListShort(Vue.ls.get('materialPropertyList')),  //扩展属性
              linkType: showType,
              isReadOnly: '1'
            }
            let url = type === '备料' ? this.url.materialPrepareList : this.url.detailList;
            this.requestSubTableData(item, type, url, params);
            this.initPlatform()
            this.initActiveBtnStr()
            this.getSystemConfig()
            this.getBillListByLinkNumber(this.model.number)
            this.getRelatedInOutBill(this.model.number)
            this.getFinancialBillNoByBillId(this.model.id)
          }
        })
      },
      initActiveBtnStr() {
        let btnStrList = Vue.ls.get('winBtnStrList'); //按钮功能列表 JSON字符串
        this.btnEnableList = ""; //按钮列表
        let pathName = location.pathname
        if(pathName.indexOf('/plugins')>-1) {
          pathName = '/system' + pathName
        }
        if (pathName && btnStrList) {
          for (let i = 0; i < btnStrList.length; i++) {
            if (btnStrList[i].url === pathName) {
              if (btnStrList[i].btnStr) {
                this.btnEnableList = btnStrList[i].btnStr;
              }
            }
          }
        }
      },
      requestSubTableData(record, type, url, params, success) {
        this.loading = true
        getAction(url, params).then(res => {
          if(res && res.code === 200){
            this.dataSource = res.data.rows
            this.initSetting(record, type, this.dataSource)
            typeof success === 'function' ? success(res) : ''
          }
        }).finally(() => {
          this.loading = false
        })
      },
      handleBackCheck() {
        let that = this
        this.$confirm({
          title: "确认操作",
          content: "是否对该单据进行反审核?",
          onOk: function () {
            that.loading = true
            postAction(that.url.batchSetStatusUrl, {status: '0', ids: that.model.id}).then((res) => {
              if(res.code === 200){
                that.$emit('ok')
                that.loading = false
                that.close()
              } else {
                that.$message.warning(res.data.message)
                that.loading = false
              }
            }).finally(() => {
            })
          }
        })
      },
      handleCheck() {
        let that = this
        this.$confirm({
          title: "确认操作",
          content: "是否对该单据进行审核?",
          onOk: function () {
            that.loading = true
            postAction(that.url.batchSetStatusUrl, {status: '1', ids: that.model.id}).then((res) => {
              if(res.code === 200){
                that.$emit('ok')
                that.loading = false
                that.close()
              } else {
                that.$message.warning(res.data.message)
                that.loading = false
              }
            }).finally(() => {
            })
          }
        })
      },
      handleBackReceipt() {
        let that = this
        this.$confirm({
          title: "确认操作",
          content: "是否对该单据进行反结算?",
          onOk: function () {
            that.loading = true
            postAction(that.url.batchSetCheckStatusUrl, {status: '0', ids: that.model.id}).then((res) => {
              if(res.code === 200){
                that.$emit('ok')
                that.loading = false
                that.close()
              } else {
                that.$message.warning(res.data.message)
                that.loading = false
              }
            }).finally(() => {
            })
          }
        })
      },
      handleReceipt() {
        let that = this
        this.$confirm({
          title: "确认操作",
          content: "是否对该单据进行结算?",
          onOk: function () {
            that.loading = true
            postAction(that.url.batchSetCheckStatusUrl, {status: '1', ids: that.model.id}).then((res) => {
              if(res.code === 200){
                that.$emit('ok')
                that.loading = false
                that.close()
              } else {
                that.$message.warning(res.data.message)
                that.loading = false
              }
            }).finally(() => {
            })
          }
        })
      },
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
        this.modalStyle = ''
      },
      myHandleDetail(billNumber) {
        findBillDetailByNumber({ number: billNumber }).then((res) => {
          if (res && res.code === 200) {
            let type = res.data.type === "其它"? "":res.data.type
            this.show(res.data, res.data.subType + type);
            this.title = res.data.subType + type + "-详情";
          }
        })
      },
      myHandleFinancialDetail(billNo) {
        let that = this
        findFinancialDetailByNumber({ billNo: billNo }).then((res) => {
          if (res && res.code === 200) {
            if(that.$refs.financialDetailModal) {
              that.$refs.financialDetailModal.show(res.data, res.data.type);
              that.$refs.financialDetailModal.title= res.data.type + "-详情";
            }
          }
        })
      },
      //三联打印预览
      handlePrint() {
        getPlatformConfigByKey({"platformKey": "bill_print_url"}).then((res)=> {
          if (res && res.code === 200) {
            let billPrintUrl = res.data.platformValue + '?no=' + this.model.number
            let billPrintHeight = this.dataSource.length*50 + 600
            this.$refs.modalDetail.show(this.model, billPrintUrl, billPrintHeight)
            this.$refs.modalDetail.title = this.billType + "-三联打印预览"
          }
        })
      },
      //生产单
      productionOrderExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number,
        '', '开始日期：', this.model.planStartTimeStr, '', '生产工时：', this.model.workHour],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '产单数量', '已入库', '单位', '库存', '项目', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode, ds.operNumber,
          ds.finishNumber, ds.unit, ds.stock, ds.project, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //备料
      materialPrepareExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number,
        '', '开始日期：', this.model.planStartTimeStr, '', '生产工时：', this.model.workHour],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '需求', '库存', '已领', '已退', '单位', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode, ds.operNumber,
          ds.stock, ds.materialPick, ds.materialReturn, ds.unit, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //领料
      materialPickExportExcel() {
        let aoa = []
        aoa = [['领料人员：', this.model.salesManStr, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['供应商', '编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码',
          '领料批号', '领料货位', '领料数量', '单位', '库存', '仓库名称', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.supplier, ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.batchNumber, ds.snListStr, ds.operNumber, ds.unit, ds.stock, ds.depotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //退料
      materialReturnExportExcel() {
        let aoa = []
        aoa = [['领料人员：', this.model.salesManStr, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['供应商', '编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码',
          '批号', '货位', '退料数量', '单位', '库存', '仓库名称', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.supplier, ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.batchNumber, ds.snListStr, ds.operNumber, ds.unit, ds.stock, ds.depotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //生产入库
      productionInExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '批号', '货位', '入库数量', '单位', '库存', '仓库名称', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.batchNumber, ds.snListStr, ds.operNumber, ds.unit, ds.stock, ds.depotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //采购申请
      applicationExportExcel() {
        let aoa = []
        aoa = [['单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number, '', '申请人：', this.model.salesManStr],[]]
        let title = ['供应商', '编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码',
          '申请数量', '已下单', '单位', '库存', '收货地', '到货日期', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.operNumber, ds.finishNumber, ds.unit, ds.stock, ds.anotherDepotName, ds.expirationDate, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //采购订单|销售订单
      orderExportExcel() {
        let aoa = []
        let finishType = ''
        let organType = ''
        let location = ''
        let expiration = ''
        if(this.billType === '采购订单') {
          finishType = '已入库'
          organType = '供应商：'
          location = '收货地'
          expiration = '到货日期'
        } else if(this.billType === '销售订单') {
          finishType = '已出库'
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '数量', finishType, '单位', '库存', location, expiration, '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.operNumber, ds.finishNumber, ds.unit, ds.stock, ds.anotherDepotName, ds.expirationDate, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //采购入库|销售出库
      purchaseSaleExportExcel() {
        let aoa = []
        let organType = ''
        if(this.billType === '采购入库') {
          organType = '供应商：'
        } else if(this.billType === '销售出库' || this.billType === '销售退货入库') {
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number, '', '关联单号：', this.model.linkNumber],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '批号', '货位', '数量', '退货数量', '单位', '库存', '仓库名称', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.batchNumber, ds.snListStr, ds.operNumber, ds.finishNumber, ds.unit, ds.stock, ds.depotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //采购退货出库|销售退货入库
      purchaseSaleBackExportExcel() {
        let aoa = []
        let organType = ''
        if(this.billType === '采购入库' || this.billType === '采购退货出库') {
          organType = '供应商：'
        } else if(this.billType === '销售出库' || this.billType === '销售退货入库') {
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number, '', '关联单号：', this.model.linkNumber],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '批号', '货位', '退货数量', '单位', '库存', '仓库名称', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.batchNumber, ds.snListStr, ds.operNumber, ds.unit, ds.stock, ds.depotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //其它入库|其它出库
      otherExportExcel() {
        let aoa = []
        let organType = ''
        if(this.billType === '其它入库') {
          organType = '供应商：'
        } else if(this.billType === '其它出库') {
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '批号', '货位', '数量', '单位', '库存', '仓库名称', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode,
            ds.batchNumber, ds.snListStr, ds.operNumber, ds.unit, ds.stock, ds.depotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //调拨出库
      allocationOutExportExcel() {
        let aoa = []
        aoa = [['单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['编码', '零件号', '客/供型号', '名称', '颜色', '颜色代码', '批号', '货位', '新货位',
          '数量', '单位', '库存', '调出仓库', '调入仓库', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.model, ds.supplierModel, ds.name, ds.color, ds.colorCode, ds.batchNumber,
            ds.snListStr, ds.materialTypeStr, ds.operNumber, ds.unit, ds.stock, ds.depotName, ds.anotherDepotName, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //组装单|拆卸单
      assembleExportExcel() {

      },
      //盘点复盘
      stockCheckReplayExportExcel() {

      }
    }
  }
</script>

<style scoped>

</style>