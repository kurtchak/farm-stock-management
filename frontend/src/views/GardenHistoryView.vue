<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-green-50 to-teal-50 flex flex-col">
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
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700"></div>
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
            <!-- Top row: item name on left, amount on right -->
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-2">
                <h3 class="text-base font-semibold text-gray-800">{{ movement.itemName }}</h3>
                <span v-if="movement.plantingSetName"
                  class="text-[10px] px-2 py-0.5 rounded-full bg-green-100 text-green-700 font-medium">
                  {{ movement.plantingSetName }}
                </span>
              </div>
              <p
                :class="[
                  'text-lg font-bold',
                  movement.movementType === 'IN' ? 'text-green-600' : 'text-amber-600'
                ]"
              >
                {{ movement.movementType === 'IN' ? '+' : '-' }}{{ movement.quantity }} {{ movement.unit }}
              </p>
            </div>

            <!-- Middle row: type indicator + reason -->
            <div class="flex items-center gap-2">
              <div
                :class="[
                  'w-2 h-2 rounded-full',
                  movement.movementType === 'IN' ? 'bg-green-500' : 'bg-amber-500'
                ]"
              ></div>
              <span class="text-[10px] px-1.5 py-0.5 rounded bg-gray-100 text-gray-500">{{ categoryLabel(movement.itemCategory) }}</span>
              <p v-if="movement.reason" class="text-sm text-gray-600">{{ movement.reason }}</p>
            </div>

            <!-- Bottom row: user on left, time on right -->
            <div class="flex items-end justify-between">
              <p v-if="movement.userName" class="text-xs text-gray-500">{{ movement.userName }}</p>
              <p class="text-sm text-gray-500">{{ formatDateTime(movement.createdAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <History class="w-16 h-16 text-gray-300 mx-auto mb-4" />
        <p class="text-gray-500">Žiadne pohyby</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, History } from 'lucide-vue-next'
import { useGardenStore } from '../stores/garden'

const router = useRouter()
const gardenStore = useGardenStore()

const loading = computed(() => gardenStore.loading)
const error = computed(() => gardenStore.error)
const movements = computed(() => gardenStore.movements)

const categoryLabel = (cat) => {
  const labels = { TREE: 'Strom', STAKE: 'Kolík', PROTECTION: 'Ochrana', OTHER: 'Ostatné' }
  return labels[cat] || cat
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

onMounted(async () => {
  await gardenStore.fetchMovements()
})

const goBack = () => {
  router.back()
}
</script>
