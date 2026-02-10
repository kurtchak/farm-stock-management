<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-green-50 to-teal-50 flex flex-col">
    <!-- Header -->
    <div class="bg-white shadow-sm px-4 py-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <button
            @click="navigateBack"
            class="flex items-center text-gray-500 active:text-gray-700 transition-colors p-2 -ml-2"
          >
            <ArrowLeft class="w-5 h-5" />
          </button>
          <h1 class="text-xl font-bold text-gray-800 ml-1 flex items-center gap-2">
            <Flower2 class="w-5 h-5 text-green-700" /> Záhrada
          </h1>
        </div>
        <button
          @click="handleLogout"
          class="w-10 h-10 rounded-xl bg-red-50 flex items-center justify-center text-red-500 active:bg-red-100 transition-colors"
          title="Odhlasiť sa"
        >
          <LogOut class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4 space-y-5 overflow-auto pb-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700"></div>
      </div>

      <template v-else>
        <!-- Posledné pohyby -->
        <div>
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Posledné pohyby</p>
          <div v-if="recentMovements.length === 0" class="bg-white rounded-xl p-4 shadow-sm text-center text-gray-400 text-sm">
            Žiadne pohyby
          </div>
          <div v-else class="space-y-2">
            <div
              v-for="movement in recentMovements"
              :key="movement.id"
              class="bg-white rounded-xl px-4 py-3 shadow-sm"
            >
              <div class="flex items-center justify-between">
                <p class="text-sm font-medium text-gray-800">{{ movement.gardenItem?.name || 'Neznáma položka' }}</p>
                <span
                  :class="movement.type === 'IN' ? 'text-green-600' : 'text-orange-500'"
                  class="text-sm font-bold"
                >
                  {{ movement.type === 'IN' ? '+' : '-' }}{{ movement.quantity }} {{ movement.gardenItem?.unit || '' }}
                </span>
              </div>
              <div class="flex items-center gap-2 mt-1">
                <span class="text-[11px] bg-gray-100 text-gray-500 px-2 py-0.5 rounded-full">{{ categoryLabel(movement.gardenItem?.category) }}</span>
                <span class="text-[11px] text-gray-400">{{ formatDateTime(movement.createdAt) }}</span>
              </div>
            </div>
            <router-link
              to="/gardens/history"
              class="block text-center text-sm text-green-700 font-medium py-2 active:text-green-900"
            >
              Zobraziť všetky →
            </router-link>
          </div>
        </div>

        <!-- Planting Sets - Quick Actions (icon row) -->
        <div v-if="activeSets.length > 0">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Sadby - rýchle akcie</p>
          <div class="flex gap-4 overflow-x-auto pb-2">
            <button
              v-for="set in activeSets"
              :key="set.id"
              @click="openExecuteModal(set)"
              :disabled="maxExecutions(set) === 0"
              class="flex flex-col items-center gap-1.5 min-w-[64px] disabled:opacity-40"
            >
              <div class="relative">
                <div
                  class="w-14 h-14 rounded-full flex items-center justify-center shadow-md"
                  :style="{ backgroundColor: set.color }"
                >
                  <TreePine class="w-7 h-7 text-white" />
                </div>
                <span
                  v-if="maxExecutions(set) > 0"
                  class="absolute -top-1 -right-1 bg-white text-gray-800 text-[10px] font-bold rounded-full w-5 h-5 flex items-center justify-center shadow border border-gray-200"
                >
                  {{ maxExecutions(set) }}
                </span>
              </div>
              <span class="text-[11px] font-medium text-gray-700 text-center leading-tight max-w-[72px] truncate">{{ set.name }}</span>
            </button>
          </div>
        </div>

        <!-- Management Actions -->
        <div>
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Správa</p>
          <div class="grid grid-cols-2 gap-3">
            <button
              @click="router.push('/gardens/items')"
              class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
            >
              <div class="w-10 h-10 rounded-xl bg-green-100 flex items-center justify-center">
                <Package class="w-5 h-5 text-green-700" />
              </div>
              <div class="text-left">
                <p class="text-sm font-bold text-gray-700">Materiál</p>
                <p class="text-[11px] text-gray-400">Prehľad zásob</p>
              </div>
            </button>

            <button
              @click="router.push('/gardens/sets')"
              class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
            >
              <div class="w-10 h-10 rounded-xl bg-amber-100 flex items-center justify-center">
                <Layers class="w-5 h-5 text-amber-600" />
              </div>
              <div class="text-left">
                <p class="text-sm font-bold text-gray-700">Sadby</p>
                <p class="text-[11px] text-gray-400">Zostavy výsadby</p>
              </div>
            </button>

            <button
              @click="router.push('/gardens/items/create')"
              class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
            >
              <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center">
                <Plus class="w-5 h-5 text-blue-600" />
              </div>
              <div class="text-left">
                <p class="text-sm font-bold text-gray-700">Nový materiál</p>
                <p class="text-[11px] text-gray-400">Pridať položku</p>
              </div>
            </button>

            <button
              @click="router.push('/gardens/history')"
              class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
            >
              <div class="w-10 h-10 rounded-xl bg-purple-100 flex items-center justify-center">
                <History class="w-5 h-5 text-purple-600" />
              </div>
              <div class="text-left">
                <p class="text-sm font-bold text-gray-700">História</p>
                <p class="text-[11px] text-gray-400">Všetky pohyby</p>
              </div>
            </button>
          </div>
        </div>
      </template>
    </div>

    <!-- Execute Set Modal -->
    <div v-if="executeModal.show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-2xl p-6 max-w-sm w-full">
        <h2 class="text-xl font-bold text-gray-800 mb-1">{{ executeModal.set?.name }}</h2>
        <p v-if="executeModal.set?.description" class="text-sm text-gray-500 mb-4">{{ executeModal.set.description }}</p>

        <!-- Quantity selector -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">Počet výsadieb</label>
          <div class="flex items-center gap-3">
            <button
              @click="executeModal.count = Math.max(1, executeModal.count - 1)"
              class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center active:bg-gray-200 text-lg font-bold"
            >-</button>
            <input
              v-model.number="executeModal.count"
              type="number"
              min="1"
              :max="maxExecutions(executeModal.set)"
              class="w-20 text-center text-xl font-bold border rounded-xl py-2"
            />
            <button
              @click="executeModal.count = Math.min(maxExecutions(executeModal.set), executeModal.count + 1)"
              class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center active:bg-gray-200 text-lg font-bold"
            >+</button>
          </div>
        </div>

        <!-- Consumption preview -->
        <div class="bg-gray-50 rounded-xl p-3 mb-4">
          <p class="text-xs font-bold text-gray-400 uppercase mb-2">Spotreba materiálu</p>
          <div v-for="item in executeModal.set?.items" :key="item.id" class="flex justify-between text-sm py-1">
            <span class="text-gray-600">{{ item.gardenItem.name }}</span>
            <span class="font-medium text-gray-800">-{{ (item.quantity * executeModal.count).toFixed(0) }} {{ item.gardenItem.unit }}</span>
          </div>
        </div>

        <div class="flex gap-2">
          <button
            @click="executeModal.show = false"
            class="flex-1 bg-gray-100 text-gray-700 py-3 rounded-xl font-medium active:bg-gray-200"
          >
            Zrušiť
          </button>
          <button
            @click="confirmExecute"
            :disabled="executeModal.loading || executeModal.count < 1"
            class="flex-1 bg-[#2d6a4f] text-white py-3 rounded-xl font-bold active:bg-[#40916c] disabled:opacity-50"
          >
            <span v-if="executeModal.loading">Vykonávam...</span>
            <span v-else>Vysadiť</span>
          </button>
        </div>

        <p v-if="executeModal.error" class="text-red-500 text-sm mt-3 text-center">{{ executeModal.error }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeft, Flower2, TreePine, LogOut,
  Package, Layers, Plus, History
} from 'lucide-vue-next'
import { useGardenStore } from '../stores/garden'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const gardenStore = useGardenStore()
const authStore = useAuthStore()

const loading = computed(() => gardenStore.loading)
const activeSets = computed(() => gardenStore.activeSets)
const recentMovements = computed(() => gardenStore.movements.slice(0, 4))

const maxExecutions = (set) => gardenStore.maxExecutions(set)

const executeModal = reactive({
  show: false,
  set: null,
  count: 1,
  loading: false,
  error: null
})

onMounted(async () => {
  await gardenStore.fetchDashboardData()
})

const openExecuteModal = (set) => {
  executeModal.set = set
  executeModal.count = 1
  executeModal.loading = false
  executeModal.error = null
  executeModal.show = true
}

const confirmExecute = async () => {
  executeModal.loading = true
  executeModal.error = null
  try {
    await gardenStore.executeSet(executeModal.set.id, executeModal.count)
    executeModal.show = false
  } catch (error) {
    executeModal.error = error.response?.data?.message || error.message || 'Chyba pri výsadbe'
  } finally {
    executeModal.loading = false
  }
}

const navigateBack = () => {
  router.push('/')
}

const handleLogout = async () => {
  await authStore.logout()
  await router.push('/login')
}

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
</script>
