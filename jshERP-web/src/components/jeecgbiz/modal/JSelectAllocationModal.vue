<template>
  <a-modal
    :width="modalWidth"
    :visible="visible"
    :title="title"
    @ok="handleSubmit"
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
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关键字">
                  <a-input ref="name" placeholder="请输入货位关键字" v-model="queryParam.name"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类型">
                  <a-select placeholder="选择货位类型" v-model="queryParam.type">
                    <a-select-option value="期初">期初</a-select-option>
                    <a-select-option value="隔离">隔离</a-select-option>
                    <a-select-option value="辅料">辅料</a-select-option>
                    <a-select-option value="原材料">原材料</a-select-option>
                    <a-select-option value="成品">成品</a-select-option>
                    <a-select-option value="半成品">半成品</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-col :md="12" :sm="24">
                  <a-button type="primary" @click="loadData(1)">查询</a-button>
                  <a-button style="margin-left: 8px" @click="searchReset(1)">重置</a-button>
                  <span style="margin-left: 20px">提示：双击行可以直接选中</span>
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
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: getType}"
            :loading="loading"
            :customRow="rowAction"
            @change="handleTableChange">
          </a-table>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
  import { getAction } from '@/api/manage'
  import { getAllocationList } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'JSelectAllocationModal',
    mixins:[JeecgListMixin],
    components: {},
    props: ['rows', 'multi', 'barCode'],
    data() {
      return {
        modalWidth: 1100,
        queryParam: {
          name: '',
          type: '',
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        categoryTree:[],
        columns: [
          {dataIndex: 'id', title: '编号', width: 100, ellipsis:true},
          {dataIndex: 'allocation', title: '名称', width: 100, ellipsis:true},
          {dataIndex: 'type', title: '类型', width: 100, ellipsis:true},
          {dataIndex: 'totalNumber', title: '货物总数', width: 100, ellipsis:true},
        ],
        scrollTrigger: {},
        dataSource: [],
        selectedRowKeys: [],
        selectRows: [],
        selectIds: [],
        title: '选择货位',
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
          column: 'totalNumber',
          order: 'desc'
        },
        departTree: [],
        depotList: [],
        visible: false,
        form: this.$form.createForm(this),
        loading: false,
        expandedKeys: [],
      }
    },
    computed: {
      // 计算属性的 getter
      getType: function () {
        return this.multi == true ? 'checkbox' : 'radio';
      }
    },
    watch: {
      barCode: {
        immediate: true,
        handler() {
          this.initBarCode()
        }
      },
    },
    created() {
      this.loadData()
    },
    methods: {
      initBarCode() {
        if (this.barCode) {
          this.$emit('initComp', this.barCode)
        } else {
          // JSelectUserByDep组件bug issues/I16634
          this.$emit('initComp', '')
        }
      },
      loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true
        let params = this.getQueryParams()//查询条件
        getAllocationList(params).then((res) => {
          if (res && res.code === 200) {
            this.dataSource = res.data.rows
            this.ipagination.total = res.data.total
          }
        }).finally(() => {
          this.loading = false
        })
      },
      showModal() {
        this.visible = true
        this.$nextTick(() => this.$refs.name.focus())
        this.loadData()
        this.form.resetFields()
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.currentPage = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return param;
      },
      getQueryField() {
        let str = 'id,';
        for (let a = 0; a < this.columns.length; a++) {
          str += ',' + this.columns[a].dataIndex;
        }
        return str;
      },
      searchReset(num) {
        let that = this;
        if (num !== 0) {
          if(this.rows) {
            this.queryParam.name=''
            this.queryParam.type=''
          }
          that.loadData(1);
        }
        that.selectedRowKeys = [];
        that.selectIds = [];
      },
      close() {
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
      handleSubmit() {
        let that = this;
        this.getSelectRows();
        that.$emit('ok', that.selectRows, that.selectIds);
        that.searchReset(0)
        that.close();
      },
      //获取选择信息
      getSelectRows(rowId) {
        let dataSource = this.dataSource;
        let ids = "";
        this.selectRows = [];
        console.log("selectedRowKeys: " + JSON.stringify(this.selectedRowKeys))
        for (let i = 0, len = this.selectedRowKeys.length; i < len; i++) {
          ids = ids + "," + this.selectedRowKeys[i]
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
      modalFormOk() {
        this.loadData();
      },
      rowAction(record, index) {
        return {
          on: {
            click: () => {
              let arr = []
              arr.push(record.id)
              this.selectedRowKeys = arr
            },
            dblclick: () => {
              let arr = []
              arr.push(record.id)
              this.selectedRowKeys = arr
              this.handleSubmit()
            }
          }
        }
      }
    }
  }
</script>

<style scoped>
  .ant-table-tbody .ant-table-row td {
    padding-top: 10px;
    padding-bottom: 10px;
  }

  #components-layout-demo-custom-trigger .trigger {
    font-size: 18px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color .3s;
  }
</style>