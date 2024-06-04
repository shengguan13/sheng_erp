<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="500"
      :visible="visible"
      :confirmLoading="confirmLoading"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:30%;height: 35%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form" id="batchSetAllocation">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="货位名称">
            <a-select placeholder="请选择货位" v-decorator="[ 'snList', validatorRules.snList ]" showSearch optionFilterProp="children">
              <a-select-option v-for="(allocation,index) in allocationList" :value="allocation.value">
                {{ allocation.text }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import { getAction } from '@/api/manage'
  import {mixinDevice} from '@/utils/mixin'
  export default {
    name: "BatchSetAllocation",
    mixins: [mixinDevice],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        allocationList: [],
        isReadOnly: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          snList:{
            rules: [
              { required: true, message: '请选择货位!' }
            ]
          }
        },
      }
    },
    created () {
    },
    methods: {
      getAllocationData() {
        getAction('/depot/findAllocation').then((res) => {
          if(res.code === 200){
            let arr = res.data
            let info1 = {};
            info1.value = 564
            info1.text = "期初-期初"
            let info2 = {};
            info2.value = 565
            info2.text = "隔离-隔离"
            this.allocationList.push(info1)
            this.allocationList.push(info2)
            for(let i=0; i<arr.length; i++) {
              if( arr[i].allocation!='期初' && arr[i].allocation!='隔离' ) {
                let allocationInfo = {};
                let text = arr[i].allocation + '-' + arr[i].type;
                allocationInfo.value = arr[i].id
                allocationInfo.text = text
                this.allocationList.push(allocationInfo)
              }
            }
          }
        })
      },
      add () {
        this.edit({});
        this.getAllocationData()
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'snList'))
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let formData = Object.assign(this.model, values);
            let snList = formData.snList
            that.$emit('ok', snList);
            that.confirmLoading = false;
            that.close();
          }
        })
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>

<style scoped>

</style>