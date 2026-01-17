<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 via-emerald-50 to-teal-50 flex flex-col items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Logo/Title Section -->
      <div class="text-center mb-8">
        <div class="text-6xl mb-4">🌱</div>
        <h1 class="text-3xl font-extrabold text-gray-800 mb-2">Môj sklad</h1>
        <p class="text-sm text-gray-500">Prihlásenie do systému</p>
      </div>

      <!-- Login Form -->
      <div class="bg-white rounded-2xl shadow-lg p-6 space-y-4">
        <form @submit.prevent="handleLogin" class="space-y-4">
          <!-- Username Input -->
          <div>
            <label for="username" class="block text-sm font-semibold text-gray-700 mb-2">
              Používateľské meno
            </label>
            <input
              id="username"
              v-model="username"
              type="text"
              required
              autocomplete="username"
              :disabled="loading"
              class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-green-500 focus:ring-2 focus:ring-green-200 outline-none transition-all disabled:bg-gray-50 disabled:cursor-not-allowed"
              placeholder="Zadajte používateľské meno"
            />
          </div>

          <!-- Password Input -->
          <div>
            <label for="password" class="block text-sm font-semibold text-gray-700 mb-2">
              Heslo
            </label>
            <input
              id="password"
              v-model="password"
              type="password"
              required
              autocomplete="current-password"
              :disabled="loading"
              class="w-full px-4 py-3 rounded-xl border border-gray-200 focus:border-green-500 focus:ring-2 focus:ring-green-200 outline-none transition-all disabled:bg-gray-50 disabled:cursor-not-allowed"
              placeholder="Zadajte heslo"
            />
          </div>

          <!-- Error Message -->
          <div v-if="error" class="bg-red-50 border border-red-200 rounded-xl p-3">
            <p class="text-sm text-red-600 text-center">{{ error }}</p>
          </div>

          <!-- Login Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-gradient-to-r from-green-600 to-green-700 text-white font-bold py-3 rounded-xl shadow-md active:scale-[0.98] transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:active:scale-100"
          >
            <span v-if="loading" class="flex items-center justify-center gap-2">
              <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
              Prihlasovanie...
            </span>
            <span v-else>Prihlásiť sa</span>
          </button>
        </form>

        <!-- Test Credentials Info -->
        <div class="mt-6 pt-6 border-t border-gray-100">
          <p class="text-xs text-gray-400 text-center mb-2">Testovacie údaje:</p>
          <div class="space-y-1 text-xs text-gray-500">
            <p class="text-center"><span class="font-semibold">Admin:</span> admin / password</p>
            <p class="text-center"><span class="font-semibold">Worker:</span> worker1 / password</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref(null)

const handleLogin = async () => {
  loading.value = true
  error.value = null

  try {
    await authStore.login(username.value, password.value)
    // Redirect to farm page on success
    router.push('/farm')
  } catch (err) {
    error.value = err.response?.data?.message || 'Nesprávne prihlasovacie údaje'
  } finally {
    loading.value = false
  }
}
</script>
