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
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目">
            <a-input placeholder="请输入项目" v-decorator.trim="[ 'project', validatorRules.project]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工艺流程">
            <a-input placeholder="请输入工艺流程" v-decorator.trim="[ 'process', validatorRules.process]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料编码">
            <a-input placeholder="请输入物料编码" v-decorator.trim="[ 'barCode' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用量">
            <a-input placeholder="请输入用量" v-decorator.trim="[ 'processUsage' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单位">
            <a-input placeholder="请输入单位" v-decorator.trim="[ 'unit' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料来源">
            <a-input placeholder="请输入物料来源" v-decorator.trim="[ 'source' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="执行部门">
            <a-input placeholder="请输入执行部门" v-decorator.trim="[ 'department' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addMaterialBom,editMaterialBom,checkMaterialAttribute } from '@/api/api'
  import {mixinDevice} from '@/utils/mixin'
  export default {
    name: "MaterialBomModal",
    mixins: [mixinDevice],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
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
          project:{
            rules: [
              { required: true, message: '请输入项目!' }
            ]
          },
          process:{
            rules: [
              { required: true, message: '请输入工艺流程!' }
            ]
          }
        }
      }
    },
    created () {
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
          this.form.setFieldsValue(pick(this.model, 'project', 'process', 'barCode', 'processUsage',
          'unit', 'source', 'department'))
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
              obj=addMaterialBom(formData);
            }else{
              obj=editMaterialBom(formData);
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