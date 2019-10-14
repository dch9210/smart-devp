<template>
  <div class="layout">
    <Layout>
      <Sider ref="side1" hide-trigger collapsible :collapsed-width="80" v-model="isCollapsed">
        <Menu active-name="1-2" theme="dark" width="auto" :class="menuitemClasses" @on-select="selectMenueItem">
          <Submenu name="1">
            <template slot="title">
              <Icon type="ios-analytics"/>
              Swagger
            </template>
            <MenuGroup title="Some function about API">
              <MenuItem name="swagger_editor">New API +</MenuItem>
              <MenuItem name="schema_manage">Schema Manage</MenuItem>
              <MenuItem name="swagger_manage">API Manage</MenuItem>
            </MenuGroup>
          </Submenu>
        </Menu>
      </Sider>
      <Layout>
        <Header :style="{padding: 0}" class="layout-header-bar">
          <!--<Icon @click.native="collapsedSider" :class="rotateIcon" :style="{margin: '20px 20px'}" type="md-menu" size="24"></Icon>-->
          <Dropdown style="float: right;margin: 0 50px 0 0;" @on-click="avaterClick" v-if="user">
            <a href="javascript:void(0)">
              <Avatar style="color: #f56a00;background-color: #fde3cf;font-size: 20px;" v-if="user">{{user.name.charAt(0)}}
              </Avatar>
            </a>
            <DropdownMenu slot="list">
              <DropdownItem name="user-info">{{user.name}}</DropdownItem>
              <DropdownItem divided name="sign-out">退出</DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </Header>
        <Content :style="{margin: '20px', background: '#fff', minHeight: '800px'}">
          <router-view/>
        </Content>
      </Layout>
    </Layout>
  </div>
</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: 'HelloWorld',
    data() {
      return {
        isCollapsed: false,
        user: null,
      }
    },
    computed: {
      rotateIcon() {
        return [
          'menu-icon',
          this.isCollapsed ? 'rotate-icon' : ''
        ];
      },
      menuitemClasses() {
        return [
          'menu-item',
          this.isCollapsed ? 'collapsed-menu' : ''
        ]
      }
    },
    methods: {
      avaterClick(name) {
        if (name == 'sign-out') {
          // 退出登录
          // sessionStorage.removeItem(Global.SESSION_KEY);
          Global.COOKIE.del(Global.SESSION_KEY)
          this.user = null;
          this.$router.push({
            path: '/sign/in'
          })
        }
      },
      selectMenueItem(name) {
        if (name == 'swagger_editor') {
          this.$router.push({
            path: '/swagger/editor'
          })
        } else if (name == 'swagger_manage') {
          this.$router.push({
            path: '/swagger/manage'
          })
        } else if (name == 'schema_manage') {
          this.$router.push({
            path: '/swagger/schema/manage'
          })
        }
      },
      freashUserInfo() {
        // 初始化用户信息
        if (Global.COOKIE.get(Global.SESSION_KEY)) {
          // this.user = JSON.parse(sessionStorage.getItem(Global.SESSION_KEY));
          this.user = JSON.parse(Global.COOKIE.get(Global.SESSION_KEY));
        }
      },


    },
    created() {
      this.freashUserInfo();
    },
    watch: {
      '$route.path': function (newVal, oldVal) {
        this.freashUserInfo();
      }
    },
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .layout {
    border: 1px solid #d7dde4;
    background: #f5f7f9;
    position: relative;
    border-radius: 4px;
    overflow: hidden;
  }

  .layout-header-bar {
    background: #fff;
    box-shadow: 0 1px 1px rgba(0, 0, 0, .1);
  }

  .layout-logo-left {
    width: 90%;
    height: 30px;
    background: #5b6270;
    border-radius: 3px;
    margin: 15px auto;
  }

  .menu-icon {
    transition: all .3s;
    float: left;
  }

  .rotate-icon {
    transform: rotate(-90deg);
  }

  .menu-item span {
    display: inline-block;
    overflow: hidden;
    width: 69px;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: bottom;
    transition: width .2s ease .2s;
  }

  .menu-item i {
    transform: translateX(0px);
    transition: font-size .2s ease, transform .2s ease;
    vertical-align: middle;
    font-size: 16px;
  }

  .collapsed-menu span {
    width: 0px;
    transition: width .2s ease;
  }

  .collapsed-menu i {
    transform: translateX(5px);
    transition: font-size .2s ease .2s, transform .2s ease .2s;
    vertical-align: middle;
    font-size: 22px;
  }
</style>
