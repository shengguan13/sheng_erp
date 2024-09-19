<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="width"
      :visible="visible"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="ant-modal-cust-warp"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:30%;height: 35%;">
      <template slot="footer">
        <a-button key="back" @click="handleCancel">取消</a-button>
      </template>
      <a-form :form="form">
        <template>
          <section>
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
          </section>
        </template>
      </a-form>
    </a-modal>
  </div>
</template>
<script>
  import Vue from 'vue'
  import { mixinDevice } from '@/utils/mixin'
  export default {
    name: 'AllocationStock',
    mixins: [mixinDevice],
    components: {},
    data () {
      return {
        title: '',
        width: '1600px',
        visible: false,
        tableWidth: {
          'width': '1500px'
        },
        form: this.$form.createForm(this),
        loading: false,
        dataSource: [],
        //表头
        columns:[],
        //列定义
        defColumns: [
          { title: '编码', dataIndex: 'barCode'},
          { title: '名称', dataIndex: 'name'},
          { title: '型号', dataIndex: 'model'},
          { title: '批号', dataIndex: 'batchNumber'},
          { title: '数量', dataIndex: 'totalNum'},
        ]
      }
    },
    created () {},
    methods: {
      show(data) {
        this.visible = true
        this.dataSource = data
        this.columns = this.defColumns
      },
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      }
    }
  }
</script>

<style scoped>

</style>