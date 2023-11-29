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
      <a-button v-if="billType === '零售出库'" v-print="'#retailOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '零售退货入库'" v-print="'#retailBackPrint'">普通打印</a-button>
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
      <a-button v-if="billType === '领料出库'" v-print="'#materialPickPrint'">普通打印</a-button>
      <a-button v-if="billType === '退料入库'" v-print="'#materialReturnPrint'">普通打印</a-button>
      <a-button v-if="billType === '生产入库'" v-print="'#productionInPrint'">普通打印</a-button>
      <a-button v-if="billType === '返修入库'" v-print="'#repairInPrint'">普通打印</a-button>
      <a-button v-if="billType === '其它入库'" v-print="'#otherInPrint'">普通打印</a-button>
      <a-button v-if="billType === '其它出库'" v-print="'#otherOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '调拨出库'" v-print="'#allocationOutPrint'">普通打印</a-button>
      <a-button v-if="billType === '组装单'" v-print="'#assemblePrint'">普通打印</a-button>
      <a-button v-if="billType === '拆卸单'" v-print="'#disassemblePrint'">普通打印</a-button>
      <a-button v-if="billType === '盘点复盘'" v-print="'#stockCheckReplayPrint'">普通打印</a-button>
      <!--导出Excel-->
      <a-button v-if="billType === '零售出库'||billType === '零售退货入库'" @click="retailExportExcel()">导出</a-button>
      <a-button v-if="billType === '采购申请'" @click="applicationExportExcel()">导出</a-button>
      <a-button v-if="billType === '采购订单'||billType === '销售订单'" @click="orderExportExcel()">导出</a-button>
      <a-button v-if="billType === '采购入库'||billType === '采购退货出库'||billType === '销售出库'||billType === '销售退货入库'"
                @click="purchaseSaleExportExcel()">导出</a-button>
      <a-button v-if="billType === '生产计划'" @click="productionPlanExportExcel()">导出</a-button>
      <a-button v-if="billType === '生产单'" @click="productionOrderExportExcel()">导出</a-button>
      <a-button v-if="billType === '备料'" @click="materialPrepareExportExcel()">导出</a-button>
      <a-button v-if="billType === '领料出库'" @click="materialPickExportExcel()">导出</a-button>
      <a-button v-if="billType === '退料入库'" @click="materialReturnExportExcel()">导出</a-button>
      <a-button v-if="billType === '生产入库'" @click="productionInExportExcel()">导出</a-button>
      <a-button v-if="billType === '其它入库'||billType === '返修入库'||billType === '其它出库'" @click="otherExportExcel()">导出</a-button>
      <a-button v-if="billType === '调拨出库'" @click="allocationOutExportExcel()">导出</a-button>
      <a-button v-if="billType === '组装单'||billType === '拆卸单'" @click="assembleExportExcel()">导出</a-button>
      <a-button v-if="billType === '盘点复盘'" @click="stockCheckReplayExportExcel()">导出</a-button>
      <!--反审核-->
      <a-button v-if="checkFlag && isCanBackCheck && model.status==='1'" @click="handleBackCheck()">反审核</a-button>
      <a-button key="back" @click="handleCancel">取消</a-button>
      <!--发起多级审核-->
      <a-button v-if="!checkFlag && model.status==='0'" @click="handleWorkflow()" type="primary">提交流程</a-button>
    </template>
    <a-form :form="form">
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
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划开始日期（含）">
                <a-input v-decorator="['id']" hidden/>
                {{model.planStartTimeStr}}
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划完成日期（含）">
                {{model.planFinishTimeStr}}
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
      <!--生产单-->
      <template v-else-if="billType === '生产单'">
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
      <!--零售出库-->
      <template v-else-if="billType === '零售出库'">
        <section ref="print" id="retailOutPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="会员卡号">
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
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="收款类型">
                {{model.payType}}
              </a-form-item>
            </a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :lg="18" :md="12" :sm="24">
              <div :style="tableWidthRetail">
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
            </a-col>
            <a-col :span="6">
              <a-row class="form-row" :gutter="24">
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据金额">
                    {{model.changeAmount}}
                  </a-form-item>
                </a-col>
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="收款金额">
                    {{model.getAmount}}
                  </a-form-item>
                </a-col>
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="找零">
                    {{model.backAmount}}
                  </a-form-item>
                </a-col>
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="收款账户">
                    {{model.accountName}}
                  </a-form-item>
                </a-col>
                <a-col v-if="model.hasBackFlag" :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="退货单号">
                    <template v-for="(item, index) in linkNumberList">
                      <a @click="myHandleDetail(item.number)">{{item.number}}</a><br/>
                    </template>
                  </a-form-item>
                </a-col>
              </a-row>
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
      <!--零售退货-->
      <template v-else-if="billType === '零售退货入库'">
        <section ref="print" id="retailBackPrint">
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="会员卡号">
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
            <a-col :lg="18" :md="12" :sm="24">
              <div :style="tableWidthRetail">
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
            </a-col>
            <a-col :span="6">
              <a-row class="form-row" :gutter="24">
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据金额">
                    {{model.changeAmount}}
                  </a-form-item>
                </a-col>
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="付款金额">
                    {{model.getAmount}}
                  </a-form-item>
                </a-col>
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="找零">
                    {{model.backAmount}}
                  </a-form-item>
                </a-col>
                <a-col :lg="24" :md="6" :sm="6">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="付款账户">
                    {{model.accountName}}
                  </a-form-item>
                </a-col>
              </a-row>
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
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联订单">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
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
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已申请定金">
                {{model.changeAmount}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已申请付款">
                {{model.discount}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已提交退款">
                {{model.otherMoney}}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已支付定金">
                {{model.backAmount}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已付款">
                {{model.discountMoney}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已退款">
                {{model.deposit}}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
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
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联订单">
                <a @click="myHandleDetail(model.linkNumber)">{{model.linkNumber}}</a>
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
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="销售人员">
                {{model.salesManStr}}
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
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已提交定金">
                {{model.changeAmount}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已提交收款">
                {{model.discount}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已申请退款">
                {{model.otherMoney}}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
          </a-row>
          <a-row class="form-row" :gutter="24">
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已收到定金">
                {{model.backAmount}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已收款">
                {{model.discountMoney}}
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item v-if="showPrice" :labelCol="labelCol" :wrapperCol="wrapperCol" label="已退款">
                {{model.deposit}}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
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
                {{model.linkNumber}} {{model.billType}}
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
                {{model.linkNumber}} {{model.billType}}
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
                {{model.linkNumber}} {{model.billType}}
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
            <a-col :span="6"></a-col>
            <a-col :span="6"></a-col>
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
      <!--组装单-->
      <template v-else-if="billType === '组装单'">
        <section ref="print" id="assemblePrint">
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
            <a-col :span="6"></a-col>
            <a-col :span="6"></a-col>
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
      <!--拆卸单-->
      <template v-else-if="billType === '拆卸单'">
        <section ref="print" id="disassemblePrint">
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
            <a-col :span="6"></a-col>
            <a-col :span="6"></a-col>
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
                {{model.linkNumber}}
              </a-form-item>
            </a-col>
            <a-col :span="6"></a-col>
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
      <div id="otherInPrint">
        <!-- 这里放置你要打印的内容，比如图片 -->
        <img src="../../../assets/checkcode.png" alt="打印图片">
      </div>
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
        financialBillNoList: [],
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
          batchSetStatusUrl: "/depotHead/batchSetStatus"
        },
        //表头
        columns:[],
        //列定义
        defColumns: [],
        retailOutColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单价', dataIndex: 'unitPrice'},
          { title: '金额', dataIndex: 'allPrice'},
          { title: '备注', dataIndex: 'remark'}
        ],
        productionPlanColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '库存', dataIndex: 'stock'},
          { title: '已下生产单', dataIndex: 'planOrderedNumber'},
          { title: '已入库', dataIndex: 'finishNumber'},
          { title: '计划生产数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '备注', dataIndex: 'remark'}
        ],
        productionOrderColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '库存', dataIndex: 'stock'},
          { title: '已入库', dataIndex: 'finishNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        materialPrepareColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '工艺流程', dataIndex: 'process'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '备料数量', dataIndex: 'prepareNumber'},
          { title: '已领', dataIndex: 'materialPick'},
          { title: '已退', dataIndex: 'materialReturn'},
          { title: '单位', dataIndex: 'unit'}
        ],
        materialPickColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '库存', dataIndex: 'stock'},
          { title: '领料数量', dataIndex: 'operNumber'},
          { title: '退料数量', dataIndex: 'finishNumber'},
          { title: '领料批号', dataIndex: 'batchNumber'},
          { title: '领料货位', dataIndex: 'snList'},
          { title: '单位', dataIndex: 'unit'},
          { title: '备注', dataIndex: 'remark'}
        ],
        materialReturnColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '库存', dataIndex: 'stock'},
          { title: '退料数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '备注', dataIndex: 'remark'}
        ],
        productionInColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '库存', dataIndex: 'stock'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单位', dataIndex: 'unit'},
          { title: '备注', dataIndex: 'remark'}
        ],
        retailBackColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单价', dataIndex: 'unitPrice'},
          { title: '金额', dataIndex: 'allPrice'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseApplicationColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '申请人', dataIndex: 'salesManStr'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseOrderColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '已入库', dataIndex: 'finishNumber'},
          { title: '金额', dataIndex: 'allPrice'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseInColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '退货', dataIndex: 'finishNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        purchaseBackColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        saleOrderColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '已出库', dataIndex: 'finishNumber'},
          { title: '金额', dataIndex: 'allPrice'},
          { title: '备注', dataIndex: 'remark'}
        ],
        saleOutColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '退货', dataIndex: 'finishNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        saleBackColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        repairInColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        otherInColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        otherOutColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        allocationOutColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '调入仓库', dataIndex: 'anotherDepotName'},
          { title: '单位', dataIndex: 'unit'},
          { title: '货位', dataIndex: 'snList'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        assembleColumns: [
          { title: '产品类型', dataIndex: 'mType'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        disassembleColumns: [
          { title: '产品类型', dataIndex: 'mType'},
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '颜色', dataIndex: 'color'},
          { title: '项目', dataIndex: 'project'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '备注', dataIndex: 'remark'}
        ],
        stockCheckReplayColumns: [
          { title: '仓库名称', dataIndex: 'depotName'},
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'internalId'},
          { title: '规格', dataIndex: 'model'},
          { title: '类别', dataIndex: 'categoryName'},
          { title: '扩展信息', dataIndex: 'materialOther'},
          { title: '库存', dataIndex: 'stock'},
          { title: '单位', dataIndex: 'unit'},
          { title: '数量', dataIndex: 'operNumber'},
          { title: '单价', dataIndex: 'unitPrice'},
          { title: '金额', dataIndex: 'allPrice'},
          { title: '备注', dataIndex: 'remark'}
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
        } else if (type === '生产计划') {
          this.defColumns = this.productionPlanColumns
        } else if (type === '生产单') {
          this.defColumns = this.productionOrderColumns
        } else if (type === '备料') {
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
              this.billType === '生产计划'||this.billType === '生产单'||this.billType === '领料出库'||
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
            this.getSystemConfig()
            this.getBillListByLinkNumber(this.model.number)
            this.getFinancialBillNoByBillId(this.model.id)
          }
        })
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
      //发起流程
      handleWorkflow() {
        getPlatformConfigByKey({"platformKey": "send_workflow_url"}).then((res)=> {
          if (res && res.code === 200) {
            let sendWorkflowUrl = res.data.platformValue + '?no=' + this.model.number + '&type=1'
            this.$refs.modalWorkflow.show(this.model, sendWorkflowUrl, 320)
            this.$refs.modalWorkflow.title = "发起流程"
          }
        })
      },
      //零售出库|零售退货入库
      retailExportExcel() {
        let aoa = []
        aoa = [['会员卡号：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '单位', '货位', '批号', '数量', '单价', '金额', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.materialOther, ds.stock, ds.unit,
            ds.snList, ds.batchNumber, ds.operNumber, ds.unitPrice, ds.allPrice, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //生产计划
      productionPlanExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number,
        '', '开始生产时间（含）：', this.model.planStartTimeStr, '', '完成生产时间（含）：', this.model.planFinishTimeStr],[]]
        let title = ['编码', '名称', '型号', '规格', '类别', '颜色', '项目', '当前库存', '已下生产单', '已入库', '计划生产数量', '单位', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project,
          ds.stock, ds.planOrderedNumber, ds.finishNumber, ds.operNumber, ds.unit, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //生产单
      productionOrderExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number,
        '', '开始日期：', this.model.planStartTimeStr, '', '生产工时：', this.model.workHour],[]]
        let title = ['编码', '名称', '型号', '规格', '类别', '颜色', '项目', '当前库存', '已入库', '计划生产数量', '单位', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project,
          ds.stock, ds.finishNumber, ds.operNumber, ds.unit, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //备料
      materialPrepareExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number,
        '', '开始日期：', this.model.planStartTimeStr, '', '生产工时：', this.model.workHour],[]]
        let title = ['编码', '名称', '型号', '规格', '类别', '颜色', '项目', '备料数量', '已领', '已退', '单位']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project,
          ds.prepareNumber, ds.materialPick, ds.materialReturn, ds.unit]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //领料
      materialPickExportExcel() {
        let aoa = []
        aoa = [['领料人员：', this.model.salesManStr, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '库存', '领料数量', '退料数量', '单位', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.stock,
             ds.preNumber, ds.finishNumber, ds.unit, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //退料
      materialReturnExportExcel() {
        let aoa = []
        aoa = [['领料人员：', this.model.salesManStr, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '库存', '退料数量', '单位', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.stock,
             ds.operNumber, ds.unit, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //生产入库
      productionInExportExcel() {
        let aoa = []
        aoa = [['客户：', this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '批号', '数量', '单位', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.stock,
            ds.batchNumber, ds.operNumber, ds.unit, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //采购申请
      applicationExportExcel() {
      },
      //采购订单|销售订单
      orderExportExcel() {
        let aoa = []
        let finishType = ''
        let organType = ''
        if(this.billType === '采购订单') {
          finishType = '已入库'
          organType = '供应商：'
        } else if(this.billType === '销售订单') {
          finishType = '已出库'
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '单位', '数量', finishType, '单价', '金额', '税率(%)', '税额', '价税合计', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.materialOther, ds.stock, ds.unit,
            ds.operNumber, ds.finishNumber, ds.unitPrice, ds.allPrice, ds.taxRate, ds.taxMoney, ds.taxLastMoney, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //采购入库|采购退货出库|销售出库|销售退货入库
      purchaseSaleExportExcel() {
        let aoa = []
        let organType = ''
        if(this.billType === '采购入库' || this.billType === '采购退货出库') {
          organType = '供应商：'
        } else if(this.billType === '销售出库' || this.billType === '销售退货入库') {
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number, '', '关联单号：', this.model.linkNumber],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '单位', '货位', '批号', '数量', '单价', '金额', '税率(%)', '税额', '价税合计', '重量', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.materialOther, ds.stock, ds.unit,
            ds.snList, ds.batchNumber, ds.operNumber, ds.unitPrice, ds.allPrice, ds.taxRate, ds.taxMoney, ds.taxLastMoney, ds.weight, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //其它入库|其它出库|返修入库
      otherExportExcel() {
        let aoa = []
        let organType = ''
        if(this.billType === '其它入库' || this.billType === '返修入库') {
          organType = '供应商：'
        } else if(this.billType === '其它出库') {
          organType = '客户：'
        }
        aoa = [[organType, this.model.organName, '', '单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '单位', '货位', '批号', '数量', '单价', '金额', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.materialOther, ds.stock, ds.unit,
            ds.snList, ds.batchNumber, ds.operNumber, ds.unitPrice, ds.allPrice, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //调拨出库
      allocationOutExportExcel() {
        let aoa = []
        aoa = [['单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '调入仓库', '单位', '数量', '单价', '金额', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.materialOther, ds.stock, ds.anotherDepotName, ds.unit,
             ds.operNumber, ds.unitPrice, ds.allPrice, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //组装单|拆卸单
      assembleExportExcel() {
        let aoa = []
        aoa = [['单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number],[]]
        let title = ['产品类型', '仓库名称', '编码', '名称', '型号', '规格', '类别', '颜色', '项目', '扩展信息', '库存', '单位', '数量', '单价', '金额', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.mType, ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.color, ds.project, ds.materialOther, ds.stock, ds.unit,
             ds.operNumber, ds.unitPrice, ds.allPrice, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      },
      //盘点复盘
      stockCheckReplayExportExcel() {
        let aoa = []
        aoa = [['单据日期：', this.model.operTimeStr, '', '单据编号：', this.model.number, '', '关联单据：', this.model.linkNumber],[]]
        let title = ['仓库名称', '编码', '名称', '型号', '规格', '类别', '扩展信息', '库存', '单位', '数量', '单价', '金额', '备注']
        aoa.push(title)
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.depotName, ds.barCode, ds.name, ds.internalId, ds.model, ds.categoryName, ds.materialOther, ds.stock, ds.unit,
             ds.operNumber, ds.unitPrice, ds.allPrice, ds.remark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), this.billType + '_' + this.model.number)
      }
    }
  }
</script>

<style scoped>

</style>