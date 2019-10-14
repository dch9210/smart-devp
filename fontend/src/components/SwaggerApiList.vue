<template>
  <div>
    <div style="padding: 40px 0px;">
      <Row>
        <Col span="12" offset="0">
          <Input v-model="searchKey" placeholder="search api ..." style="width: 100%"/>
        </Col>
        <Col span="3" offset="1">
          <Button @click="getApiList" type="info" long>Search</Button>
        </Col>
      </Row>
    </div>
    <Table border :columns="columns" :data="showList"></Table>
    <br>
    <br>
    <br>
    <Page style="float: right;margin-right: 45px;" :total="total" :current="page" @on-change="changePage"/>
  </div>
</template>

<script>
  export default {
    name: "SwaggerApiList",
    props: ['source'],
    data() {
      return {
        searchKey: '',
        page: 1,
        size: 10,
        total: 0,
        columns: [
          {
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: 'API Method',
            key: 'method',
            width: 150,
            render: (h, params) => {
              var color = 'rgb(15,106,180)';
              if ('post' == params.row.method) {
                color = 'rgb(16,165,74)'
              }
              if ('put' == params.row.method) {
                color = 'rgb(255,173,51)'
              }
              if ('delete' == params.row.method) {
                color = 'rgb(237,64,20)'
              }
              return h('div', [
                h('strong', {
                  style: {
                    'background-color': color,
                    'color': '#fff',
                    'padding': '10px',
                  }
                }, params.row.method.toUpperCase())
              ]);
            }
          },
          {
            title: 'Operation ID',
            key: 'name',
            render: (h, params) => {
              return h('div', [
                h('span', {
                  props: {
                    type: 'person'
                  },
                  style: {
                    'font-size': '16px'
                  }
                }, params.row.name)
              ]);
            }
          },
          {
            title: 'API Route',
            key: 'path'
          },
          {
            title: 'Description',
            key: 'desc'
          },
          {
            title: 'Response Data',
            render: (h, params) => {
              return h('div', [
                h('span', params.row.response)
              ]);
            }
          },
          {
            title: 'Tags',
            render: (h, params) => {
              return h('div', [
                h('span', params.row.tags)
              ]);
            }
          },
          {
            title: 'Action',
            key: 'action',
            width: 350,
            align: 'center',
            render: (h, params) => {
              return h('div', [
                h('Button', {
                  props: {
                    type: 'success',
                    size: 'small',
                    ghost: ''
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.copyAPI(params.index)
                    }
                  }
                }, 'Copy'),
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.show(params.index)
                    }
                  }
                }, 'API'),
                // h('Button', {
                //   props: {
                //     type: 'default',
                //     size: 'small'
                //   },
                //   style: {
                //     marginRight: '5px'
                //   },
                //   on: {
                //     click: () => {
                //       this.goApiTestPage(params.index)
                //     }
                //   }
                // }, 'Test'),
              ]);
            }
          }
        ],
        apiList: [],
        showList: [],
      }
    },
    methods: {
      copyAPI(index) {
        var api = this.showList[index];
        sessionStorage.setItem('api-copy-from-swagger', JSON.stringify(api))
        this.$router.push({
          path: '/swagger/editor'
        })
      },
      show(index) {
        var api = this.showList[index];
        var content = {};
        content[api.path] = {};
        content[api.path][api.method] = {
          "description": api.desc,
          "operationId": api.name,
          "parameters": api.parameters,
          "responses": {
            "200": {
              "description": "successful operation",
              "schema": {
                "$ref": api.response
              }
            },
            "400": {
              "description": "Invalid agent or unit ID supplied"
            },
            "404": {
              "description": "Agent or unit not found"
            }
          },
          "summary": api.desc,
          "tags": api.tags
        };
        var data = {
          content: JSON.stringify(content),
          parameters: api.parameters,
          response: api.response.replace('#/definitions/', '')
        };
        this.$emit('show', index, data);
      },
      goApiTestPage(index) {
        var api = this.showList[index];

      },
      converSwaggerToApiList() {
        for (var path in this.source.paths) {
          var pathList = this.source.paths[path];
          for (var method in pathList) {
            var api = pathList[method];
            var resp = (api.responses[200] && api.responses[200].schema && api.responses[200].schema.$ref) ? api.responses[200].schema.$ref : '-';
            this.apiList.push({
              method: method,
              name: api.operationId,
              path: path,
              desc: api.description,
              response: resp,
              tags: api.tags,
              parameters: api.parameters
            })
          }
        }
        this.total = this.apiList.length;
        // 初次刷新列表
        this.getApiList();
      },
      getApiList() {
        this.showList = [];
        var reg = new RegExp(this.searchKey, 'i');
        var skip = (this.page - 1) * this.size;
        if (skip > this.apiList.length) {
          return;
        }
        if (this.searchKey) {
          for (var i = 0; i < this.apiList.length; i++) {
            var api = this.apiList[i];
            if (api.name) {
              if (new String(api.name).match(reg) || new String(api.path).match(reg) || new String(api.desc).match(reg)) {
                this.showList.push(api);
              }
            }
          }
          return;
        }
        for (var i = skip; i < this.page * this.size; i++) {
          var api = this.apiList[i];
          this.showList.push(api);
        }
      },
      changePage(page) {
        this.page = page;
        this.getApiList();
      },
    },
    created() {
      console.log(this.source);
      this.converSwaggerToApiList()
    },
  }
</script>

<style scoped>

</style>
