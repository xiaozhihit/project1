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

const currentPage = ref(1);
const pageSize = ref(20);
function handleSizeChange(size) {
    ElMessage.success("分页功能暂时没有实现")
    currentPage.value = 1;
}
function handleCurrentChange(page) {
    ElMessage.success("分页功能暂时没有实现")
    currentPage.value = page;
}

const fetchData = async (searchKeyword) => {
    await get(`/api/user?identity=0&searchKeyword=${searchKeyword}`, (data) => {
        students = data;
        loading.value = true
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

// 将选中学生分到同一组中
const assignGroupId = async () => {
    const selectedIds = selectedStudents.value.map((student) => student.id);
    const selectedIdList = selectedIds.map(id => parseInt(id)); // 转换为 List<Integer> 类型
    const { value: groupId } = await ElMessageBox.prompt("请输入要分配到的小组编号", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputPattern: /\d+/,
        inputErrorMessage: "请输入数字",
    });
    if (groupId) {
        const confirmResult = await ElMessageBox.confirm(
            `确定要将选中的 ${selectedIds.length} 个学生分配到${groupId}号小组吗？`,
            "提示"
        );
        if (confirmResult === "confirm") {
            const requestBody = {
                ids: selectedIdList,
                groupId: parseInt(groupId),
            };
            await put('/api/user/groupId', requestBody, (data) => {
                ElMessage.success("分配成功!")
            })
            await fetchData('')
        }
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
            <el-table-column prop="name" label="学生姓名"></el-table-column>
            <el-table-column prop="groupId" label="组号"></el-table-column>
            <el-table-column prop="identity" label="身份">
                <template v-slot="{ row }">
                    <span v-if="row.isJudge">评委</span>
                    <span v-else>仅学生</span>
                </template>
            </el-table-column>
            <el-table-column prop="username" label="学号"></el-table-column>
            <el-table-column prop="password" label="密码"></el-table-column>
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
                <el-button type="primary" plain @click="assignGroupId" :disabled="selectedStudents.length === 0">
                    指定小组
                </el-button>
            </el-col>
        </el-row>
    </div>
</template>


<style scoped>
</style>