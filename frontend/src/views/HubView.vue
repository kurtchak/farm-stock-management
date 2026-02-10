<template>
  <div class="min-h-screen bg-gradient-to-br from-[#e8f5e9] to-[#c8e6c9] flex items-center justify-center p-6">
    <div class="w-full max-w-md">
      <div class="flex items-center justify-between mb-8">
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-800">
          Vitajte v Správe Podniku
        </h1>
        <button
          @click="handleLogout"
          class="w-10 h-10 rounded-xl bg-white/80 flex items-center justify-center text-red-500 active:bg-red-50 transition-colors shadow-sm"
          title="Odhlasiť sa"
        >
          <LogOut class="w-5 h-5" />
        </button>
      </div>

      <div class="flex flex-col gap-6">
        <!-- Zahrady Button -->
        <button
          @click="navigateToZahrady"
          class="group relative overflow-hidden bg-white rounded-2xl shadow-xl
                 active:scale-95 transition-all duration-300
                 p-8 flex flex-col items-center justify-center min-h-[200px]"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-[#81c784] to-[#66bb6a]
                      opacity-10 group-active:opacity-20 transition-opacity duration-300"></div>

          <div class="relative z-10 flex flex-col items-center">
            <div class="w-20 h-20 mb-4 bg-[#81c784] rounded-full flex items-center justify-center
                        group-active:bg-[#66bb6a] transition-colors duration-300 shadow-lg">
              <Flower2 class="w-10 h-10 text-white" />
            </div>

            <h2 class="text-2xl font-bold text-gray-800 mb-1">Záhrady</h2>
            <p class="text-gray-600 text-center text-sm">Správa záhrad a záhradníctva</p>
          </div>
        </button>

        <!-- Farma Button -->
        <button
          @click="navigateToFarma"
          class="group relative overflow-hidden bg-white rounded-2xl shadow-xl
                 active:scale-95 transition-all duration-300
                 p-8 flex flex-col items-center justify-center min-h-[200px]"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-[#94c1e8] to-[#7eb3e2]
                      opacity-10 group-active:opacity-20 transition-opacity duration-300"></div>

          <div class="relative z-10 flex flex-col items-center">
            <div class="w-20 h-20 mb-4 bg-[#94c1e8] rounded-full flex items-center justify-center
                        group-active:bg-[#7eb3e2] transition-colors duration-300 shadow-lg">
              <Wheat class="w-10 h-10 text-white" />
            </div>

            <h2 class="text-2xl font-bold text-gray-800 mb-1">Farma</h2>
            <p class="text-gray-600 text-center text-sm">Správa skladu a úrody</p>
          </div>
        </button>

      </div>
    </div>
  </div>
</template>

<script setup>
import { Flower2, Wheat, LogOut } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const navigateToZahrady = () => {
  router.push('/gardens')
}

const navigateToFarma = () => {
  router.push('/farm')
}

const handleLogout = async () => {
  await authStore.logout()
  await router.push('/login')
}

</script>