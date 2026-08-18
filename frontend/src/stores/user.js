import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    nickname: localStorage.getItem('nickname') || '',
    avatar: localStorage.getItem('avatar') || '',
    role: Number(localStorage.getItem('role') || 0)
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 1,
    displayName: (state) => state.nickname || state.username
  },
  actions: {
    setLogin(data) {
      this.token = data.token
      this.username = data.username
      this.nickname = data.nickname
      this.avatar = data.avatar
      this.role = data.role
      localStorage.setItem('token', data.token)
      localStorage.setItem('username', data.username)
      localStorage.setItem('nickname', data.nickname)
      localStorage.setItem('avatar', data.avatar || '')
      localStorage.setItem('role', data.role)
    },
    logout() {
      this.token = ''
      this.username = ''
      this.nickname = ''
      this.avatar = ''
      this.role = 0
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('nickname')
      localStorage.removeItem('avatar')
      localStorage.removeItem('role')
    }
  }
})
