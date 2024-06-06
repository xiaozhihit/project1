<script setup>

import {reactive, ref, watchEffect} from 'vue'
import {get, post, put} from "../../net";
import {useUserStore} from "../../stores/userStore";
import {
    ElButton,
    ElCol,
    ElInput,
    ElMessage,
    ElMessageBox,
    ElPagination,
    ElRow,
    ElTable,
    ElTableColumn
} from "element-plus";

const activeNames = ref(['1'])//默认打开第一个折叠窗
const userStore = useUserStore()
let students = ref([])

const judge = reactive({
    name: '',
    username: '',
    password: '',
})
const handleChange = (val) => {
    console.log(val)
}
const addJudge = () => {
    if (judge.name === '') {
        ElMessage.warning('评委姓名不能为空')
        return
    }
    if (judge.username === '') {
        ElMessage.warning('评委用户名不能为空')
        return
    }
    if (judge.password === '') {
        ElMessage.warning('评委密码不能为空')
        return
    }
    post('/api/judge', {
        username: judge.username,
        name: judge.name,
        password: judge.password,
    }, (data) => {
        ElMessage.success(data)
    })
}
const fetchData = async (searchKeyword) => {
    await get(`/api/user?identity=0&searchKeyword=${searchKeyword}`, (data) => {
        loading.value = true
        students = data
        total.value = students.length
    })
    loading.value = false
}
watchEffect(async () => {
    if (userStore.refreshOnNavigate) {
        await fetchData('')
    }
});

const rowKey = "id"; // 表格行的 key，用于多选
let loading = ref(false); // 列表数据加载状态
const total = ref(0); // 总的学生数量
const searchKeyword = ref(""); // 搜索关键字
const selectedStudents = ref([]); // 选中的学生列表
const currentPage = ref(1); // 当前页码
const pageSize = ref(20); // 每页显示的学生数量
// 处理页码和每页显示数量变化的事件
function handleSizeChange(size) {
    ElMessage.success("分页功能暂时没有实现")
    currentPage.value = 1;
    // pageSize.value = size;
}

function handleCurrentChange(page) {
    ElMessage.success("分页功能暂时没有实现")
    currentPage.value = page;
}

function handleSelectionChange(selectedRows) {
    selectedStudents.value = selectedRows;
}

const search = async () => {
    currentPage.value = 1;
    await fetchData(searchKeyword.value);
}
//设置选中学生为评委
const setAsJudge = async () => {
    const selectedIds = selectedStudents.value.map((student) => student.id);
    const selectedIdList = selectedIds.map(id => parseInt(id)); // 转换为 List<Integer> 类型
    // console.log(selectedIds)
    // console.log(selectedIdList)
    const confirmResult = await ElMessageBox.confirm(
        `确定要将选中的 ${selectedIds.length} 个学生的设置为评委吗？`,
        "提示"
    );
    if (confirmResult === "confirm") {
        await put('/api/judge', selectedIdList, (data) => {
            ElMessage.success(data)
        })
        await fetchData('')
    }
}

</script>

<template>
    <div>
        <el-collapse v-model="activeNames" @change="handleChange" accordion>
            <el-collapse-item title="新增评委用户信息" name="1">
                <div style="margin: 20px"/>
                <el-form
                        :label-position="'right'"
                        label-width="100px"
                        :model="judge"
                        style="max-width: 460px"
                >
                    <el-form-item label="评委姓名">
                        <el-input
                                v-model="judge.name"/>
                    </el-form-item>
                    <el-form-item label="用户名">
                        <el-input
                                v-model="judge.username"/>
                    </el-form-item>
                    <el-form-item label="初始密码">
                        <el-input v-model="judge.password"/>
                    </el-form-item>
                    <el-button style="margin-left: 230px" type="primary" plain @click="addJudge">新增</el-button>
                </el-form>
            </el-collapse-item>
            <el-collapse-item title="选取学生作为评委" name="2">
                <div>
                    <el-row>
                        <el-col :span="5">
                            <el-input v-model.lazy.trim="searchKeyword" placeholder="请输入姓名或学号进行搜索"
                                      clearable>
                            </el-input>
                        </el-col>
                        <el-col :span="10">
                            <el-button style="width:100px ;margin-left: 5px " type="primary" plain @click="search">
                                搜索
                            </el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="students" :row-key="rowKey" v-loading="loading"
                              @selection-change="handleSelectionChange">
                        <el-table-column type="selection" width="55"></el-table-column>
                        <el-table-column prop="name" label="姓名"></el-table-column>
                        <el-table-column prop="identity" label="身份">
                            <template v-slot="{ row }">
                                <span v-if="row.isJudge">评委</span>
                                <span v-else>仅学生</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="username" label="学号"></el-table-column>
                        <el-table-column prop="password" label="密码"></el-table-column>
                        <el-table-column prop="groupId" label="组号"></el-table-column>
                    </el-table>
                    <el-row>
                        <el-pagination :span="12"
                                       background
                                       layout="total, sizes, prev, pager, next, jumper"
                                       :total="total"
                                       :page-size="pageSize"
                                       :current-page.sync="currentPage"
                                       :page-sizes="[10, 20, 30, 40]"
                                       @size-change="handleSizeChange"
                                       @current-change="handleCurrentChange"
                        ></el-pagination>
                        <el-col :span="12">
                            <el-button type="primary" plain @click="setAsJudge"
                                       :disabled="selectedStudents.length === 0">
                                设为评委
                            </el-button>
                        </el-col>
                    </el-row>
                </div>
            </el-collapse-item>
        </el-collapse>
    </div>
</template>

<style scoped>

</style>