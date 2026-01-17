<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50 flex flex-col">
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
            <span>🌱</span> Môj sklad
          </h1>
        </div>
        <div class="flex items-center gap-2">
          <button
              v-if="settingsEnabled"
              @click="navigateToSettings"
              class="w-10 h-10 rounded-xl bg-gray-50 flex items-center justify-center text-gray-400 active:bg-gray-100 transition-colors"
          >
            <Settings class="w-5 h-5" />
          </button>
          <button
              @click="handleLogout"
              class="w-10 h-10 rounded-xl bg-red-50 flex items-center justify-center text-red-500 active:bg-red-100 transition-colors"
              title="Odhlásiť sa"
          >
            <LogOut class="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4 space-y-5 overflow-auto pb-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>

      <!-- Content -->
      <template v-else>
        <!-- Summary Card -->
        <div class="bg-gradient-to-br from-green-600 to-green-700 rounded-2xl p-5 text-white relative overflow-hidden">
          <div class="absolute -top-10 -right-10 w-32 h-32 bg-white/10 rounded-full"></div>
          <div class="absolute bottom-3 right-4 text-4xl opacity-30">🌾</div>

          <p class="text-green-100 text-sm">Celkom na sklade</p>
          <p class="text-3xl font-extrabold mb-4">{{ totalItems }} {{ getItemsLabel(totalItems) }}</p>

        <div class="flex gap-6">
          <div>
            <p class="text-[11px] text-green-200 uppercase tracking-wide">Tento týždeň</p>
            <p class="text-base font-bold">{{ weeklyIn }} naskladnené</p>
          </div>
          <div>
            <p class="text-[11px] text-green-200 uppercase tracking-wide">Predané</p>
            <p class="text-base font-bold">{{ weeklyOut }} {{ getItemsLabel(weeklyOut) }}</p>
          </div>
        </div>
      </div>

      <!-- Main Actions -->
      <div>
        <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Rýchle akcie</p>
        <div class="grid grid-cols-2 gap-3">
          <button
              @click="navigateToStockIn"
              class="bg-white rounded-2xl p-5 flex flex-col items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
          >
            <div class="w-14 h-14 rounded-xl bg-gradient-to-br from-green-100 to-green-200 flex items-center justify-center">
              <ArrowDownToLine class="w-7 h-7 text-green-600" />
            </div>
            <div class="text-center">
              <p class="text-base font-bold text-gray-700">Naskladniť</p>
              <p class="text-xs text-gray-400">Pridať úrodu</p>
            </div>
          </button>

          <button
              @click="navigateToStockOut"
              class="bg-white rounded-2xl p-5 flex flex-col items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
          >
            <div class="w-14 h-14 rounded-xl bg-gradient-to-br from-amber-50 to-amber-100 flex items-center justify-center">
              <ArrowUpFromLine class="w-7 h-7 text-amber-600" />
            </div>
            <div class="text-center">
              <p class="text-base font-bold text-gray-700">Vyskladniť</p>
              <p class="text-xs text-gray-400">Predaj / výber</p>
            </div>
          </button>
        </div>
      </div>

      <!-- Secondary Actions -->
      <div class="grid grid-cols-2 gap-3">
        <button
            @click="navigateToBrowser"
            class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
        >
          <div class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center">
            <Package class="w-5 h-5 text-gray-500" />
          </div>
          <div class="text-left">
            <p class="text-sm font-bold text-gray-700">Zásoby</p>
            <p class="text-[11px] text-gray-400">Prehľad skladu</p>
          </div>
        </button>

        <button
            @click="navigateToCreate"
            class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
        >
          <div class="w-10 h-10 rounded-xl bg-gray-100 flex items-center justify-center">
            <Plus class="w-5 h-5 text-gray-500" />
          </div>
          <div class="text-left">
            <p class="text-sm font-bold text-gray-700">Nová úroda</p>
            <p class="text-[11px] text-gray-400">Naskladniť úrodu</p>
          </div>
        </button>
      </div>

      <!-- Tertiary Actions -->
      <div class="grid grid-cols-2 gap-3">
        <button
            @click="navigateToHistory"
            class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
        >
          <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center">
            <Package class="w-5 h-5 text-blue-600" />
          </div>
          <div class="text-left">
            <p class="text-sm font-bold text-gray-700">História</p>
            <p class="text-[11px] text-gray-400">Všetky pohyby</p>
          </div>
        </button>

        <button
            v-if="deletedStocksCount > 0"
            @click="navigateToDeleted"
            class="bg-white rounded-xl p-4 flex items-center gap-3 shadow-sm active:scale-[0.98] transition-all"
        >
          <div class="w-10 h-10 rounded-xl bg-orange-100 flex items-center justify-center">
            <Archive class="w-5 h-5 text-orange-600" />
          </div>
          <div class="text-left">
            <p class="text-sm font-bold text-gray-700">Archivované</p>
            <p class="text-[11px] text-gray-400">{{ deletedStocksCount }} {{ getDeletedItemsLabel(deletedStocksCount) }}</p>
          </div>
        </button>
      </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import {
  ArrowLeft,
  ArrowDownToLine,
  ArrowUpFromLine,
  Package,
  Plus,
  Settings,
  Archive,
  LogOut
} from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { computed, onMounted, ref } from 'vue'
import { useStockStore } from '../stores/stock'
import { useAuthStore } from '../stores/auth'
import { stockApi } from '../services/api'
import { features } from '../config/features'

const router = useRouter()
const stockStore = useStockStore()
const authStore = useAuthStore()

// Computed properties from store
const totalItems = computed(() => stockStore.totalStockItems)
const weeklyIn = computed(() => stockStore.weeklyInCount)
const weeklyOut = computed(() => stockStore.weeklyOutCount)
const loading = computed(() => stockStore.loading)

// Feature flags
const settingsEnabled = features.settings.enabled

// Deleted stocks count
const deletedStocksCount = ref(0)

// Load data on mount
onMounted(async () => {
  await stockStore.fetchDashboardData()
  await loadDeletedStocksCount()
})

const loadDeletedStocksCount = async () => {
  try {
    const response = await stockApi.getDeletedStocks()
    deletedStocksCount.value = response.data.length
  } catch (error) {
    console.error('Failed to load deleted stocks count:', error)
    deletedStocksCount.value = 0
  }
}

// Navigation
const navigateBack = () => {
  router.push('/')
}

const navigateToStockIn = () => {
  router.push('/scan?mode=in')
}

const navigateToStockOut = () => {
  router.push('/scan?mode=out')
}

const navigateToBrowser = () => {
  router.push('/stocks')
}

const navigateToCreate = () => {
  router.push('/create-item')
}

const navigateToHistory = () => {
  router.push('/history')
}

const navigateToSettings = () => {
  router.push('/settings')
}

const navigateToDeleted = () => {
  router.push('/deleted-stocks')
}

const handleLogout = async () => {
  await authStore.logout()
  await router.push('/login')
}

// Slovak declension helper
const getItemsLabel = (count) => {
  if (count === 1) return 'položka'
  if (count >= 2 && count <= 4) return 'položky'
  return 'položiek'
}

const getDeletedItemsLabel = (count) => {
  if (count === 1) return 'skrytá položka'
  if (count >= 2 && count <= 4) return 'skryté položky'
  return 'skrytých položiek'
}
</script>