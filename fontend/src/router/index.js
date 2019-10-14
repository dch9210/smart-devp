import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import SwaggerEditor from '@/components/SwaggerEditor.vue'
import SwaggerManage from '@/components/SwaggerManage.vue'
import SwaggerSchemaManage from '@/components/SwaggerSchemaManage.vue'
import SwaggerApiTest from '@/components/SwaggerApiTest.vue'
import SwaggerApiShareProfile from '@/components/SwaggerApiShareProfile.vue'
import SignUp from '@/components/SignUp.vue'
import SignIn from '@/components/SignIn.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home Page',
      component: HelloWorld,
      meta: {
        requireAuth: true,
        title: 'Work Station'
      },
      children: [
        {
          path: 'swagger/editor',
          component: SwaggerEditor,
          meta: {
            requireAuth: true,
          },
        },
        {
          path: 'swagger/editor/:apiId',
          component: SwaggerEditor,
          meta: {
            requireAuth: true,
          },
        },
        {
          path: 'swagger/manage',
          component: SwaggerManage,
          meta: {
            requireAuth: true,
          },
        },
        {
          path: 'swagger/schema/manage',
          component: SwaggerSchemaManage,
          meta: {
            requireAuth: true,
          },
        },
        {
          path: 'swagger/test/:apiId',
          component: SwaggerApiTest,
          meta: {
            requireAuth: true,
          },
        },
        {
          path: 'sign/up',
          component: SignUp,
          meta: {
            requireAuth: false,
          },
        },
        {
          path: 'sign/in',
          component: SignIn,
          meta: {
            requireAuth: false,
          },
        },
      ]
    },
    {
      path: '/swagger/share/profile/:apiId',
      name: 'SWG Share Profile',
      component: SwaggerApiShareProfile,
      meta: {
        requireAuth: false,
      },
    }
  ]
})
