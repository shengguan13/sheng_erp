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
      :ok-button-props="{ style: { display: 'none' } }"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:20px;height: 95%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
        <a-row :gutter="10">
          <a-col :md="12" :sm="24">
            <a-card :bordered="false">
              <!-- 按钮操作区域 -->
              <a-row style="margin-left: 14px">
                <a-button @click="refresh" type="default" icon="reload">刷新</a-button>
                <a-button v-if="btnEnableList.indexOf(1)>-1" @click="handleAdd" type="default" icon="form">新增</a-button>
                <a-button v-if="btnEnableList.indexOf(1)>-1" title="删除多条数据" @click="batchDel" type="default">批量删除</a-button>
              </a-row>
              <div style="background: #fff;padding-left:16px;height: 100%; margin-top: 5px">
                <a-alert type="info" :showIcon="true">
                  <div slot="message">
                    当前选择：<span v-if="this.currSelected.title">{{ getCurrSelectedTitle() }}</span>
                    <a v-if="this.currSelected.title" style="margin-left: 10px" @click="onClearSelected">取消选择</a>
                  </div>
                </a-alert>
                <!-- 树-->
                <a-col :md="10" :sm="24">
                  <template>
                    <a-dropdown :trigger="[this.dropTrigger]" @visibleChange="dropStatus">
                     <span style="user-select: none">
                      <a-tree
                        checkable
                        multiple
                        @select="onSelect"
                        @check="onCheck"
                        @rightClick="rightHandle"
                        :selectedKeys="selectedKeys"
                        :checkedKeys="checkedKeys"
                        :treeData="categoryTree"
                        :checkStrictly="checkStrictly"
                        :expandedKeys="iExpandedKeys"
                        :autoExpandParent="true"
                        @expand="onExpand"/>
                      </span>
                    </a-dropdown>
                  </template>
                </a-col>
              </div>
            </a-card>
            <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
            <div class="drawer-bootom-button">
              <a-dropdown :trigger="['click']" placement="topCenter">
                <a-menu slot="overlay">
                  <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
                  <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
                  <a-menu-item key="5" @click="expandAll">展开所有</a-menu-item>
                  <a-menu-item key="6" @click="closeAll">合并所有</a-menu-item>
                </a-menu>
                <a-button>
                  树操作 <a-icon type="up" />
                </a-button>
              </a-dropdown>
            </div>
            <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
          </a-col>
          <a-col :md="12" :sm="24">
            <template>
              <a-button v-if="currSelected.id && currSelected.id!=record.id" @click="handleSelectMaterial"><a-icon type="setting"/>选择物料编码</a-button>
            </template>
            <template>
              <a-button v-if="currSelected.id" @click="handleSelectProductSupplier"><a-icon type="setting"/>选择客商档案</a-button>
            </template>
            <a-card :bordered="false" v-if="selectedKeys.length>0">
              <a-form :form="form">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料编码">
                  <a-input :readOnly="true" placeholder="请选择物料编码" v-decorator="['barCode', validatorRules.barCode]"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级物料编码">
                  <a-input :readOnly="currSelected.id==record.id" placeholder="请输入上级物料编码" v-decorator="['upper']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
                  <a-input :readOnly="true" placeholder="名称" v-decorator="['name']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="零件号">
                  <a-input :readOnly="true" placeholder="零件号" v-decorator="['model']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客商档案">
                  <a-input :readOnly="true" placeholder="客商档案" v-decorator="['department']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户/供应商">
                  <a-input :readOnly="true" placeholder="客户/供应商" v-decorator="['supplierName']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客/供型号">
                  <a-input :readOnly="true" placeholder="客/供型号" v-decorator="['supplierModel']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标包">
                  <a-input :readOnly="true" placeholder="标包" v-decorator="['pack']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料来源">
                  <a-input :readOnly="true" placeholder="物料来源" v-decorator="['otherField7']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="产能">
                  <a-input :readOnly="true" placeholder="产能（时/件）" v-decorator="['otherField8']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规格">
                  <a-input :readOnly="true" placeholder="规格" v-decorator="['otherField5']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="颜色">
                  <a-input :readOnly="true" placeholder="颜色" v-decorator="['color']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="颜色代码">
                  <a-input :readOnly="true" placeholder="颜色代码" v-decorator="['colorCode']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="材料">
                  <a-input :readOnly="true" placeholder="材料标准" v-decorator="['material']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="材料标准">
                  <a-input :readOnly="true" placeholder="材料标准" v-decorator="['otherField6']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类别">
                  <a-input :readOnly="true" placeholder="类别" v-decorator="['category']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目">
                  <a-input :readOnly="true" placeholder="请输入项目" v-decorator="['project']"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单位">
                  <a-input :readOnly="true" v-decorator="[ 'unit', validatorRules.unit ]"/>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用量">
                  <a-input placeholder="请输入用量" v-decorator="[ 'processUsage' ]" />
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
                  <a-select placeholder="请选择状态" v-decorator="[ 'source', validatorRules.source ]" >
                    <a-select-option value="项目">项目</a-select-option>
                    <a-select-option value="量产">量产</a-select-option>
                    <a-select-option value="新增">新增</a-select-option>
                    <a-select-option value="设变">设变</a-select-option>
                    <a-select-option value="临时">临时</a-select-option>
                    <a-select-option value="沿用">沿用</a-select-option>
                    <a-select-option value="屏蔽">屏蔽</a-select-option>
                    <a-select-option value="指定">指定</a-select-option>
                    <a-select-option value="取消">取消</a-select-option>
                    <a-select-option value="下线">下线</a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
                  <a-input placeholder="请输入备注" v-decorator="[ 'remark' ]" />
                </a-form-item>
              </a-form>
              <div class="anty-form-btn">
                <a-button v-if="btnEnableList.indexOf(1)>-1 && currSelected.id!=record.id" @click="emptyCurrForm" type="default" htmlType="button" icon="sync">重置</a-button>
                <a-button v-if="btnEnableList.indexOf(1)>-1" @click="submitCurrForm" type="primary" htmlType="button" icon="form">保存</a-button>
                <a-button v-if="btnEnableList.indexOf(1)>-1" @click="submitCurrFormAndNotify" type="primary" htmlType="button" icon="form">保存并通知</a-button>
              </div>
            </a-card>
            <a-card v-else >
              <a-empty>
                <span slot="description"> 请先选择一个零件! </span>
              </a-empty>
            </a-card>
          </a-col>
        </a-row>
    </a-modal>
    <select-material ref="selectMaterialForm" @ok="selectMaterialFormOk"></select-material>
    <select-product-supplier ref="selectProductSupplierForm" @ok="selectProductSupplierFormOk"></select-product-supplier>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import { mixinDevice } from '@/utils/mixin'
  import { getAction, httpAction, deleteAction, postAction } from '@/api/manage'
  import { getMaterialByBarCode } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getMpListShort } from "@/utils/util"
  import SelectMaterial from '@/views/bill/dialog/SelectMaterial'
  import SelectProductSupplier from '@/views/bill/dialog/SelectProductSupplier'
  import Vue from 'vue'
  export default {
    name: "MaterialBomViewModal",
    components: {
      SelectMaterial,
      SelectProductSupplier
    },
    mixins: [mixinDevice, JeecgListMixin],
    data () {
      return {
        title:"查看",
        isReadOnly: false,
        iExpandedKeys: [],
        loading: false,
        currFlowId: '',
        currFlowName: '',
        disable: true,
        treeData: [],
        visible: false,
        categoryTree: [],
        rightClickSelectedKey: '',
        rightClickSelectedOrgCode: '',
        hiding: true,
        model: {},
        dropTrigger: '',
        category: {},
        disableSubmit: false,
        checkedKeys: [],
        selectedKeys: [],
        autoIncr: 1,
        currSelected: {},
        allTreeKeys:[],
        checkStrictly: true,
        form: this.$form.createForm(this),
        labelCol: {
          xs: {span: 24},
          sm: {span: 5}
        },
        wrapperCol: {
          xs: {span: 24},
          sm: {span: 16}
        },
        graphDatasource: {
          nodes: [],
          edges: []
        },
        validatorRules:{
          upper: {rules: [{required: true, message: '请输入上级物料编码!'}]},
          barCode: {rules: [{required: true, message: '请输入物料编码!'}]},
          processUsage: {
            rules: [
              { required: true, message: '请输入用量!' },
              { pattern: /^(0|([1-9][0-9]*))(\.[\d]+)?$/, message: '请输入非负的整数或者小数!' }
            ]
          },
          source: {rules: [{required: true, message: '请选择状态!'}]}
        },
        record: {},
        url: {
          getMaterialBomTree: '/materialBom/getMaterialBomTree',
          addBomChild: '/materialBom/addBomChild',
          list: '/materialBom/selectMaterialBomWithUpper',
          deleteBatch: '/materialBom/deleteBatch',
          edit: '/materialBom/update'
        },
        orgCategoryDisabled:false,
      }
    },
    created() {
      this.currFlowId = this.$route.params.id
      this.currFlowName = this.$route.params.name
    },
    methods: {
      edit (record) {
        this.form.resetFields();
        this.record = record
        console.log("record: " + JSON.stringify(record))
        this.loadTree();
        this.visible = true;
        this.$nextTick(() => {
        });
      },
      handleSelectMaterial () {
        console.log("handleSelectMaterial")
        this.$refs.selectMaterialForm.add();
        this.$refs.selectMaterialForm.title = "选择物料编码";
        this.$refs.selectMaterialForm.disableSubmit = false;
      },
      selectProductSupplierFormOk(ids) {
        this.form.setFieldsValue({
          'department': ids
        })
      },
      handleSelectProductSupplier () {
        console.log("handleSelectProductSupplier")
        this.$refs.selectProductSupplierForm.add();
        this.$refs.selectProductSupplierForm.title = "选择客商档案";
        this.$refs.selectProductSupplierForm.disableSubmit = false;
      },
      selectMaterialFormOk(ids) {
        let param = {
          barCode: ids,
          mpList: getMpListShort(Vue.ls.get('materialPropertyList'))
        }
        getMaterialByBarCode(param).then((res) => {
          if (res && res.code === 200) {
            let mList = res.data
            let mInfo = mList[0]
            console.log("mInfo: " + JSON.stringify(mInfo))
            this.form.setFieldsValue({
              'barCode': ids,
              'name': mInfo.name,
              'model': mInfo.model
            })
          }
        })
      },
      close () {
        this.$emit('close');
        this.onClearSelected()
        this.visible = false;
      },
      loadData() {
        this.refresh();
      },
      loadTree() {
        let that = this
        that.treeData = []
        that.categoryTree = []
        let params = {};
        params.parent = this.record.parent;
        params.barCode = this.record.barCode;
        params.project = this.record.project;
        getAction(that.url.getMaterialBomTree, params).then((res) => {
          if (res.code === 200) {
            //类别全选后，再添加类别，选中数量增多
            this.allTreeKeys = [];
            for (let i = 0; i < res.data.length; i++) {
              let temp = res.data[i]
              that.categoryTree.push(temp)
              console.log("pushing " + JSON.stringify(temp))
              that.setThisExpandedKeys(temp)
              that.getAllKeys(temp);
            }
            this.loading = false
          }
        })
      },
      setThisExpandedKeys(node) {
        if (node.children && node.children.length > 0) {
          this.iExpandedKeys.push(node.key)
          for (let a = 0; a < node.children.length; a++) {
            this.setThisExpandedKeys(node.children[a])
          }
        }
      },
      refresh() {
        this.loading = true
        this.loadTree()
      },
      // 右键操作方法
      rightHandle(node) {
        this.dropTrigger = 'contextmenu'
        console.log(node.node.eventKey)
        this.rightClickSelectedKey = node.node.eventKey
        this.rightClickSelectedOrgCode = node.node.dataRef.orgCode
      },
      onExpand(expandedKeys) {
        console.log('onExpand', expandedKeys)
        this.iExpandedKeys = expandedKeys
      },
      backFlowList() {
        this.$router.back(-1)
      },
      // 右键点击下拉框改变事件
      dropStatus(visible) {
        if (visible == false) {
          this.dropTrigger = ''
        }
      },
      batchDel: function () {
        console.log(this.checkedKeys)
        if (this.checkedKeys.length <= 0) {
          this.$message.warning('请选择一条记录！')
        } else {
          let ids = ''
          for (let a = 0; a < this.checkedKeys.length; a++) {
            ids += this.checkedKeys[a] + ','
          }
          let that = this
          this.$confirm({
            title: '确认删除',
            content: '确定要删除所选中的 ' + this.checkedKeys.length + ' 条数据吗?',
            onOk: function () {
              deleteAction(that.url.deleteBatch, {ids: ids}).then((res) => {
                if (res.code == 200) {
                  that.$message.success(res.data.message)
                  that.loadTree()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.data.message)
                }
              })
            }
          })
        }
      },
      // 右键店家下拉关闭下拉框
      closeDrop() {
        this.dropTrigger = ''
      },
      addRootNode() {
        this.$refs.nodeModal.add(this.currFlowId, '')
      },
      nodeModalOk() {
        this.loadTree()
      },
      nodeModalClose() {
      },
      hide() {
        this.visible = false
      },
      onCheck(checkedKeys, info) {
        console.log('onCheck', checkedKeys, info)
        this.hiding = false
        //this.checkedKeys = checkedKeys.checked
        // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
        if(this.checkStrictly){
          this.checkedKeys = checkedKeys.checked;
        }else{
          this.checkedKeys = checkedKeys
        }
        // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
      },
      onSelect(selectedKeys, e) {
        console.log('selected', selectedKeys, e)
        this.hiding = false
        let record = e.node.dataRef
        console.log('record str: ' + JSON.stringify(record))
        let params = {};
        params.parent = record.parent
        params.project = record.project
        params.upper = record.upper
        params.materialParam = record.barCode
        getAction(this.url.list, params).then((res) => {
          if (res.code === 200) {
            record.parent = res.data[0].parent;
            record.upper = res.data[0].upper;
            record.partNo = res.data[0].partNo;
            record.project = res.data[0].project;
            record.processUsage = res.data[0].processUsage;
            record.source = res.data[0].source;
            record.barCode = res.data[0].barCode;
            record.department = res.data[0].department;
            record.name = res.data[0].name;
            record.model = res.data[0].model;
            record.color = res.data[0].color;
            record.unit = res.data[0].unit;
            record.colorCode = res.data[0].colorCode;
            record.supplierModel = res.data[0].supplierModel;
            record.supplierName = res.data[0].supplierName;
            record.pack = res.data[0].pack;
            record.otherField5 = res.data[0].otherField5;
            record.otherField6 = res.data[0].otherField6;
            record.otherField7 = res.data[0].otherField7;
            record.otherField8 = res.data[0].otherField8;
            record.category = res.data[0].category;
            record.material = res.data[0].material;
            console.log('onSelect-record', JSON.stringify(record))
            this.currSelected = Object.assign({}, record)
            this.model = this.currSelected
            this.selectedKeys = [record.key]
            this.setValuesToForm(record)
          }
        })
      },
      // 触发onSelect事件时,为类别树右侧的form表单赋值
      setValuesToForm(record) {
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(record, 'barCode', 'name', 'model', 'color', 'colorCode', 'supplierModel',
            'supplierName', 'otherField5', 'otherField6', 'otherField7', 'otherField8', 'material', 'category',
            'upper', 'unit', 'source', 'remark', 'project', 'processUsage', 'department', 'pack'))
        })
      },
      getCurrSelectedTitle() {
        return !this.currSelected.title ? '' : this.currSelected.title
      },
      onClearSelected() {
        this.hiding = true
        this.checkedKeys = []
        this.currSelected = {}
        this.form.resetFields()
        this.selectedKeys = []
      },
      handleNodeTypeChange(val) {
        this.currSelected.nodeType = val
      },
      notifyTriggerTypeChange(value) {
        this.currSelected.notifyTriggerType = value
      },
      receiptTriggerTypeChange(value) {
        this.currSelected.receiptTriggerType = value
      },
      emptyCurrForm() {
        this.form.resetFields()
      },
      submitCurrForm() {
        this.form.validateFields((err, values) => {
          if (!err) {
            if (!this.currSelected.id) {
              this.$message.warning('请点击选择要修改BOM!')
              return
            }
            console.log('Received values of form: ', values)
            let formData = Object.assign(this.currSelected, values)
            console.log('Form to submit: ', formData)
            httpAction(this.url.edit, formData, 'put').then((res) => {
              if (res.code == 200) {
                this.$message.success('保存成功!')
                this.loadTree()
              } else {
                this.$message.warning('更新失败，检查BOM上级是否已有指定的物料！')
              }
            })
          }
        })
      },
      submitCurrFormAndNotify() {
        this.form.validateFields((err, values) => {
          if (!err) {
            if (!this.currSelected.id) {
              this.$message.warning('请点击选择要修改BOM!')
              return
            }
            let oldStr = "旧：物料编码：" + this.currSelected.barCode + "；零件号：" + this.currSelected.model + "；上级物料："
              + this.currSelected.upper + "；用量：" + this.currSelected.processUsage + "；状态：" + this.currSelected.source + "<br />";
            console.log('Received values of form: ', values)
            let formData = Object.assign(this.currSelected, values)
            let newStr = "新：物料编码：" + this.currSelected.barCode + "；零件号：" + this.currSelected.model + "；上级物料："
              + this.currSelected.upper + "；用量：" + this.currSelected.processUsage + "；状态：" + this.currSelected.source + "<br />";
            console.log('Form to submit: ', formData)
            httpAction(this.url.edit, formData, 'put').then((res) => {
              if (res.code == 200) {
                this.$message.success('保存成功!')
                this.loadTree()
                let msgParam = {
                  'msgTitle': this.currSelected.project + ' BOM更改提醒',
                  'msgContent': oldStr + newStr,
                  'type': 'BOM更改提醒',
                  'userIdList': "63,245,178,180,181,193,195,201,202,205,207,215,216,217,218,219,226,184,227"
                }
                postAction("/msg/add",msgParam).then(res=>{
                  if(res && res.code === 200) {}
                })
              } else {
                this.$message.warning('更新失败，检查BOM上级是否已有指定的物料！')
              }
            })
          }
        })
      },
      handleAdd() {
        let params = {};
        if (this.selectedKeys.length != 1) {
          this.$message.warning("必须选中一个零件，作为新增零件的上级！")
        } else {
          params.parent = this.record.parent;
          params.upper = this.currSelected.barCode;
          params.project = this.record.project;
          getAction(this.url.addBomChild, params).then((res) => {
            if (res.code == 200) {
              this.$message.success('新增成功!')
              this.loadTree()
            } else {
              this.$message.warning(res.data)
            }
          })
        }
      },
      nodeSettingFormSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
          }
        })
      },
      openSelect() {
        this.$refs.sysDirectiveModal.show()
      },
      selectDirectiveOk(record) {
        console.log('选中指令数据', record)
        this.nodeSettingForm.setFieldsValue({directiveCode: record.directiveCode})
        this.currSelected.sysCode = record.sysCode
      },
      getFlowGraphData(node) {
        this.graphDatasource.nodes.push({
          id: node.id,
          text: node.flowNodeName
        })
        if (node.children.length > 0) {
          for (let a = 0; a < node.children.length; a++) {
            let temp = node.children[a]
            this.graphDatasource.edges.push({
              source: node.id,
              target: temp.id
            })
            this.getFlowGraphData(temp)
          }
        }
      },
      // <!---- author:os_chengtgen -- date:20190827 --  for:切换父子勾选模式 =======------>
      expandAll () {
        this.iExpandedKeys = this.allTreeKeys
      },
      closeAll () {
        this.iExpandedKeys = []
      },
      checkALL () {
        this.checkStrictly = false
        this.checkedKeys = this.allTreeKeys
      },
      cancelCheckALL () {
        //this.checkedKeys = this.defaultCheckedKeys
        this.checkedKeys = []
      },
      getAllKeys(node) {
        // console.log('node',node);
        this.allTreeKeys.push(node.key)
        if (node.children && node.children.length > 0) {
          for (let a = 0; a < node.children.length; a++) {
            this.getAllKeys(node.children[a])
          }
        }
      },
      handleOk () {
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>
<style scoped>

</style>