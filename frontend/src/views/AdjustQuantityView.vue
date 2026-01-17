<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <!-- Header s ikonou -->
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button class="mr-4" @click="goBack">
        <ArrowLeft class="w-6 h-6"/>
      </button>
      <div :class="['w-8 h-8 rounded-full flex items-center justify-center mr-2',
                    isIncoming ? 'bg-teal-100 text-teal-600' : 'bg-rose-100 text-rose-600']">
        <ArrowDownCircle v-if="isIncoming" class="w-5 h-5"/>
        <ArrowUpCircle v-else class="w-5 h-5"/>
      </div>
      <h1 class="text-xl font-semibold">{{ isIncoming ? 'Príjem' : 'Výdaj' }}</h1>
    </div>

    <div class="flex-grow flex flex-col items-center p-4">
      <!-- Karta s farebným pruhom -->
      <div :class="[
        'w-full max-w-md bg-white rounded-lg shadow-sm overflow-hidden',
        isIncoming ? 'border-t-4 border-teal-500' : 'border-t-4 border-rose-500'
      ]">
        <div class="p-6">
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

            <form class="space-y-6" @submit.prevent="submitAdjustment">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  {{ isIncoming ? 'Množstvo na naskladnenie' : 'Množstvo na vyskladnenie' }}
                </label>
                <input
                    v-model="quantity"
                    :max="isIncoming ? undefined : stock.quantity"
                    class="w-full p-2 border rounded-md"
                    min="0.01"
                    required
                    step="0.01"
                    type="number"
                />
                <p v-if="!isIncoming" class="text-sm text-gray-500 mt-1">
                  Maximálne množstvo: {{ stock.quantity }} {{ stock.unitOfMeasure }}
                </p>
                <p v-if="showQuantityError" class="text-sm text-red-600 font-medium mt-1">
                  ⚠️ Nedostatočné množstvo na sklade. Dostupné: {{ stock.quantity }} {{ stock.unitOfMeasure }}
                </p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Poznámka
                </label>
                <textarea
                    v-model="note"
                    :placeholder="isIncoming ? 'Napr. Nákup, Úroda...' : 'Napr. Predaj, Spotreba...'"
                    class="w-full p-2 border rounded-md"
                    rows="2"
                ></textarea>
              </div>

              <button
                  :class="[
                    'w-full text-white py-3 rounded-lg font-semibold',
                    isIncoming
                      ? 'bg-teal-500 hover:bg-teal-600'
                      : 'bg-rose-500 hover:bg-rose-600'
                  ]"
                  :disabled="loading || !isValidQuantity"
                  type="submit"
              >
                {{ isIncoming ? 'Naskladniť' : 'Vyskladniť' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {ArrowLeft, ArrowDownCircle, ArrowUpCircle} from 'lucide-vue-next'
import { stockApi } from '../services/api'

const router = useRouter()
const route = useRoute()
const stock = ref(null)
const quantity = ref('')
const note = ref('')
const loading = ref(true)
const error = ref(null)

const isIncoming = computed(() => route.query.type === 'in')

const isValidQuantity = computed(() => {
  const qty = parseFloat(quantity.value)
  if (isIncoming.value) {
    return qty > 0
  }
  return qty > 0 && qty <= stock.value?.quantity
})

const showQuantityError = computed(() => {
  if (isIncoming.value) return false
  const qty = parseFloat(quantity.value)
  if (isNaN(qty) || qty <= 0) return false
  return qty > stock.value?.quantity
})

onMounted(async () => {
  try {
    const batchCode = route.params.stockId
    const response = await stockApi.getStockByBatchCode(batchCode)
    stock.value = response.data
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

    const adjustedQuantity = isIncoming.value
        ? parseFloat(quantity.value)
        : -parseFloat(quantity.value)

    await stockApi.adjustStock(stock.value.id, {
      quantity: adjustedQuantity,
      movementType: isIncoming.value ? 'IN' : 'OUT',
      reason: note.value || (isIncoming.value ? 'Príjem' : 'Výdaj')
    })

    router.push('/')
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

const goBack = () => router.back()
</script>