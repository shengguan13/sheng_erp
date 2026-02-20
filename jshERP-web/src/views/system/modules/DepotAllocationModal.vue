<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
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
      style="top:100px;height: 90%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item label="仓库名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-select placeholder="请选择仓库" v-decorator="[ 'depotId', validatorRules.depotId ]"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option v-for="(depot,index) in depotList" :value="depot.id">
                {{ depot.depotName }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="货位">
            <a-input placeholder="请输入货位" v-decorator.trim="[ 'allocation', validatorRules.allocation]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="货位分类">
            <a-select placeholder="选择货位分类" v-decorator="[ 'type', validatorRules.type ]"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option value="总成">总成</a-select-option>
              <a-select-option value="半成品">半成品</a-select-option>
              <a-select-option value="原材料">原材料</a-select-option>
              <a-select-option value="辅料">辅料</a-select-option>
              <a-select-option value="隔离">隔离</a-select-option>
              <a-select-option value="返修中">返修中</a-select-option>
              <a-select-option value="项目">项目</a-select-option>
              <a-select-option value="供应商">供应商</a-select-option>
              <a-select-option value="门卫室">门卫室</a-select-option>
              <a-select-option value="外检区">外检区</a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addDepotAllocation,editDepotAllocation } from '@/api/api'
  import {mixinDevice} from '@/utils/mixin'
  import { getAction } from '@api/manage'
  export default {
    name: "DepotAllocationModal",
    mixins: [mixinDevice],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        depotList: [],
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
          depotId:{
            rules: [
              { required: true, message: '请选择仓库!' }
            ]
          },
          allocation:{
            rules: [
              { required: true, message: '请输入货位!' }
            ]
          },
          type:{
            rules: [
              { required: true, message: '请选择货位分类!' }
            ]
          }
        }
      }
    },
    created () {
      getAction('/depot/findDepotByCurrentUser').then((res)=>{
        if(res.code === 200){
          this.depotList = res.data;
        }else{
          this.$message.info(res.data);
        }
      })
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'allocation', 'depotId', 'type'))
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
            let obj;
            if(!this.model.id){
              obj=addDepotAllocation(formData);
            }else{
              obj=editDepotAllocation(formData);
            }
            obj.then((res)=>{
              if(res.code === 200){
                that.$emit('ok');
              }else{
                that.$message.warning(res.data.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
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