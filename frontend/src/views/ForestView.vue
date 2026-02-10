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
            <TreePine class="w-5 h-5 text-green-700" /> Les
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
        <!-- Summary Card -->
        <div class="bg-gradient-to-br from-[#2d6a4f] to-[#40916c] rounded-2xl p-5 text-white relative overflow-hidden">
          <div class="absolute -top-10 -right-10 w-32 h-32 bg-white/10 rounded-full"></div>

          <p class="text-green-200 text-sm">Celkom materiálu</p>
          <p class="text-3xl font-extrabold mb-4">{{ statistics.totalItems }} {{ getItemsLabel(statistics.totalItems) }}</p>

          <div class="grid grid-cols-2 gap-3 text-sm">
            <div class="bg-white/10 rounded-lg p-2">
              <p class="text-green-200 text-[11px] uppercase tracking-wide">Stromy</p>
              <p class="font-bold">{{ statistics.treeCount }}</p>
            </div>
            <div class="bg-white/10 rounded-lg p-2">
              <p class="text-green-200 text-[11px] uppercase tracking-wide">Kolíky</p>
              <p class="font-bold">{{ statistics.stakeCount }}</p>
            </div>
            <div class="bg-white/10 rounded-lg p-2">
              <p class="text-green-200 text-[11px] uppercase tracking-wide">Ochrana</p>
              <p class="font-bold">{{ statistics.protectionCount }}</p>
            </div>
            <div class="bg-white/10 rounded-lg p-2">
              <p class="text-green-200 text-[11px] uppercase tracking-wide">Ostatné</p>
              <p class="font-bold">{{ statistics.otherCount }}</p>
            </div>
          </div>

          <div v-if="statistics.lowStockCount > 0" class="mt-3 bg-red-500/30 rounded-lg px-3 py-2 flex items-center gap-2">
            <AlertTriangle class="w-4 h-4" />
            <span class="text-sm font-medium">{{ statistics.lowStockCount }} {{ statistics.lowStockCount === 1 ? 'položka' : 'položiek' }} s nízkym stavom</span>
          </div>
        </div>

        <!-- Planting Sets - Quick Actions -->
        <div v-if="activeSets.length > 0">
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Sadby - rýchle akcie</p>
          <div class="space-y-3">
            <div
              v-for="set in activeSets"
              :key="set.id"
              class="bg-white rounded-2xl shadow-sm p-4"
            >
              <div class="flex items-center justify-between mb-2">
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-full" :style="{ backgroundColor: set.color }"></div>
                  <h3 class="font-bold text-gray-800">{{ set.name }}</h3>
                </div>
                <span class="text-xs text-gray-400">max {{ maxExecutions(set) }}x</span>
              </div>

              <div class="text-xs text-gray-500 mb-3">
                <span v-for="(item, idx) in set.items" :key="item.id">
                  {{ item.forestItem.name }} ({{ item.quantity }} {{ item.forestItem.unit }}){{ idx < set.items.length - 1 ? ', ' : '' }}
                </span>
              </div>

              <button
                @click="openExecuteModal(set)"
                :disabled="maxExecutions(set) === 0"
                class="w-full bg-[#2d6a4f] text-white py-2.5 rounded-xl font-semibold active:bg-[#40916c] transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
              >
                Vysadiť
              </button>
            </div>
          </div>
        </div>

        <!-- Management Actions -->
        <div>
          <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">Správa</p>
          <div class="grid grid-cols-2 gap-3">
            <button
              @click="router.push('/forest/items')"
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
              @click="router.push('/forest/sets')"
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
              @click="router.push('/forest/items/create')"
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
              @click="router.push('/forest/history')"
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
            <span class="text-gray-600">{{ item.forestItem.name }}</span>
            <span class="font-medium text-gray-800">-{{ (item.quantity * executeModal.count).toFixed(0) }} {{ item.forestItem.unit }}</span>
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
  ArrowLeft, TreePine, LogOut, AlertTriangle,
  Package, Layers, Plus, History
} from 'lucide-vue-next'
import { useForestStore } from '../stores/forest'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const forestStore = useForestStore()
const authStore = useAuthStore()

const loading = computed(() => forestStore.loading)
const statistics = computed(() => forestStore.statistics)
const activeSets = computed(() => forestStore.activeSets)

const maxExecutions = (set) => forestStore.maxExecutions(set)

const executeModal = reactive({
  show: false,
  set: null,
  count: 1,
  loading: false,
  error: null
})

onMounted(async () => {
  await forestStore.fetchDashboardData()
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
    await forestStore.executeSet(executeModal.set.id, executeModal.count)
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

const getItemsLabel = (count) => {
  if (count === 1) return 'položka'
  if (count >= 2 && count <= 4) return 'položky'
  return 'položiek'
}
</script>
