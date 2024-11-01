<template>
  <div ref="container">
    <a-modal
      :width="modalWidth"
      :visible="visible"
      :title="title"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭"
      style="top:12%;height: 90%;overflow-y: hidden"
      wrapClassName="ant-modal-cust-warp">
      <a-row :gutter="10" style="padding: 10px; margin: -10px">
        <a-col :md="24" :sm="24">
          <!-- 查询区域 -->
          <div class="table-page-search-wrapper">
            <!-- 搜索区域 -->
            <a-form layout="inline" @keyup.enter.native="onSearch">
              <a-row :gutter="24">
                <a-col :md="6" :sm="24">
                  <a-form-item label="客商名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="请选择客商" showSearch optionFilterProp="children" v-model="queryParam.supplierId">
                      <a-select-option v-for="(supplier,index) in supplierList" :value="supplier.id">
                        {{ supplier.supplier }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入编码、名称、型号、规格查询" v-model="queryParam.keyword"></a-input>
                  </a-form-item>
                </a-col>
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-col :md="12" :sm="24">
                    <a-button type="primary" @click="loadData(1)">查询</a-button>
                    <a-button style="margin-left: 8px" @click="searchReset(1)">重置</a-button>
                  </a-col>
                </span>
              </a-row>
            </a-form>
            <a-table
              ref="table"
              :scroll="scrollTrigger"
              size="middle"
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource"
              :pagination="ipagination"
              :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: 'checkbox'}"
              :loading="loading"
              :customRow="rowAction"
              @change="handleTableChange">
            </a-table>
          </div>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import { postAction } from '@/api/manage'
  import { listProductSupplier } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  export default {
    name: "SelectProductSupplier",
    mixins: [JeecgListMixin],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        isReadOnly: false,
        modalWidth: 1600,
        queryParam: {
          supplierId: '',
          keyword: '',
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        supplierList:[],
        confirmLoading: false,
        scrollTrigger: {},
        dataSource: [],
        selectedRowKeys: [],
        selectRows: [],
        selectIds: [],
        title: '选择客商档案',
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc'
        },
        form: this.$form.createForm(this),
        loading: false,
        columns: [
          {title: '客商名称', dataIndex: 'supplierName', width: 100},
          {title: '制造商', dataIndex: 'manufactory', width: 100},
          {title: '客商类别', dataIndex: 'supplierType', width: 40},
          {title: '物料编码', dataIndex: 'barCode', width: 40},
          {title: '物料名称', dataIndex: 'materialName', width: 100},
          {title: '客/供型号', dataIndex: 'model', width: 80},
          {title: '零件号', dataIndex: 'materialModel', width: 60},
          {title: '标包', dataIndex: 'pack', width: 40},
          {title: '颜色', dataIndex: 'color', width: 40},
          {title: '颜色代码', dataIndex: 'colorCode', width: 40},
          {title: '规格', dataIndex: 'otherField5', width: 40}
        ],
      }
    },
    created() {
      postAction('/supplier/findBySelect_sup').then((res)=>{
        this.supplierList = res;
      })
      this.loadData()
    },
    methods: {
      loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true
        let params = this.getQueryParams()//查询条件
        listProductSupplier(params).then((res) => {
          if (res && res.code === 200) {
            this.dataSource = res.data.rows
            this.ipagination.total = res.data.total
          }
        }).finally(() => {
          this.loading = false
        })
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.currentPage = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return param;
      },
      searchReset(num) {
        let that = this;
        this.queryParam.supplierId=''
        this.queryParam.keyword=''
        that.loadData(1);
        that.selectedRowKeys = [];
        that.selectIds = [];
      },
      add () {
        console.log("SelectProductSupplier add()")
        this.edit({});
        this.loadData()
      },
      edit (record) {
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.searchReset(0);
        this.visible = false;
      },
      handleTableChange(pagination, filters, sorter) {
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc';
        }
        this.ipagination = pagination;
        this.loadData();
      },
      handleOk () {
        const that = this;
        that.confirmLoading = true;
        this.getSelectRows();
        that.$emit('ok', this.selectIds);
        this.searchReset(0);
        that.confirmLoading = false;
        that.close();
      },
      handleCancel () {
        this.close()
      },
      //获取选择信息
      getSelectRows(rowId) {
        let dataSource = this.dataSource;
        let ids = "";
        this.selectRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedRowKeys.includes(dataSource[i].id)) {
            this.selectRows.push(dataSource[i]);
            ids = ids + "," + dataSource[i].id
          }
        }
        this.selectIds = ids.substring(1);
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      onSearch() {
        this.loadData(1)
      },
      rowAction(record, index) {
        return {
          on: {
            click: () => {
              let arr = []
              arr.push(record.id)
              this.selectedRowKeys = arr
            }
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>