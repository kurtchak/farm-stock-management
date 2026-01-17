import { defineStore } from 'pinia'
import authService from '../services/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: authService.getToken(),
    isAuthenticated: authService.isAuthenticated(),
    loading: false,
    error: null
  }),

  getters: {
    currentUser: (state) => state.user,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isWorker: (state) => state.user?.role === 'WORKER'
  },

  actions: {
    async login(username, password) {
      this.loading = true
      this.error = null
      try {
        const response = await authService.login(username, password)
        this.token = response.token
        this.isAuthenticated = true

        // Fetch user details after login
        await this.fetchCurrentUser()

        return response
      } catch (error) {
        this.error = error.response?.data?.message || 'Prihlásenie zlyhalo'
        this.isAuthenticated = false
        this.token = null
        this.user = null
        throw error
      } finally {
        this.loading = false
      }
    },

    async logout() {
      authService.logout()
      this.user = null
      this.token = null
      this.isAuthenticated = false
    },

    async fetchCurrentUser() {
      if (!this.isAuthenticated) {
        return
      }

      this.loading = true
      this.error = null
      try {
        const response = await authService.getCurrentUser()
        this.user = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Načítanie používateľa zlyhalo'
        // If fetching user fails, logout
        await this.logout()
        throw error
      } finally {
        this.loading = false
      }
    },

    async checkAuth() {
      const token = authService.getToken()
      if (token) {
        this.token = token
        this.isAuthenticated = true
        try {
          await this.fetchCurrentUser()
        } catch (error) {
          // Token is invalid, logout
          await this.logout()
        }
      } else {
        this.isAuthenticated = false
      }
    }
  }
})
