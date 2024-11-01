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
      <template>
        <a-button @click="handleSelectMaterial"><a-icon type="setting"/>选择物料编码</a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料编码">
            <a-input :readOnly="true" placeholder="请选择物料编码" v-decorator.trim="[ 'barCode', validatorRules.barCode ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="周用量">
            <a-input placeholder="请输入周用量" v-decorator.trim="[ 'number', validatorRules.number ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请人">
            <a-select placeholder="选择申请人" v-decorator="[ 'salesMan', validatorRules.salesMan ]"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option v-for="(item,index) in personList.options" :key="index" :value="item.value">
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
      <select-material ref="selectMaterialForm" @ok="selectMaterialFormOk"></select-material>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addMaterialUsage,editMaterialUsage,checkMaterialAttribute,getAllPerson,getDepartment } from '@/api/api'
  import {mixinDevice} from '@/utils/mixin'
  import SelectMaterial from '@/views/bill/dialog/SelectMaterial'
  export default {
    name: "MaterialUsageModal",
    mixins: [mixinDevice],
    components: {
      SelectMaterial
    },
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
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
    methods: {
      add () {
        this.edit({});
      },
      handleSelectMaterial () {
        console.log("handleSelectMaterial")
        this.$refs.selectMaterialForm.add();
        this.$refs.selectMaterialForm.title = "选择物料编码";
        this.$refs.selectMaterialForm.disableSubmit = false;
      },
      selectMaterialFormOk(ids) {
        this.form.setFieldsValue({
          'barCode': ids
        })
      },
      initPerson() {
        getAllPerson().then((res)=>{
          if(res) {
            let withDepartment = [];
            for (let i=0; i<res.length; i++) {
              let info = res[i]
              info.text = info.text + "-" + info.department
              withDepartment.push(info)
            }
            this.personList.options = withDepartment;
          }
        });
      },
      edit (record) {
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