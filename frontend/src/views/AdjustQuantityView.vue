<!-- views/AdjustQuantityView.vue -->
<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl">Upraviť množstvo</h1>
    </div>

    <div class="flex-grow flex flex-col items-center p-4">
      <div class="w-full max-w-md bg-white rounded-lg shadow-sm p-6">
        <div v-if="loading">Načítavam...</div>
        <div v-else-if="error">{{ error }}</div>
        <form v-else @submit.prevent="submitAdjustment" class="space-y-6">
          <div>
            <h2 class="text-lg font-medium">{{ stock.name }}</h2>
            <p class="text-gray-600">Aktuálne množstvo: {{ stock.quantity }} {{ stock.unit }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Množstvo na odpísanie</label>
            <input
                v-model="quantity"
                type="number"
                required
                min="0.01"
                :max="stock.quantity"
                step="0.01"
                class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            />
          </div>

          <button
              type="submit"
              class="w-full bg-[#94c1e8] text-white py-2 px-4 rounded-lg hover:bg-[#7eb3e2]"
          >
            Potvrdiť
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const stockId = route.params.stockId

const stock = ref(null)
const quantity = ref(0)
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    // Fetch stock details from API
    const response = await fetch(`/api/stock/${stockId}`)
    if (!response.ok) throw new Error('Stock not found')
    stock.value = await response.json()
  } catch (e) {
    error.value = 'Nepodarilo sa načítať položku'
  } finally {
    loading.value = false
  }
})

const submitAdjustment = async () => {
  try {
    const response = await fetch(`/api/stock/${stockId}/adjust`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        quantity: -quantity.value,
        movementType: 'OUT',
        reason: 'Sale'
      })
    })

    if (!response.ok) throw new Error('Failed to adjust stock')
    router.push('/success')
  } catch (e) {
    error.value = 'Nepodarilo sa upraviť množstvo'
  }
}

const goBack = () => router.back()
</script>