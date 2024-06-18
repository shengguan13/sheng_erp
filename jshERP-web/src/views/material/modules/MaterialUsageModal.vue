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
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料编码">
            <a-input placeholder="请输入物料编码" v-decorator.trim="[ 'barCode', validatorRules.barCode ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="周用量">
            <a-input placeholder="请输入周用量" v-decorator.trim="[ 'number', validatorRules.number ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请部门">
            <a-select placeholder="选择部门" v-model="selectedDepartment"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option v-for="(item,index) in departmentList.options" :key="index" :value="item.value">
                {{ item.text }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请人">
            <a-select placeholder="选择申请人" v-decorator="[ 'salesMan', validatorRules.salesMan ]"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option v-for="(item,index) in dynamicOptions" :key="index" :value="item.value">
                {{ item.text }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请原因">
            <a-input placeholder="请输入申请原因" v-decorator.trim="[ 'remark', validatorRules.remark ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请日期">
            <a-input placeholder="请输入申请日期" v-decorator.trim="[ 'time', validatorRules.time ]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addMaterialUsage,editMaterialUsage,checkMaterialAttribute,getAllPerson,getDepartment } from '@/api/api'
  import {mixinDevice} from '@/utils/mixin'
  export default {
    name: "MaterialUsageModal",
    mixins: [mixinDevice],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        dynamicOptions:[],
        selectedDepartment:'',
        personList: {
          options: [],
          value: ''
        },
        departmentList: {
          options: [],
          value: ''
        },
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
          barCode:{
            rules: [
              { required: true, message: '请输入物料编码!' }
            ]
          },
          number:{
            rules: [
              { required: true, message: '请输入周用量!' }
            ]
          },
          salesMan:{
            rules: [
              { required: true, message: '请选择申请人!' }
            ]
          },
          remark:{
            rules: [
              { required: true, message: '请选择申请原因!' }
            ]
          },
          time:{
            rules: [
              { required: true, message: '请选择申请日期!' }
            ]
          },
        }
      }
    },
    created () {
    },
    watch: {
      selectedDepartment(newVal) {
        this.dynamicOptions = [];
        for (let i = 0; i < this.personList.options.length; i++) {
          const option = this.personList.options[i]
          if (option.department === this.selectedDepartment) {
            this.dynamicOptions.push(option)
          }
        }
      }
    },
    methods: {
      add () {
        this.edit({});
      },
      initDepartment() {
        getDepartment().then((res)=>{
          if(res) {
            this.departmentList.options = res;
          }
        });
      },
      initPerson() {
        getAllPerson().then((res)=>{
          if(res) {
            this.personList.options = res;
            this.dynamicOptions = res;
          }
        });
      },
      edit (record) {
        this.initDepartment()
        this.initPerson()
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'barCode', 'number', 'salesMan', 'remark', 'time'))
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        console.log("handleOk() start")
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            console.log("no err")
            that.confirmLoading = true;
            let formData = Object.assign(this.model, values);
            let obj;
            if(!this.model.id){
              console.log("add")
              obj=addMaterialUsage(formData);
            }else{
              console.log("edit")
              obj=editMaterialUsage(formData);
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