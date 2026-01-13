<!-- views/TestQRView.vue -->
<template>
  <div class="p-4">
    <h2 class="text-xl mb-4">Test QR Codes for Stock Items</h2>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div v-for="stock in stocks" :key="stock.id" class="p-4 bg-white rounded shadow">
        <VueQrcode
            :value="stock.batchCode"
            :options="{ width: 200 }"
        />
        <div class="mt-2 text-center">
          <p class="font-bold">{{ stock.batchCode }}</p>
          <p>{{ stock.quantity }} {{ stock.unitOfMeasure }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import VueQrcode from 'qrcode.vue'

const stocks = ref([])

onMounted(async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/stock`)
    stocks.value = await response.json()
  } catch (error) {
    console.error('Failed to load stocks:', error)
  }
})
</script>