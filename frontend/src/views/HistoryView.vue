<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50 flex flex-col">
    <!-- Header -->
    <div class="bg-white shadow-sm px-4 py-3">
      <div class="flex items-center">
        <button
            @click="goBack"
            class="flex items-center text-gray-500 active:text-gray-700 transition-colors p-2 -ml-2"
        >
          <ArrowLeft class="w-5 h-5" />
        </button>
        <h1 class="text-xl font-bold text-gray-800 ml-1">História pohybov</h1>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4 overflow-auto pb-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-xl p-4 text-center">
        <p class="text-red-600">{{ error }}</p>
      </div>

      <!-- Movements List -->
      <div v-else-if="movements.length > 0" class="space-y-3">
        <div
            v-for="movement in movements"
            :key="movement.id"
            class="bg-white rounded-xl shadow-sm p-4"
        >
          <div class="flex flex-col gap-2">
            <!-- Top row: crop name on left, amount on right -->
            <div class="flex items-start justify-between">
              <h3 class="text-base font-semibold text-gray-800">{{ movement.name }}</h3>
              <p
                  :class="[
                    'text-lg font-bold',
                    movement.type === 'in' ? 'text-green-600' : 'text-amber-600'
                  ]"
              >
                {{ movement.type === 'in' ? '+' : '-' }}{{ movement.amount }}
              </p>
            </div>

            <!-- Middle row: type indicator + reason -->
            <div class="flex items-center gap-2">
              <div
                  :class="[
                    'w-2 h-2 rounded-full',
                    movement.type === 'in' ? 'bg-green-500' : 'bg-amber-500'
                  ]"
              ></div>
              <p v-if="movement.reason" class="text-sm text-gray-600">{{ movement.reason }}</p>
            </div>

            <!-- Bottom row: user on left, time on right -->
            <div class="flex items-end justify-between">
              <p v-if="movement.user" class="text-xs text-gray-500">👤 {{ movement.user }}</p>
              <p class="text-sm text-gray-500">{{ movement.time }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <div class="text-6xl mb-4">📦</div>
        <p class="text-gray-500">Žiadne pohyby</p>
      </div>

      <!-- Load More Button -->
      <div v-if="movements.length > 0 && hasMore" class="mt-6">
        <button
            @click="loadMore"
            :disabled="loadingMore"
            class="w-full bg-white text-gray-700 font-medium py-3 rounded-xl shadow-sm active:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
        >
          <span v-if="loadingMore">Načítavam...</span>
          <span v-else>Načítať viac</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'
import { stockMovementApi } from '../services/api'

const router = useRouter()
const movements = ref([])
const loading = ref(true)
const loadingMore = ref(false)
const error = ref(null)
const limit = ref(20)
const currentOffset = ref(0)
const hasMore = ref(true)

onMounted(async () => {
  await fetchMovements()
})

const fetchMovements = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
  }

  error.value = null

  try {
    const response = await stockMovementApi.getRecentMovements(limit.value)
    const data = response.data

    if (isLoadMore) {
      movements.value = [...movements.value, ...formatMovements(data)]
    } else {
      movements.value = formatMovements(data)
    }

    // Check if there are more movements to load
    hasMore.value = data.length === limit.value
    currentOffset.value += data.length
  } catch (err) {
    error.value = 'Nepodarilo sa načítať históriu pohybov'
    console.error('Error fetching movements:', err)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const formatMovements = (data) => {
  return data.map(movement => {
    const cropName = movement.cropVariety
      ? `${movement.cropName} ${movement.cropVariety}`
      : movement.cropName

    return {
      id: movement.id,
      name: cropName || 'Neznáma plodina',
      time: formatDateTime(movement.createdAt),
      amount: `${Math.abs(movement.quantity)} ${movement.unitOfMeasure}`,
      type: movement.movementType.toLowerCase(),
      reason: movement.reason,
      user: movement.userFullName || movement.userName
    }
  })
}

const formatDateTime = (dateTime) => {
  const date = new Date(dateTime)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 60) {
    return diffMins <= 1 ? 'Práve teraz' : `Pred ${diffMins} min`
  } else if (diffHours < 24) {
    return `Dnes, ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  } else if (diffDays === 1) {
    return `Včera, ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  } else if (diffDays < 7) {
    return `Pred ${diffDays} dňami, ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  } else {
    return `${date.toLocaleDateString('sk-SK')} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  }
}

const loadMore = async () => {
  limit.value += 20
  await fetchMovements(true)
}

const goBack = () => {
  router.back()
}
</script>
