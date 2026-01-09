<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl">Úprava množstva</h1>
    </div>

    <div class="flex-grow flex flex-col items-center p-4">
      <div class="w-full max-w-md bg-white rounded-lg shadow-sm p-6">
        <!-- Loading state -->
        <div v-if="loading" class="text-center py-8">
          <div class="animate-spin h-8 w-8 border-4 border-blue-500 border-t-transparent rounded-full mx-auto mb-4"></div>
          <p>Načítavam...</p>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="text-center text-red-500 py-8">
          {{ error }}
        </div>

        <!-- Stock info and form -->
        <div v-else>
          <div class="mb-6 p-4 bg-gray-50 rounded-lg">
            <h2 class="text-lg font-semibold text-gray-900">{{ stock.crop.name }}</h2>
            <p class="text-gray-600">Kód: {{ stock.batchCode }}</p>
            <p class="text-gray-600">Aktuálne množstvo: {{ stock.quantity }} {{ stock.unitOfMeasure }}</p>
          </div>

          <form @submit.prevent="submitAdjustment" class="space-y-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Množstvo na odpísanie
              </label>
              <input
                  v-model="quantity"
                  type="number"
                  required
                  min="0.01"
                  :max="stock.quantity"
                  step="0.01"
                  class="w-full p-2 border rounded-md"
              />
              <p class="text-sm text-gray-500 mt-1">
                Maximálne množstvo: {{ stock.quantity }} {{ stock.unitOfMeasure }}
              </p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Poznámka
              </label>
              <textarea
                  v-model="note"
                  rows="2"
                  class="w-full p-2 border rounded-md"
              ></textarea>
            </div>

            <button
                type="submit"
                class="w-full bg-[#94c1e8] text-white py-3 rounded-lg hover:bg-[#7eb3e2]"
                :disabled="loading || !isValidQuantity"
            >
              Potvrdiť
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const stock = ref(null)
const quantity = ref('')
const note = ref('')
const loading = ref(true)
const error = ref(null)

const isValidQuantity = computed(() => {
  const qty = parseFloat(quantity.value)
  return qty > 0 && qty <= stock.value?.quantity
})

onMounted(async () => {
  try {
    const batchCode = route.params.stockId
    const response = await fetch(`/api/stock/batch/${batchCode}`)
    if (!response.ok) throw new Error('Položka nebola nájdená')
    stock.value = await response.json()
  } catch (e) {
    error.value = 'Nepodarilo sa načítať položku'
  } finally {
    loading.value = false
  }
})

const submitAdjustment = async () => {
  if (!isValidQuantity.value) return

  try {
    loading.value = true
    const response = await fetch(`/api/stock/${stock.value.id}/adjust`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        quantity: -parseFloat(quantity.value), // negative for reduction
        movementType: 'OUT',
        reason: note.value || 'Predaj'
      })
    })

    if (!response.ok) throw new Error('Nepodarilo sa upraviť množstvo')

    router.push('/')
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

const goBack = () => router.back()
</script>