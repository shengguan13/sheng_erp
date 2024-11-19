<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="1600"
      :visible="visible"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:20px;height: 95%;">
      <a-table
        bordered
        ref="table"
        size="middle"
        rowKey="id"
        :pagination="false"
        :columns="columnsDetail"
        :dataSource="dataSourceDetail"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedDetailRowKeys, onChange: onSelectDetailChange, type: 'checkbox'}"
        @change="handleTableChange">
      </a-table>
      <!-- table区域-end -->
    </a-modal>
  </div>
</template>

<script>
  import {mixinDevice} from '@/utils/mixin'
  import { getAction } from '@/api/manage'
  import Vue from 'vue'
  export default {
    name: 'LinkBillList',
    mixins:[mixinDevice],
    components: {
    },
    data () {
      return {
        title: "操作",
        visible: false,
        disableMixinCreated: true,
        selectedRowKeys: [],
        selectedDetailRowKeys: [],
        selectBillRows: [],
        selectBillDetailRows: [],
        showType: 'basic',
        selectType: 'list',
        linkNumber: '',
        organId: '',
        payType: '',
        remark: '',
        record: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        columnsDetail: [
          { title: '编码', dataIndex: 'barCode',width:50},
          { title: '名称', dataIndex: 'name',width:100, ellipsis:true},
          { title: '客/供型号', dataIndex: 'supplierModel',width:100, ellipsis:true},
          { title: '零件号', dataIndex: 'model',width:100, ellipsis:true},
          { title: '扩展信息', dataIndex: 'materialOther',width:50, ellipsis:true},
          { title: '数量', dataIndex: 'operNumber',width:50},
          { title: '已完成/已退货', dataIndex: 'finishNumber',width:60},
          { title: '单位', dataIndex: 'unit',width:40},
          { title: '到货日期', dataIndex: 'expirationDate',width:50},
          { title: '收货地', dataIndex: 'anotherDepotName',width:60},
          { title: '备注', dataIndex: 'remark',width:100, ellipsis:true},
        ],
        dataSourceDetail: []
      }
    },
    computed: {
      getType: function () {
        return 'radio';
      }
    },
    created() {
    },
    methods: {
      show(record) {
        this.record = record
        this.title = "选择单据明细"
        this.linkNumber = record.number
        this.payType = record.payType
        this.organId = record.organId
        this.loadDetailData(1)
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      },
      onSelectChange(selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys;
      },
      onSelectDetailChange(selectedRowKeys) {
        this.selectedDetailRowKeys = selectedRowKeys;
      },
      handleOk () {
        if(this.selectedDetailRowKeys.length) {
          this.getSelectBillDetailRows()
          this.$emit('ok', this.selectBillDetailRows, this.linkNumber, this.payType, this.organId, this.remark)
          this.close()
        } else {
          this.$message.warning('抱歉，请选择单据明细！')
        }
      },
      //查询明细列表
      loadDetailData(arg) {
        let param = {
          headerId: this.record.id,
          mpList : '',
          linkType: this.showType
        }
        this.loading = true;
        getAction('/depotItem/getDetailList', param).then((res) => {
          if (res.code===200) {
            let list = res.data.rows;
            let listEx = []
            for(let j=0; j<list.length; j++){
              let info = list[j];
              listEx.push(info)
            }
            this.dataSourceDetail = listEx
            for (let i = 0, len = this.dataSourceDetail.length; i < len; i++) {
              this.selectedDetailRowKeys.push(this.dataSourceDetail[i].id);
              this.selectBillDetailRows.push(this.dataSourceDetail[i]);
            }
          }
          if(res.code===510){
            this.$message.warning(res.data)
          }
          this.loading = false;
        })
      },
      getSelectBillDetailRows() {
        let dataSource = this.dataSourceDetail;
        this.selectBillDetailRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedDetailRowKeys.includes(dataSource[i].id)) {
            this.selectBillDetailRows.push(dataSource[i]);
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>