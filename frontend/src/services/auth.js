import apiClient from './api'

const TOKEN_KEY = 'auth_token'

export const authService = {
  // Login user
  async login(username, password) {
    const response = await apiClient.post('/api/auth/login', {
      username,
      password
    })
    if (response.data.token) {
      this.setToken(response.data.token)
    }
    return response.data
  },

  // Logout user
  logout() {
    localStorage.removeItem(TOKEN_KEY)
  },

  // Get current user
  getCurrentUser() {
    return apiClient.get('/api/auth/me')
  },

  // Get token from localStorage
  getToken() {
    return localStorage.getItem(TOKEN_KEY)
  },

  // Set token in localStorage
  setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
  },

  // Check if user is authenticated
  isAuthenticated() {
    const token = this.getToken()
    return !!token
  }
}

export default authService
