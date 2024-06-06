<script setup>

</script>

<template>
    <div style="width:100vw;height: 100vh;overflow: hidden;display: flex">
        <div style="flex: 1;">
            <el-image style="width: 100%;height: 100%" fit="cover"
                      src="https://i3.3conline.com/images/piclib/201001/25/batch/1/53404/1264408156918rnm1g99kpm.jpg"/>
        </div>
        <div style="position: absolute;bottom: 20px;left: 20px;color: fuchsia;font-size: 12px;text-shadow: 0 0 10px black">
        </div>
        <div style="width: 450px;">
            <router-view/>
        </div>
    </div>
</template>

<style scoped>

</style>