<script setup>
import {ref, reactive, watchEffect} from "vue";
import {useUserStore} from "../../stores/userStore";

const userStore = useUserStore()
import {
    ElRow,
    ElCol,
    ElInput,
    ElButton,
    ElTable,
    ElTableColumn,
    ElPagination,
    ElMessage,
    ElMessageBox,
} from "element-plus";
import {get, put} from "../../net";

const rowKey = "id"; // 表格行的 key，用于多选
let loading = ref(false); // 列表数据加载状态
let students = reactive([]); // 学生列表数据
const total = ref(0); // 总的学生数量
const searchKeyword = ref(""); // 搜索关键字
const selectedStudents = ref([]); // 选中的学生列表

/** todo 由于时间关系，我并没有实现分页查询功能**/
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

const fetchData = async (searchKeyword) => {
    await get(`/api/user?identity=0&searchKeyword=${searchKeyword}`, (data) => {
        loading.value = true
        students = data;
    })
    total.value = students.length
    loading.value = false
}

function handleSelectionChange(selectedRows) {
    selectedStudents.value = selectedRows;
}

const search = async () => {
    currentPage.value = 1;
    await fetchData(searchKeyword.value);
}

watchEffect(async () => {
    if (userStore.refreshOnNavigate) {
        await fetchData('')
    }
});

// 重置选中的学生的密码
const resetPassword = async () => {
    const selectedIds = selectedStudents.value.map((student) => student.id);
    const selectedIdList = selectedIds.map(id => parseInt(id)); // 转换为 List<Integer> 类型
    // console.log(selectedIds)
    // console.log(selectedIdList)
    const confirmResult = await ElMessageBox.confirm(
        `确定要将选中的 ${selectedIds.length} 个学生的密码重置为对应的用户名吗？`,
        "提示"
    );
    if (confirmResult === "confirm") {
        await put('/api/user', selectedIdList, (data) => {
            ElMessage.success("重置成功!")
        })
        await fetchData('')
    }
}
</script>

<template>
    <div>
        <el-row>
            <el-col :span="5">
                <el-input v-model.lazy.trim="searchKeyword" placeholder="请输入姓名或学号进行搜索" clearable>
                </el-input>
            </el-col>
            <el-col :span="10" >
                <el-button style="width:100px ;margin-left: 5px " type="primary" plain  @click="search">搜索</el-button>
            </el-col>
        </el-row>
        <el-table :data="students" :row-key="rowKey" v-loading="loading" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
<!--            <el-table-column prop="id" label="id"></el-table-column>-->
            <el-table-column prop="name" label="姓名"></el-table-column>
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
                <el-button type="primary" plain @click="resetPassword" :disabled="selectedStudents.length === 0">
                    重置密码
                </el-button>
            </el-col>
        </el-row>
    </div>
</template>


<style scoped>
</style>