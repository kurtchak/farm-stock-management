<template>
  <div id="app">
    <router-view></router-view>
    <TestComponent></TestComponent>
  </div>
</template>

<script setup>
import TestComponent from "./components/test/TestComponent.vue";
</script>
