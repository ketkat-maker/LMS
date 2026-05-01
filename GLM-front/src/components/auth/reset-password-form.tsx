'use client'

import { useState } from 'react'
import { authApi, ApiError } from '@/lib/api'
import { useAuthStore } from '@/lib/auth-store'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { useToast } from '@/hooks/use-toast'
import { KeyRound, Loader2, Mail, Lock } from 'lucide-react'

interface ResetPasswordFormProps {
  onSwitchToLogin: () => void
}

export function ResetPasswordForm({ onSwitchToLogin }: ResetPasswordFormProps) {
  const [userEmail, setUserEmail] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [errors, setErrors] = useState<{ userEmail?: string; newPassword?: string }>({})
  const login = useAuthStore((s) => s.login)
  const { toast } = useToast()

  const validatePassword = (pwd: string): string | undefined => {
    if (pwd.length < 8) return 'Password must be at least 8 characters'
    if (!/[A-Z]/.test(pwd)) return 'Password must contain an uppercase letter'
    if (!/[a-z]/.test(pwd)) return 'Password must contain a lowercase letter'
    if (!/[0-9]/.test(pwd)) return 'Password must contain a digit'
    return undefined
  }

  const validate = () => {
    const newErrors: { userEmail?: string; newPassword?: string } = {}
    if (!userEmail.trim()) {
      newErrors.userEmail = 'Email is required'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(userEmail)) {
      newErrors.userEmail = 'Please enter a valid email'
    }
    if (!newPassword) {
      newErrors.newPassword = 'New password is required'
    } else {
      const pwdError = validatePassword(newPassword)
      if (pwdError) newErrors.newPassword = pwdError
    }
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate()) return

    setIsLoading(true)
    try {
      const response = await authApi.resetPassword({
        userEmail,
        newPassword,
      })
      login({
        logInId: response.logInId,
        fullName: response.fullName,
        logInEmail: response.logInEmail,
        role: response.role,
        token: response.token,
      })
      toast({
        title: 'Password Reset!',
        description: 'Your password has been reset successfully.',
      })
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : 'Reset failed. Please try again.'
      toast({
        title: 'Reset Failed',
        description: message,
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader className="space-y-1 text-center">
        <div className="flex justify-center mb-2">
          <div className="rounded-full bg-primary/10 p-3">
            <KeyRound className="h-6 w-6 text-primary" />
          </div>
        </div>
        <CardTitle className="text-2xl font-bold">Reset Password</CardTitle>
        <CardDescription>Enter your email and new password</CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="reset-email">Email</Label>
            <div className="relative">
              <Mail className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                id="reset-email"
                type="email"
                placeholder="you@example.com"
                value={userEmail}
                onChange={(e) => setUserEmail(e.target.value)}
                className="pl-10"
                disabled={isLoading}
              />
            </div>
            {errors.userEmail && (
              <p className="text-sm text-destructive">{errors.userEmail}</p>
            )}
          </div>
          <div className="space-y-2">
            <Label htmlFor="reset-password">New Password</Label>
            <div className="relative">
              <Lock className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                id="reset-password"
                type="password"
                placeholder="Min 8 chars, uppercase, lowercase, digit"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="pl-10"
                disabled={isLoading}
              />
            </div>
            {errors.newPassword && (
              <p className="text-sm text-destructive">{errors.newPassword}</p>
            )}
          </div>
        </CardContent>
        <CardFooter className="flex flex-col space-y-4">
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Resetting password...
              </>
            ) : (
              'Reset Password'
            )}
          </Button>
          <p className="text-sm text-muted-foreground text-center">
            Remember your password?{' '}
            <button
              type="button"
              onClick={onSwitchToLogin}
              className="text-primary hover:underline font-medium"
            >
              Sign in
            </button>
          </p>
        </CardFooter>
      </form>
    </Card>
  )
}
