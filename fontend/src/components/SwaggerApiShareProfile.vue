<template>
  <div class="container">
    <div v-if="api">
      <h1>API Profile</h1>
      <br>
      <Card :bordered="false" style="font-size: 22px;">
        <div>
          <br>
          <Row class="api-header">
            <Col :xs="4" :sm="4" :md="6" :lg="1">
              <Tag color="primary" v-if="api.method == 'get'">GET</Tag>
              <Tag color="success" v-if="api.method == 'post'">POST</Tag>
              <Tag color="warning" v-if="api.method == 'put'">PUT</Tag>
              <Tag color="error" v-if="api.method == 'delete'">DELETE</Tag>
            </Col>
            <Col :xs="18" :sm="16" :md="12" :lg="21">
              <h2 style="">
                {{api.path}}
              </h2>
            </Col>
            <Col :xs="2" :sm="4" :md="6" :lg="2">
              <label>Author&nbsp;&nbsp;&nbsp;&nbsp;</label>
              <Avatar style="background-color: #87d068;position: relative;bottom: 3px;" icon="ios-person" />
            </Col>
          </Row>
        </div>
        <br>
        <div>
          <Divider orientation="left" style="font-size: 20px;">Basic Description</Divider>
          <div style="">
            <Table :columns="columns1" :data="data1" border :show-header="false" size="large"></Table>
          </div>
          <br>
          <Divider orientation="left" style="font-size: 20px;">Request Parameters</Divider>
          <Table :columns="columns2" :data="data2" border size="large"></Table>
          <br>
          <Divider orientation="left" style="font-size: 20px;">Response Data</Divider>
          <Alert type="success">
            <a style="" @click="showSwaggerSchema(api.response)">{{api.response}}</a>
          </Alert>
          <br>
        </div>
      </Card>

    </div>

    <Drawer title="Swagger schema Json Data" placement="left" :closable="false" v-model="schemaDisplay.show" width="60">
      <div v-if="schemaDisplay.history.length > 0" style="width: fit-content;" @click="goBackDisplay">
        <Icon type="md-arrow-back" style="font-size: 24px;cursor: pointer;color: darkred;"/>
      </div>
      <pre v-html="schemaDisplay.content" @click="handleApiContentClick($event)"></pre>
    </Drawer>

  </div>
</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: "SwaggerApiShareProfile",
    data() {
        return {
          swaggerData: {},
          schemaDisplay: {
            show: false,
            content: '',
            history: [],
          },
          apiId: null,
          api: null,
          defaultCollapseArr: ['1', '2', '3'],
          /* basic description table*/
          columns1: [
            {
              title: 'KEY',
              key: 'key',
              width: 175,
            },
            {
              title: 'VALUE',
              render: (h, params) => {
                return h('div', [
                  h('strong', params.row.value)
                ]);
              }
            }
          ],
          data1: [],
          /* request parameters table*/
          columns2: [
            {
              title: 'Parameter In',
              key: 'in',
              width: 175,
            },
            {
              title: 'Data Type',
              render: (h, params) => {
                if(params.row.type && !params.row.schema) {
                  return h('div', {

                  }, params.row.type);
                } else if(params.row.schema) {
                  return h('a', {
                    on: {
                      click: () => {
                        this.showSwaggerSchema(params.row.schema)
                      }
                    }
                  }, params.row.schema);
                }
              },
              width: 175,
            },
            {
              title: 'Parameter Name',
              key: 'name',
              width: 250,
            },
            {
              title: 'Is Required',
              key: 'required',
              width: 120,
            },
            {
              title: 'Parameter description',
              key: 'desc',
            }
          ],
          data2: []
        }
    },
    methods: {
      getApi() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/apiManage/api/' + this.apiId).then((resp) => {
          console.log(resp.body)
          this.api = resp.body;
          // 加载各种表格
          // api基本信息
          this.data1 = [
            {
              key: "API Method",
              value: this.api.name
            },
            {
              key: "Tags",
              value: this.api.tags
            },
            {
              key: "Description",
              value: this.api.desc
            },
            {
              key: "Create Time",
              value: new Date(this.api.createTime).Format("yyyy-MM-dd hh:mm:ss")
            }
          ];
          // api入参
          this.data2 = this.api.parameters;
          // api响应值

        }, (error) => {
          this.$Message.error('分享链接已失效!');
        });
      },
      showSwaggerSchema(name) {
        var data = this.findSchema(name);
        var _data = {};
        _data[name] = data.val;
        var content = JSON.stringify(_data, null, 4);
        var parameters = _data[name].properties;
        if (parameters) {
          for (var key in parameters) {
            var param = parameters[key];
            if (param.$ref) {
              content = content.replace(param.$ref, '#/definitions/<a class="schema_object" data-id="' + param.$ref.replace('#/definitions/', '') + '">' + param.$ref.replace('#/definitions/', '') + '</a>');
            } else if (param.type == 'array') {
              if (param.items.$ref) {
                var key2 = param.items.$ref.replace('#/definitions/', '');
                content = content.replace(param.items.$ref, '#/definitions/<a class="schema_object" data-id="' + key2 + '">' + key2 + '</a>');
              }
            }
          }
        }
        this.schemaDisplay.history = [];
        this.schemaDisplay.content = content;
        this.schemaDisplay.show = true;
      },
      handleApiContentClick(event) {
        if (event.target.className == 'schema_object') {
          this.openSchemaModal(event.target.dataset.id);
        }
      },
      openSchemaModal(name) {
        var schema = this.findSchema(name);
        if (schema) {
          var data = {};
          data[schema.key] = schema.val;
          var content = JSON.stringify(data, null, 4);
          var properties = schema.val.properties;
          if (properties) {
            for (var key in properties) {
              var param = properties[key];
              if (param.$ref) {
                var key1 = param.$ref.replace('#/definitions/', '');
                content = content.replace(param.$ref, '#/definitions/<a class="schema_object" data-id="' + key1 + '">' + key1 + '</a>');
              } else if (param.type == 'array') {
                if (param.items.$ref) {
                  var key2 = param.items.$ref.replace('#/definitions/', '');
                  content = content.replace(param.items.$ref, '#/definitions/<a class="schema_object" data-id="' + key2 + '">' + key2 + '</a>');
                }
              }
            }
          }
          this.schemaDisplay.history.push(this.schemaDisplay.content);
          this.schemaDisplay.content = content;
          this.schemaDisplay.show = true;
        }
      },
      goBackDisplay() {
        this.schemaDisplay.content = this.schemaDisplay.history[this.schemaDisplay.history.length - 1];
        var arr = [];
        for (var i = 0; i < this.schemaDisplay.history.length - 1; i++) {
          arr.push(this.schemaDisplay.history[i])
        }
        this.schemaDisplay.history = arr;
        this.schemaDisplay.show = true;
      },
      findSchema(name) {
        var schema = {};
        // 现在线上swagger中找
        if (this.swaggerData.definitions[name]) {
          schema.val = this.swaggerData.definitions[name];
          schema.key = name;
          schema.from = 'swagger';
          return schema;
        }
        // 在自己数据库定义的schema中找
        // if (this.selfSchema[name]) {
        //   schema.val = this.selfSchema[name];
        //   schema.key = name;
        //   schema.from = 'database';
        //   return schema;
        // }
        this.$Message.error('can not find this model ! ')
        return null;
      },
      getSwagger() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/swagger/resource').then((resp) => {
          this.swaggerData = resp.body;
        }, (error) => {

        });
      },
    },
    created() {
      this.apiId = this.$route.params.apiId;
      this.getApi()
      this.getSwagger();
    }
  }
</script>

<style scoped>
  .container {
    padding: 5em 5em;
    background-color: #e8eaec;
  }

  .api-header {
  }

</style>
