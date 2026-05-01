'use client'

import { useState } from 'react'
import { useAuthStore } from '@/lib/auth-store'
import { LoginForm } from '@/components/auth/login-form'
import { SignUpForm } from '@/components/auth/signup-form'
import { ResetPasswordForm } from '@/components/auth/reset-password-form'
import { AppLayout, type ViewType } from '@/components/layout/app-layout'
import { StudentDashboard } from '@/components/dashboard/student-dashboard'
import { InstructorDashboard } from '@/components/dashboard/instructor-dashboard'
import { AdminDashboard } from '@/components/dashboard/admin-dashboard'
import { ThemeProvider } from 'next-themes'
import { GraduationCap } from 'lucide-react'

type AuthView = 'login' | 'signup' | 'reset-password'

function AuthScreen() {
  const [authView, setAuthView] = useState<AuthView>('login')

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-background via-background to-muted/30 p-4">
      <div className="mb-8 text-center">
        <div className="flex items-center justify-center gap-3 mb-4">
          <div className="rounded-xl bg-primary p-3 shadow-lg">
            <GraduationCap className="h-8 w-8 text-primary-foreground" />
          </div>
          <h1 className="text-3xl font-bold">LMS</h1>
        </div>
        <p className="text-muted-foreground">Learning Management System</p>
      </div>

      {authView === 'login' && (
        <LoginForm
          onSwitchToSignUp={() => setAuthView('signup')}
          onSwitchToResetPassword={() => setAuthView('reset-password')}
        />
      )}
      {authView === 'signup' && (
        <SignUpForm onSwitchToLogin={() => setAuthView('login')} />
      )}
      {authView === 'reset-password' && (
        <ResetPasswordForm onSwitchToLogin={() => setAuthView('login')} />
      )}
    </div>
  )
}

function DashboardScreen() {
  const { user } = useAuthStore()
  const [currentView, setCurrentView] = useState<ViewType>('overview')

  if (!user) return null

  const renderDashboard = () => {
    switch (user.role) {
      case 'STUDENT':
        return <StudentDashboard activeView={currentView} />
      case 'INSTRUCTOR':
        return <InstructorDashboard activeView={currentView} />
      case 'ADMIN':
        return <AdminDashboard activeView={currentView} />
      default:
        return <StudentDashboard activeView={currentView} />
    }
  }

  return (
    <AppLayout currentView={currentView} onViewChange={setCurrentView}>
      {renderDashboard()}
    </AppLayout>
  )
}

export default function Home() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)

  return (
    <ThemeProvider attribute="class" defaultTheme="system" enableSystem disableTransitionOnChange>
      {isAuthenticated ? <DashboardScreen /> : <AuthScreen />}
    </ThemeProvider>
  )
}
