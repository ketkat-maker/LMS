'use client'

import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { Role } from '@/lib/api'

export interface AuthUser {
  logInId: string
  fullName: string
  logInEmail: string
  role: Role
  token: string
}

interface AuthState {
  user: AuthUser | null
  isAuthenticated: boolean
  login: (user: AuthUser) => void
  logout: () => void
  updateToken: (token: string) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      login: (user: AuthUser) =>
        set({
          user,
          isAuthenticated: true,
        }),
      logout: () =>
        set({
          user: null,
          isAuthenticated: false,
        }),
      updateToken: (token: string) =>
        set((state) => ({
          user: state.user ? { ...state.user, token } : null,
        })),
    }),
    {
      name: 'lms-auth-storage',
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)
