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
        <button
            @click="navigateToSettings"
            class="w-10 h-10 rounded-xl bg-gray-50 flex items-center justify-center text-gray-400 active:bg-gray-100 transition-colors"
        >
          <Settings class="w-5 h-5" />
        </button>
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
          <p class="text-3xl font-extrabold mb-4">{{ totalItems }} položiek</p>

        <div class="flex gap-6">
          <div>
            <p class="text-[11px] text-green-200 uppercase tracking-wide">Tento týždeň</p>
            <p class="text-base font-bold">{{ weeklyIn }} naskladnené</p>
          </div>
          <div>
            <p class="text-[11px] text-green-200 uppercase tracking-wide">Predané</p>
            <p class="text-base font-bold">{{ weeklyOut }} položky</p>
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
            <p class="text-sm font-bold text-gray-700">Nová plodina</p>
            <p class="text-[11px] text-gray-400">Pridať do katalógu</p>
          </div>
        </button>
      </div>

      <!-- Recent Activity -->
      <div>
        <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Posledná aktivita</p>
        <div class="bg-white rounded-xl overflow-hidden shadow-sm divide-y divide-gray-100">
          <div
              v-for="item in recentActivity"
              :key="item.id"
              class="flex items-center p-4 gap-3"
          >
            <div
                class="w-2 h-2 rounded-full"
                :class="item.type === 'in' ? 'bg-green-500' : 'bg-amber-500'"
            ></div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-semibold text-gray-700 truncate">{{ item.name }}</p>
              <p class="text-[11px] text-gray-400">{{ item.time }}</p>
            </div>
            <p
                class="text-sm font-bold"
                :class="item.type === 'in' ? 'text-green-600' : 'text-amber-600'"
            >
              {{ item.type === 'in' ? '+' : '-' }}{{ item.amount }}
            </p>
          </div>

          <button
              v-if="recentActivity.length > 0"
              @click="navigateToHistory"
              class="w-full p-3 text-sm text-gray-500 font-medium active:bg-gray-50 transition-colors"
          >
            Zobraziť všetku históriu
          </button>

          <div v-if="recentActivity.length === 0" class="p-6 text-center">
            <p class="text-sm text-gray-400">Zatiaľ žiadna aktivita</p>
          </div>
        </div>
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
  Settings
} from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { computed, onMounted } from 'vue'
import { useStockStore } from '../stores/stock'

const router = useRouter()
const stockStore = useStockStore()

// Computed properties from store
const totalItems = computed(() => stockStore.totalStockItems)
const weeklyIn = computed(() => stockStore.weeklyInCount)
const weeklyOut = computed(() => stockStore.weeklyOutCount)
const recentActivity = computed(() => stockStore.formattedRecentMovements)
const loading = computed(() => stockStore.loading)

// Load data on mount
onMounted(async () => {
  await stockStore.fetchDashboardData()
})

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
</script>pozr