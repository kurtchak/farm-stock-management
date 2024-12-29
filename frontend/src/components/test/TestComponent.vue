<template>
  <div class="test-connection">
    <h2>Backend Connection Test</h2>
    <button @click="testConnection" class="test-button">
      Test Connection
    </button>
    <div v-if="response" class="response">
      Response: {{ response }}
    </div>
    <div v-if="error" class="error">
      Error: {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const response = ref(null)
const error = ref(null)

const testConnection = async () => {
  try {
    error.value = null
    const result = await axios.get(`${import.meta.env.VITE_BACKEND_URL}/api/test`)
    response.value = result.data
  } catch (e) {
    error.value = e.message
    console.error('Connection error:', e)
  }
}
</script>

<style scoped>
.test-connection {
  padding: 20px;
  max-width: 500px;
  margin: 0 auto;
}

.test-button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 10px 0;
}

.response {
  margin-top: 20px;
  padding: 10px;
  background-color: #e8f5e9;
  border-radius: 4px;
}

.error {
  margin-top: 20px;
  padding: 10px;
  background-color: #ffebee;
  border-radius: 4px;
  color: #c62828;
}
</style>