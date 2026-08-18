import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    nickname: localStorage.getItem('nickname') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    setLogin(data) {
      this.token = data.token
      this.username = data.username
      this.nickname = data.nickname
      localStorage.setItem('token', data.token)
      localStorage.setItem('username', data.username)
      localStorage.setItem('nickname', data.nickname)
    },
    logout() {
      this.token = ''
      this.username = ''
      this.nickname = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('nickname')
    }
  }
})
