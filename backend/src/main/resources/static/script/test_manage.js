var vm;

$(function() {


	vm = new Vue({
		el : '#app',
		data : {
			// 测试任务管理表格 列 定义
			testManageColumns: [
                {
                    title: 'Name',
                    key: 'name'
                },
                {
                    title: 'Age',
                    key: 'age'
                },
                {
                    title: 'Address',
                    key: 'address'
                }
            ],
            // 测试任务管理表格 数据 定义
            testManageDatas: [],
		},
		methods : {
			createTestTask : function() {
				
			},
		},
		created : function() {
			// do something magic.
		},
		mounted : function() {
			// do something magic.
		},
		computed : {
			// 计算属性的 getter
			reversedMessage : function() {
				// `this` 指向 vm 实例
				return this.message.split('').reverse().join('')
			}
		},
		watch : {
			// do something magic.
		},
	})


});