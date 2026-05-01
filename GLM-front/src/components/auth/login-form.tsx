'use client'

import { useState } from 'react'
import { authApi, ApiError, isMockMode } from '@/lib/api'
import { useAuthStore } from '@/lib/auth-store'
import { DEMO_CREDENTIALS } from '@/lib/mock-api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { useToast } from '@/hooks/use-toast'
import { LogIn, Loader2, Mail, Lock } from 'lucide-react'

interface LoginFormProps {
  onSwitchToSignUp: () => void
  onSwitchToResetPassword: () => void
}

export function LoginForm({ onSwitchToSignUp, onSwitchToResetPassword }: LoginFormProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({})
  const login = useAuthStore((s) => s.login)
  const { toast } = useToast()
  const mockMode = isMockMode()

  const validate = () => {
    const newErrors: { email?: string; password?: string } = {}
    if (!email.trim()) {
      newErrors.email = 'Email is required'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = 'Please enter a valid email'
    }
    if (!password) {
      newErrors.password = 'Password is required'
    }
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate()) return

    setIsLoading(true)
    try {
      const response = await authApi.login({
        logInEmail: email,
        logInPassword: password,
      })
      login({
        logInId: response.logInId,
        fullName: response.fullName,
        logInEmail: response.logInEmail,
        role: response.role,
        token: response.token,
      })
      toast({
        title: 'Welcome back!',
        description: `Logged in as ${response.fullName}`,
      })
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Login failed. Please try again.'
      toast({
        title: 'Login Failed',
        description: message,
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleDemoLogin = (credential: typeof DEMO_CREDENTIALS[0]) => {
    setEmail(credential.email)
    setPassword(credential.password)
  }

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader className="space-y-1 text-center">
        <div className="flex justify-center mb-2">
          <div className="rounded-full bg-primary p-3">
            <LogIn className="h-6 w-6 text-primary" />
          </div>
        </div>
        <CardTitle className="text-2xl font-bold">Welcome Back</CardTitle>
        <CardDescription>Sign in to your LMS account</CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="login-email">Email</Label>
            <div className="relative">
              <Mail className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                id="login-email"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="pl-10"
                disabled={isLoading}
              />
            </div>
            {errors.email && (
              <p className="text-sm text-destructive">{errors.email}</p>
            )}
          </div>
          <div className="space-y-2">
            <Label htmlFor="login-password">Password</Label>
            <div className="relative">
              <Lock className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                id="login-password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="pl-10"
                disabled={isLoading}
              />
            </div>
            {errors.password && (
              <p className="text-sm text-destructive">{errors.password}</p>
            )}
          </div>
          <button
            type="button"
            onClick={onSwitchToResetPassword}
            className="text-sm text-muted-foreground hover:text-primary transition-colors"
          >
            Forgot your password?
          </button>

          {/* Demo Credentials */}
          <div className="rounded-lg border bg-muted/50 p-4 space-y-3">
            <p className="text-sm font-medium text-center">
              🎮 Demo Accounts
            </p>
            <p className="text-xs text-muted-foreground text-center">
              Backend not connected — using demo data. Click to auto-fill:
            </p>
            <div className="grid grid-cols-3 gap-2">
              {DEMO_CREDENTIALS.map((cred) => (
                <button
                  key={cred.role}
                  type="button"
                  onClick={() => handleDemoLogin(cred)}
                  className="flex flex-col items-center gap-1 rounded-md border bg-background p-2 hover:bg-accent transition-colors text-xs"
                >
                  <span className="font-medium">{cred.role}</span>
                  <span className="text-muted-foreground truncate w-full text-center">
                    {cred.email}
                  </span>
                </button>
              ))}
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex flex-col space-y-4">
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Signing in...
              </>
            ) : (
              'Sign In'
            )}
          </Button>
          <p className="text-sm text-muted-foreground text-center">
            Don&apos;t have an account?{' '}
            <button
              type="button"
              onClick={onSwitchToSignUp}
              className="text-primary hover:underline font-medium"
            >
              Sign up
            </button>
          </p>
        </CardFooter>
      </form>
    </Card>
  )
}
