<script setup>
import {ref, reactive, watchEffect} from "vue";
import {useUserStore} from "../../stores/userStore";

const userStore = useUserStore()
import {
    ElCol,
    ElButton,
    ElTable,
    ElTableColumn,
    ElMessage,
    ElMessageBox,
} from "element-plus";
import {get, put} from "../../net";

const rowKey = "id"; // 表格行的 key，用于多选
let loading = ref(false); // 列表数据加载状态
let scores = reactive([]); // 评分列表数据
const selectedScores = ref([]); // 选中的评委列表


const fetchData = async () => {
    await get('/api/score',(data)=>{
        loading.value=true
        scores=data
    })
    loading.value = false
}

function handleSelectionChange(selectedRows) {
    selectedScores.value = selectedRows;
}


watchEffect(async () => {
    if (userStore.refreshOnNavigate) {
        await fetchData()
    }
});

//撤销评委打分提交状态至可修改状态
const resetPassword = async () => {
    const selectedIds = selectedScores.value.map((student) => student.id);
    const selectedIdList = selectedIds.map(id => parseInt(id)); // 转换为 List<Integer> 类型
    console.log(selectedIds)
    console.log(selectedIdList)
    const confirmResult = await ElMessageBox.confirm(
        `确定要将选中的 ${selectedIds.length} 条成绩打分状态重置至可修改状态吗？`,
        "提示"
    );
    if (confirmResult === "confirm") {
        await put('/api/score', selectedIdList, (data) => {
            ElMessage.success("重置成功!")
        })
        await fetchData()
    }
}
</script>

<template>
    <div>
        <el-table :data="scores" :row-key="rowKey" v-loading="loading" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column prop="groupId" label="组号"></el-table-column>
            <el-table-column prop="judgeUsername" label="评委用户名"></el-table-column>
            <el-table-column prop="workloadScore" label="工作量得分"></el-table-column>
            <el-table-column prop="completionScore" label="完成度得分"></el-table-column>
            <el-table-column prop="designScore" label="设计得分"></el-table-column>
            <el-table-column prop="divisionScore" label="分工得分"></el-table-column>
            <el-table-column prop="isSubmitted" label="打分状态">
                <template v-slot="{ row }">
                    <span v-if="row.isSubmitted">评分已完成</span>
                    <span v-else>可修改</span>
                </template>
            </el-table-column>
        </el-table>
        <el-col style="text-align: center;margin-top: 10px">
            <el-button style="width:250px;" type="primary" plain @click="resetPassword"
                       :disabled="selectedScores.length === 0">
                重置打分状态
            </el-button>
        </el-col>
    </div>
</template>


<style scoped>
</style>