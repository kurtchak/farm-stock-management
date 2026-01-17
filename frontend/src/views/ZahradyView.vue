<template>
  <div class="min-h-screen bg-gradient-to-br from-[#e8f5e9] to-[#c8e6c9] flex flex-col">
    <!-- Header with back button -->
    <div class="bg-white shadow-md p-4">
      <div class="flex items-center">
        <button
          @click="navigateBack"
          class="flex items-center text-gray-600 active:text-gray-800 transition-colors p-2"
        >
          <ArrowLeft class="w-5 h-5 mr-2" />
          <span class="text-base">Späť</span>
        </button>
        <h1 class="text-xl font-bold text-gray-800 ml-2">Záhrady</h1>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 flex items-center justify-center p-6">
      <div class="text-center w-full max-w-md">
        <!-- Garden illustration -->
        <div class="mb-6">
          <div class="w-32 h-32 mx-auto bg-white rounded-full shadow-xl flex items-center justify-center">
            <Flower2 class="w-20 h-20 text-[#66bb6a]" />
          </div>
        </div>

        <h2 class="text-2xl sm:text-3xl font-bold text-gray-800 mb-3">
          Sekcia Záhrady
        </h2>

        <p class="text-base sm:text-lg text-gray-600 mb-6">
          Táto sekcia je momentálne vo vývoji
        </p>

        <div class="bg-white rounded-lg shadow-lg p-5">
          <div class="flex items-start space-x-3">
            <Sprout class="w-7 h-7 text-[#81c784] flex-shrink-0 mt-1" />
            <div class="text-left">
              <h3 class="font-semibold text-gray-800 mb-2 text-base">Pripravované funkcie:</h3>
              <ul class="text-gray-600 space-y-1 text-sm">
                <li>• Správa záhradných plôch ?</li>
                <li>• Sledovanie výsadby a starostlivosti ?</li>
                <li>• Plánovanie záhradných prác ?</li>
                <li>• Evidencia nástrojov a materiálu ?</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Flower2, Sprout, ArrowLeft } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateBack = () => {
  router.push('/')
}
</script>