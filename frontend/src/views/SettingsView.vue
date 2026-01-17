<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <!-- Header with back button -->
    <div class="bg-white shadow-sm p-4">
      <div class="flex items-center max-w-5xl mx-auto">
        <button
          @click="navigateBack"
          class="flex items-center text-gray-500 active:text-gray-700 transition-colors p-2 -ml-2"
        >
          <ArrowLeft class="w-5 h-5" />
        </button>
        <h1 class="text-xl font-bold text-gray-800 ml-2">Nastavenia</h1>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 flex items-center justify-center p-4">
      <div class="max-w-md w-full text-center">
        <div class="bg-white rounded-lg shadow-sm p-8">
          <div class="mb-4">
            <Settings class="w-16 h-16 mx-auto text-gray-400" />
          </div>
          <h2 class="text-xl font-bold text-gray-800 mb-2">Nastavenia</h2>
          <p class="text-gray-600 mb-4">
            Táto sekcia je momentálne vo vývoji. Nastavenia aplikácie budú čoskoro k dispozícii.
          </p>
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 text-left">
            <p class="text-sm text-blue-800">
              <strong>ℹ️ Plánované funkcie:</strong>
            </p>
            <ul class="text-sm text-blue-700 mt-2 space-y-1 list-disc list-inside">
              <li>Správa používateľského profilu</li>
              <li>Notifikácie a upozornenia</li>
              <li>Zálohovanie údajov</li>
              <li>Exporty a reporty</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ArrowLeft, Settings } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateBack = () => {
  router.push('/farm')
}
</script>