<template>

  <div class="container">
    <Form :model="newApi" :label-width="120" ref="newApi" :rules="ruleValidate" style="width: 1400px;margin: auto;">
      <FormItem label="API Route" prop="path">
        <Input v-model="newApi.path" placeholder="Example: /agents/me/group"></Input>
      </FormItem>

      <FormItem label="Operation ID" prop="name">
        <Input v-model="newApi.name" placeholder="Enter Operation ID ..."></Input>
      </FormItem>

      <FormItem label="API Description">
        <Input v-model="newApi.desc" placeholder="Enter API Descriptions  ..."></Input>
      </FormItem>

      <FormItem label="API Method" prop="method">
        <Select v-model="newApi.method" style="width:100px">
          <Option v-for="item in reqMethodList" :value="item.value" :key="item.value">{{ item.label }}</Option>
        </Select>
      </FormItem>

      <FormItem label="API Parameters">
        <Button type="info" @click="createNewParameter">Create Parameter +</Button>
        <Button type="dashed" @click="openNewSchemaModal">Create Schema +</Button>
        <div style="margin-top: 20px;">
          <div v-if="newApi.parameters.length > 0">
            <Row :gutter="16">
              <Col span="3">
                Param In *
              </Col>
              <Col span="3">
                Param Type *
              </Col>
              <Col span="3">
                Param Name *
              </Col>
              <Col span="3">
                Is Required ?
              </Col>
              <Col span="3">
                Schema Model
              </Col>
              <Col span="5">
                Param Descriptions
              </Col>
              <Col span="3">
                Param Operation
              </Col>
            </Row>
          </div>
          <Row :gutter="16" v-for="(param, index) in newApi.parameters" style="margin-bottom: 10px;">
            <Col span="3">
              <div>
                <Select v-model="param.in" style="width:100px">
                  <Option v-for="item in paramTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>
              </div>
            </Col>
            <Col span="3">
              <Select v-if="param.in == 'body'" v-model="param.type" style="width:100px">
                <Option v-for="item in bodyParamCategoryList" :value="item.value" :key="item.value">{{ item.label }}
                </Option>
              </Select>
              <Select v-if="param.in == 'query'" v-model="param.type" style="width:100px">
                <Option v-for="item in queryParamCategoryList" :value="item.value" :key="item.value">{{ item.label }}
                </Option>
              </Select>
            </Col>
            <Col span="3">
              <Input v-model="param.name" placeholder="" style="width: 150px;"></Input>
            </Col>
            <Col span="3">
              <i-switch v-model="param.required"></i-switch>
            </Col>
            <Col span="3">
              <AutoComplete
                v-if="param.type == 'object' || param.type == 'array'"
                :filter-method="schemaFilterMethod"
                :data="schemaList"
                v-model="param.schema"
                placeholder="input here for search"
                style="width:150px">
              </AutoComplete>
              <span v-else> - </span>
            </Col>
            <Col span="5">
              <Input v-model="param.desc" type="textarea" :rows="4" placeholder="Enter something..."/>
            </Col>
            <Col span="3">
              <Button type="error" @click="removeParam(index)">Delete</Button>
            </Col>
          </Row>
        </div>
      </FormItem>

      <FormItem label="API Tags">
        <Input v-model="tagInput" placeholder="Enter New Tag  ..." style="width: 220px;">
        <Button slot="append" icon="md-add" @click="addTag"></Button>
        </Input>
        <br>
        <p>
          <Tag closable @on-close="removeTag(tagIndex)" type="border" v-for="(tag, tagIndex) in newApi.tags">{{tag}}
          </Tag>
        </p>
      </FormItem>

      <FormItem label="API Response">
        <AutoComplete
          :filter-method="schemaFilterMethod"
          :data="schemaList"
          v-model="newApi.response"
          placeholder="input here for search"
          style="width:150px">
        </AutoComplete>
      </FormItem>

      <Button @click="saveNewApi" type="success" long>Submit</Button>
    </Form>

    <Modal
      v-model="newSchemaModal.show"
      :width="1000"
    >
      <p slot="header" style="color:green;text-align:center">
        <Icon type="ios-albums"/>
        <span>Create A New Schema Object</span>
      </p>
      <div style="text-align:left">
        <br>
        <div>
          <label>Name *</label>
          <Input v-model="newSchemaModal.name" placeholder="" style="width: 70%;margin-left: 38px;"></Input>
        </div>
        <br>
        <div>
          <label>Description</label>
          <Input v-model="newSchemaModal.desc" type="textarea" :rows="4" placeholder=""
                 style="width: 70%;margin-left: 18px;"/>
        </div>
        <br>
        <br>
        <div>
          <Button type="info" @click="createNewSchemaParameter">Create Parameter +</Button>
          <div v-if="newSchemaModal.parameters.length > 0" style="margin-top: 25px;">
            <Row :gutter="16">
              <Col span="1">
                #
              </Col>
              <Col span="5">
                Param Name *
              </Col>
              <Col span="3">
                Param Type *
              </Col>
              <Col span="5">
                Schema Model
              </Col>
              <Col span="5">
                Param Descriptions
              </Col>
              <Col span="4" offset="1">
                Param Operation
              </Col>
            </Row>
          </div>
          <div style="margin-top: 20px;" v-for="(param, index) in newSchemaModal.parameters">
            <Row :gutter="16" style="margin-bottom: 10px;">
              <Col span="1">
                {{index+1}}
              </Col>
              <Col span="5">
                <Input v-model="param.name" placeholder="" style="width: 150px;"></Input>
              </Col>
              <Col span="3">
                <Select v-model="param.type" style="width:100px">
                  <Option v-for="item in paramCategoryList" :value="item.value" :key="item.value">{{ item.label }}
                  </Option>
                </Select>
              </Col>
              <Col span="5">
                <AutoComplete
                  v-if="param.type == 'object' || param.type == 'array'"
                  :filter-method="schemaFilterMethod"
                  :data="schemaList"
                  v-model="param.schema"
                  placeholder="input here for search"
                  style="width:168px">
                </AutoComplete>
                <span v-else> - </span>
              </Col>
              <Col span="5">
                <Input v-model="param.desc" type="textarea" :rows="4" placeholder=""/>
              </Col>
              <Col span="4" offset="1">
                <Button type="error" @click="removeSchemaParam(index)">Delete</Button>
              </Col>
            </Row>

          </div>
        </div>
        <br>
      </div>
      <div slot="footer">
        <Button type="info" size="large" long @click="createSchemaObject">Submit</Button>
      </div>
    </Modal>

  </div>

</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: "SwaggerEditor",
    data() {
      return {
        newSchemaModal: {
          show: false,
          name: '',
          desc: '',
          parameters: [],
        },
        newApi: {
          _id: '',
          path: '',
          name: '',
          desc: '',
          method: 'get',
          parameters: [],
          tags: [],
          response: '',
        },
        tagInput: '',
        reqMethodList: [
          {
            label: 'get',
            value: 'get'
          },
          {
            label: 'post',
            value: 'post'
          },
          {
            label: 'put',
            value: 'put'
          },
          {
            label: 'delete',
            value: 'delete'
          },
        ],
        paramTypeList: [
          {
            label: 'query',
            value: 'query'
          },
          {
            label: 'body',
            value: 'body'
          }
        ],
        queryParamCategoryList: [
          {
            label: 'string',
            value: 'string'
          },
          {
            label: 'integer',
            value: 'integer'
          },
          {
            label: 'double',
            value: 'double'
          },
          {
            label: 'float',
            value: 'float'
          },
          {
            label: 'date-time',
            value: 'date-time'
          }
        ],
        bodyParamCategoryList: [
          {
            label: 'object',
            value: 'object'
          }
        ],
        paramCategoryList: [
          {
            label: 'string',
            value: 'string'
          },
          {
            label: 'integer',
            value: 'integer'
          },
          {
            label: 'double',
            value: 'double'
          },
          {
            label: 'float',
            value: 'float'
          },
          {
            label: 'date-time',
            value: 'date-time'
          },
          {
            label: 'object',
            value: 'object'
          },
          {
            label: 'array',
            value: 'array'
          }
        ],
        ruleValidate: {
          path: [
            {required: true, message: 'The route(path) cannot be empty', trigger: 'blur'}
          ],
          name: [
            {required: true, message: 'The name cannot be empty', trigger: 'blur'}
          ],
          method: [
            {required: true, message: 'The method cannot be empty', trigger: 'blur'}
          ],
          operationId: [
            {required: true, message: 'The operation ID cannot be empty', trigger: 'blur'}
          ],
        },
        swaggerData: null,
        schemaList: [],
        apiId: null,

      }
    },
    methods: {
      saveNewApi() {
        // 检查参数
        if (!this.newApi.path) {
          this.$Message.error('API Route Cannot Be Null!')
          return;
        }
        if (!this.newApi.name) {
          this.$Message.error('API Name Cannot Be Null!')
          return;
        }
        // 转换参数列表
        var parameters = [];
        if (this.newApi.parameters && this.newApi.parameters.length > 0) {
          for (var i = 0; i < this.newApi.parameters.length; i++) {
            var pp = {
              "description": this.newApi.parameters[i].desc,
              "in": this.newApi.parameters[i].in,
              "name": this.newApi.parameters[i].name,
              "required": this.newApi.parameters[i].required
            };
            if (this.newApi.parameters[i].type == 'object') {
              pp.schema = {
                "$ref": "#/definitions/" + this.newApi.parameters[i].schema
              };
            } else if (this.newApi.parameters[i].type == 'array') {
              continue;
            } else if (this.newApi.parameters[i].type == 'string') {
              pp.type = 'string';
            } else if (this.newApi.parameters[i].type == 'integer') {
              pp.type = 'integer';
            } else if (this.newApi.parameters[i].type == 'double') {
              pp.format = 'double';
              pp.type = 'number';
            } else if (this.newApi.parameters[i].type == 'float') {
              pp.format = 'float';
              pp.type = 'number';
            } else if (this.newApi.parameters[i].type == 'date-time') {
              pp.format = 'date-time';
              pp.type = 'string';
            }
            parameters.push(pp);
          }
        }
        // 转换response内容
        var responseModel = 'Nothing';
        if (this.newApi.response) {
          responseModel = this.newApi.response;
        } else {
          this.newApi.response = responseModel;
        }
        var content = {};
        content[this.newApi.path] = {};
        content[this.newApi.path][this.newApi.method] = {
          "description": this.newApi.desc,
          "operationId": this.newApi.name,
          "parameters": parameters,
          "responses": {
            "200": {
              "description": "successful operation",
              "schema": {
                "$ref": "#/definitions/" + responseModel
              }
            },
            "400": {
              "description": "Invalid agent or unit ID supplied"
            },
            "404": {
              "description": "Agent or unit not found"
            }
          },
          "summary": this.newApi.desc,
          "tags": this.newApi.tags
        };
        var param = {
          userId: this.getUser().id,
          content: JSON.stringify(content),
          path: this.newApi.path,
          name: this.newApi.name,
          desc: this.newApi.desc,
          method: this.newApi.method,
          parameters: this.newApi.parameters,
          tags: this.newApi.tags,
          response: this.newApi.response
        };
        // 如果API-ID不为空，表示是在编辑旧的APi
        if (this.apiId) {
          param._id = this.apiId;
        }
        console.log(param)
        this.$http.put(Global.SERVER_ROOT + '/swaggerTool/newApi/api', param).then((resp) => {
          if (resp.body.code == -1) {
            this.$Message.error(resp.body.msg)
          } else {
            // 跳转到API列表页面
            this.$router.push({
              path: '/swagger/manage'
            })
          }
        }, (error) => {

        });
      },
      getSwagger() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/swagger/resource').then((resp) => {
          this.swaggerData = resp.body;
          console.log(this.swaggerData);
          for (var key in this.swaggerData.definitions) {
            this.schemaList.push(key);
          }
          this.freashSchemaList();
        }, (error) => {

        });
      },
      createNewSchemaParameter() {
        this.newSchemaModal.parameters.push({
          name: '',
          schema: null,
          desc: '',
          type: 'string',
        });
      },
      createNewParameter() {
        this.newApi.parameters.push({
          in: 'query',
          name: '',
          required: false,
          schema: '',
          desc: '',
          type: 'string',
        });
      },
      removeParam(index) {
        var datas = [];
        for (var i = 0; i < this.newApi.parameters.length; i++) {
          if (index != i) {
            datas.push(this.newApi.parameters[i]);
          }
        }
        this.newApi.parameters = datas;
      },
      removeSchemaParam(index) {
        var datas = [];
        for (var i = 0; i < this.newSchemaModal.parameters.length; i++) {
          if (index != i) {
            datas.push(this.newSchemaModal.parameters[i]);
          }
        }
        this.newSchemaModal.parameters = datas;
      },
      addTag() {
        if (!this.tagInput)
          return;
        this.newApi.tags.push(this.tagInput);
        this.tagInput = '';
      },
      removeTag(index) {
        var datas = [];
        for (var i = 0; i < this.newApi.tags.length; i++) {
          if (index != i) {
            datas.push(this.newApi.tags[i]);
          }
        }
        this.newApi.tags = datas;
      },
      openNewSchemaModal() {
        this.newSchemaModal = {
          show: true,
          name: '',
          desc: '',
          parameters: [],
        }
      },
      createSchemaObject() {
        // 校验参数合法性
        if (!this.newSchemaModal.name) {
          this.$Message.error('Please Enter Schema Object Name!')
          return;
        }
        if (this.newSchemaModal.parameters && this.newSchemaModal.parameters.length > 0) {
          for (var i = 0; i < this.newSchemaModal.parameters.length; i++) {
            var param = this.newSchemaModal.parameters[i];
            if (!param.name) {
              this.$Message.error('Please Enter Param [' + (i + 1) + ']\'s Name!')
              return;
            }
          }
        }
        // 组装Schema数据结构
        var data = {};
        data[this.newSchemaModal.name] = {
          "type": "object",
          "description": this.newSchemaModal.desc,
        };
        if (this.newSchemaModal.parameters && this.newSchemaModal.parameters.length > 0) {
          var properties = {};
          for (var i = 0; i < this.newSchemaModal.parameters.length; i++) {
            var param = this.newSchemaModal.parameters[i];
            if (param.type == 'object') {
              properties[param.name] = {
                "$ref": "#/definitions/" + param.schema,
                "description": param.desc
              };
            } else if (param.type == 'array') {
              properties[param.name] = {
                "type": "array",
                "items": {
                  "$ref": "#/definitions/" + param.schema
                },
                "description": param.desc
              };
            } else if (param.type == 'string') {
              properties[param.name] = {
                "type": "string",
                "description": param.desc
              };
            } else if (param.type == 'integer') {
              properties[param.name] = {
                "type": "integer",
                "description": param.desc
              };
            } else if (param.type == 'double') {
              properties[param.name] = {
                "type": "number",
                "format": "double",
                "description": param.desc
              };
            } else if (param.type == 'float') {
              properties[param.name] = {
                "type": "number",
                "format": "float",
                "description": param.desc
              };
            } else if (param.type == 'date-time') {
              properties[param.name] = {
                "type": "string",
                "format": "date-time",
                "description": param.desc
              };
            }
          }
          data[this.newSchemaModal.name]['properties'] = properties;
        }
        // 后台存入数据库
        var userId = this.getUser().id;
        var queryParam = {
          userId: userId,
          content: JSON.stringify(data),
          name: this.newSchemaModal.name,
          desc: this.newSchemaModal.desc,
          parameters: this.newSchemaModal.parameters
        };
        this.$http.put(Global.SERVER_ROOT + '/swaggerTool/newApi/schema', queryParam).then((resp) => {
          if (resp.body.code == -1) {
            this.$Message.error(resp.body.msg)
          } else {
            // 刷新Schema列表
            this.freashSchemaList();
            this.$Message.success('Create Succeed!')
            // 关闭modal
            this.newSchemaModal.show = !this.newSchemaModal.show;
          }
        }, (error) => {
          this.$Message.error('Net Error!')
        });
      },
      getUser() {
        return JSON.parse(Global.COOKIE.get(Global.SESSION_KEY));
      },
      freashSchemaList() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/schema/list?userId=' + this.getUser().id).then((resp) => {
          console.log(resp.body);
          if (resp.body && resp.body.length > 0) {
            for (var i = 0; i < resp.body.length; i++) {
              var schema = JSON.parse(resp.body[i].content);
              for (var key in schema) {
                this.schemaList.push(key);
              }
            }
          }
        }, (error) => {

        });
      },
      handleSchemaSearch(value) {

      },
      schemaFilterMethod(value, option) {
        if (!value)
          return false;
        else
          return option.toUpperCase().indexOf(value.toUpperCase()) !== -1;
      },
      init() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/api/' + this.apiId).then((resp) => {
          console.log(resp.body);
          this.newApi = resp.body;
        }, (error) => {
        });
      },


    },
    created() {
      // 路由中获取API - ID
      this.apiId = this.$route.params.apiId;
      if (this.apiId) {
        // 初始化API数据
        this.init();
      }
      // 从sessionStorage中获取API信息，作为copy数据源
      if (sessionStorage.getItem('api-copy')) {
        var copyData = JSON.parse(sessionStorage.getItem('api-copy'));
        // 擦除ID等数据
        copyData._id = null;
        copyData.userId = null;
        console.log(copyData)
        this.newApi.path = copyData.path;
        this.newApi.parameters = copyData.parameters;
        this.newApi.name = copyData.name;
        this.newApi.method = copyData.method;
        this.newApi.desc = copyData.desc;
        this.newApi.tags = copyData.tags;
        this.newApi.response = copyData.response;
        sessionStorage.removeItem('api-copy');
      }
      if (sessionStorage.getItem('api-copy-from-swagger')) {
        var copyData = JSON.parse(sessionStorage.getItem('api-copy-from-swagger'));
        // 擦除ID等数据
        copyData._id = null;
        copyData.userId = null;
        console.log(copyData)
        this.newApi.path = copyData.path;
        // 修改parameters的结构
        var parameters = [];
        if (copyData.parameters && copyData.parameters.length > 0) {
          console.log(copyData.parameters)
          for (var i = 0; i < copyData.parameters.length; i++) {
            var param = copyData.parameters[i];
            if (param.schema && param.schema.$ref) {
              param.type = 'object';
              param.schema = param.schema.$ref.replace('#/definitions/', '');
            }
            parameters.push(param);
          }
        }
        this.newApi.parameters = parameters;
        this.newApi.name = copyData.name;
        this.newApi.method = copyData.method;
        this.newApi.desc = copyData.desc;
        this.newApi.tags = copyData.tags;
        this.newApi.response = copyData.response.replace('#/definitions/', '');
        sessionStorage.removeItem('api-copy-from-swagger');
      }
      this.getSwagger();
    },
    mounted() {

    },
    watch: {
      '$route.path': function (newVal, oldVal) {
        if (newVal == '/swagger/editor') {
          history.go(0);
        }
      }
    }

  }
</script>

<style scoped>

</style>
