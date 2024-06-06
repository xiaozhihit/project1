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
    ElMessage,
    ElMessageBox,
} from "element-plus";
import {get, put} from "../../net";

const rowKey = "id"; // 表格行的 key，用于多选
let loading = ref(false); // 列表数据加载状态
let judges = reactive([]); // 评委列表数据
const total = ref(0); // 总的评委数量
const searchKeyword = ref(""); // 搜索关键字
const selectedJudges = ref([]); // 选中的评委列表


const fetchData = async (searchKeyword) => {
    await get(`/api/user?identity=1&searchKeyword=${searchKeyword}`, (data) => {
        judges = data;
        loading.value = true
    })
    total.value = judges.length
    loading.value = false
}

function handleSelectionChange(selectedRows) {
    selectedJudges.value = selectedRows;
}

const search = async () => {
    await fetchData(searchKeyword.value);
}

watchEffect(async () => {
    if (userStore.refreshOnNavigate) {
        await fetchData('')
    }
});

// 重置选中的评委的密码
const resetPassword = async () => {
    const selectedIds = selectedJudges.value.map((student) => student.id);
    const selectedIdList = selectedIds.map(id => parseInt(id)); // 转换为 List<Integer> 类型
    const confirmResult = await ElMessageBox.confirm(
        `确定要将选中的 ${selectedIds.length} 个评委的密码重置为对应的用户名吗？`,
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
                <el-input v-model.lazy.trim="searchKeyword" placeholder="请输入姓名或用户名进行搜索" clearable>
                </el-input>
            </el-col>
            <el-col :span="10" >
                <el-button style="width:100px ;margin-left: 5px " type="primary" plain  @click="search">搜索</el-button>
            </el-col>
        </el-row>
        <el-table :data="judges" :row-key="rowKey" v-loading="loading" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
<!--            <el-table-column prop="id" label="id"></el-table-column>-->
            <el-table-column prop="name" label="评委姓名"></el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="password" label="密码"></el-table-column>
            <el-table-column prop="groupId" label="组号"></el-table-column>
        </el-table>
        <el-col style="text-align: center;margin-top: 10px">
            <el-button style="width:250px;" type="primary" plain @click="resetPassword"
                       :disabled="selectedJudges.length === 0">
                重置密码
            </el-button>
        </el-col>
    </div>
</template>


<style scoped>
</style>