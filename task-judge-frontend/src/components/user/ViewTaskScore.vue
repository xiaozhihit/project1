<script setup>
import {get} from "../../net";
import {reactive, ref, watchEffect} from "vue";
import {useUserStore} from "../../stores/userStore";
import {ElMessage} from "element-plus";

const userStore = useUserStore()
const username = userStore.username
let scores = reactive([])
let flag = ref(false)
//记录是否已经获取到响应即scores，并处理完数据
//这里我遇到了一个非常非常奇怪的bug，如果v-if条件是 scores相关的任何条件都不行
let maxScoreIndex = ref()
let minScoreIndex = ref()
let avgScore = ref()
const fetchData = async () => {
    await get(`/api/user/scores?username=${username}`, (data) => {
        scores = data
    })
}
const computeAvgScores = async () => {
    let maxScore = 0
    let minScore = Infinity
    let total = 0;
    for (let i = 0; i < scores.length; i++) {
        const score = Number(scores[i].total)
        total += score
        if (score > maxScore) {
            maxScore = score
            maxScoreIndex.value = i
        }
        if (score < minScore) {
            minScore = score
            minScoreIndex.value = i
        }
    }
    if (scores.length > 2) {
        total -= maxScore + minScore
        avgScore = total / (scores.length - 2)
    } else {
        avgScore = total / scores.length
    }
    //默认保留2位小数点
    avgScore = avgScore.toFixed(2)
}
watchEffect(async () => {
    if (userStore.refreshOnNavigate) {
        await fetchData()
        await computeAvgScores()
        flag.value = true
    }
});

const question = () => {
    ElMessage.success('申诉功能还在开发中 ! 请耐心等待')
}

</script>

<template>
    <div>
        <h1>大作业成绩</h1>
    </div>
    <div>
        <div v-if="flag">
            <el-table :data="scores" stripe border>
                <el-table-column label="小组编号" prop="groupId"></el-table-column>
                <el-table-column label="工作量得分40%" prop="workloadScore"></el-table-column>
                <el-table-column label="完成度得分40%" prop="completionScore"></el-table-column>
                <el-table-column label="设计得分10%" prop="designScore"></el-table-column>
                <el-table-column label="分工合作得分10%" prop="divisionScore"></el-table-column>
                <el-table-column label="总分" prop="total"></el-table-column>
            </el-table>
            <div style="text-align: center">
                <p>去掉最高分 {{scores[maxScoreIndex].total}} 和最低分 {{scores[minScoreIndex].total}} 后，您的大作业最终总成绩平均分为：{{ avgScore }}</p>
                如有疑义
                <el-button @click="question()" style="width: 160px"
                           type="warning"
                           plain>点此申诉
                </el-button>
            </div>
        </div>

        <div v-else>
            <p>正在加载数据，请稍候...</p>
        </div>
    </div>
</template>

<style scoped>
</style>