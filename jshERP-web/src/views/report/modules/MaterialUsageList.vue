<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="1200"
      :visible="visible"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:20px;height: 95%;">
      <template slot="footer">
        <a-button key="back" @click="handleCancel">取消</a-button>
      </template>
      <!-- table区域-begin -->
      <a-table
        bordered
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange">
      </a-table>
      <!-- table区域-end -->
      <!-- 表单区域 -->
    </a-modal>
  </div>
</template>
<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import { openDownloadDialog, sheet2blob} from "@/utils/util"
  import { mixinDevice } from '@/utils/mixin'

  export default {
    name: "MaterialUsageList",
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JEllipsis
    },
    data () {
      return {
        title:"操作",
        visible: false,
        disableMixinCreated: false,
        toFromType: '',
        // 查询条件
        queryParam: {
          productionOrderIds: '',
          productionInNum: '',
          productionUnit: '',
        },
        ipagination:{
          pageSizeOptions: ['10', '20', '30', '100', '200']
        },
        tabKey: "1",
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:40,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          { title: '编码', dataIndex: 'barCode', width: 70},
          { title: '名称', dataIndex: 'materialName', width: 200},
          { title: '用料（领-退）', dataIndex: 'useNum', width: 70},
          { title: '单位', dataIndex: 'materialUnit', width:50 },
          { title: '平均用料（用料/生产数量）', dataIndex: 'averageUsage', width:50 },
        ],
        labelCol: {
          xs: { span: 1 },
          sm: { span: 2 },
        },
        wrapperCol: {
          xs: { span: 10 },
          sm: { span: 16 },
        },
        url: {
          list: "/depotItem/findMaterialUsageByProductionOrderId"
        }
      }
    },
    created() {
    },
    methods: {
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter)
        param.field = this.getQueryField()
        param.currentPage = this.ipagination.current
        param.pageSize = this.ipagination.pageSize
        return param
      },
      show(record, depotIds) {
        this.model = Object.assign({}, record);
        this.visible = true;
        this.queryParam.productionOrderIds = record.productionOrders
        this.queryParam.productionInNum = record.productionIn
        this.queryParam.productionUnit = record.materialUnit
        this.loadData(1)
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      },
      exportExcel() {
        let aoa = [['编码', '名称', '数量']]
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.materialName, ds.basicNumber]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), '用料详情')
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>