<template>
  <el-menu-item index="user-management">
    <el-icon><User /></el-icon>
    <template #title>用户管理</template>
  </el-menu-item>
  <el-sub-menu index="movie-management">
    <template #title>
      <el-icon><Film /></el-icon>
      <span>电影管理</span>
    </template>
    <el-menu-item index="movies">电影列表</el-menu-item>
    <el-menu-item index="genres">类型管理</el-menu-item>
    <el-menu-item index="sessions">场次管理</el-menu-item>

  </el-sub-menu>
  <el-menu-item index="orders">
    <el-icon><Document /></el-icon>
    <span>订单管理</span>
  </el-menu-item>
  <el-menu-item index="news">
    <el-icon><Document /></el-icon>
    <span>资讯管理</span>
  </el-menu-item>
</template>
