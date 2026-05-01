'use client'

import { useState } from 'react'
import { useAuthStore } from '@/lib/auth-store'
import { type Role } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Sheet, SheetContent, SheetTrigger, SheetTitle } from '@/components/ui/sheet'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { isMockMode } from '@/lib/api'
import { useTheme } from 'next-themes'
import {
  GraduationCap,
  LogOut,
  Menu,
  Moon,
  Sun,
  BookOpen,
  Users,
  LayoutDashboard,
  ClipboardList,
  UserCog,
  FlaskConical,
} from 'lucide-react'

export type ViewType =
  | 'overview'
  | 'courses'
  | 'enrollments'
  | 'manage-courses'
  | 'manage-users'
  | 'student-enrollments'

interface NavItem {
  id: ViewType
  label: string
  icon: React.ReactNode
}

const navItemIcons: Record<string, React.ReactNode> = {
  overview: <LayoutDashboard className="h-4 w-4" />,
  courses: <BookOpen className="h-4 w-4" />,
  enrollments: <ClipboardList className="h-4 w-4" />,
  'manage-courses': <BookOpen className="h-4 w-4" />,
  'student-enrollments': <ClipboardList className="h-4 w-4" />,
  'manage-users': <UserCog className="h-4 w-4" />,
}

const navItemLabels: Record<string, string> = {
  overview: 'Dashboard',
  courses: 'Browse Courses',
  enrollments: 'My Enrollments',
  'manage-courses': 'My Courses',
  'student-enrollments': 'Enrollments',
  'manage-users': 'Manage Users',
}

function getNavItems(role: Role): NavItem[] {
  const keys: ViewType[] = (() => {
    switch (role) {
      case 'STUDENT':
        return ['overview', 'courses', 'enrollments']
      case 'INSTRUCTOR':
        return ['overview', 'manage-courses', 'student-enrollments']
      case 'ADMIN':
        return ['overview', 'manage-users', 'courses']
      default:
        return []
    }
  })()
  return keys.map((id) => ({
    id,
    label: navItemLabels[id],
    icon: navItemIcons[id],
  }))
}

function getRoleBadgeVariant(role: Role) {
  switch (role) {
    case 'ADMIN': return 'destructive' as const
    case 'INSTRUCTOR': return 'default' as const
    case 'STUDENT': return 'secondary' as const
    default: return 'outline' as const
  }
}

function getRoleIcon(role: Role) {
  switch (role) {
    case 'ADMIN': return <UserCog className="h-3 w-3" />
    case 'INSTRUCTOR': return <GraduationCap className="h-3 w-3" />
    case 'STUDENT': return <Users className="h-3 w-3" />
    default: return <Users className="h-3 w-3" />
  }
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

// Extracted sidebar as its own component to avoid creating components during render
interface SidebarContentProps {
  navItems: NavItem[]
  currentView: ViewType
  onViewChange: (view: ViewType) => void
  onNavClick?: () => void
  userName: string
  userEmail: string
}

function SidebarContent({ navItems, currentView, onViewChange, onNavClick, userName, userEmail }: SidebarContentProps) {
  return (
    <div className="flex flex-col h-full">
      <div className="p-4 flex items-center gap-3">
        <div className="rounded-lg bg-primary p-2">
          <GraduationCap className="h-5 w-5 text-primary-foreground" />
        </div>
        <div>
          <h2 className="font-bold text-lg leading-none">LMS</h2>
          <p className="text-xs text-muted-foreground">Learning Platform</p>
        </div>
      </div>
      <Separator />
      <ScrollArea className="flex-1 px-3 py-4">
        <nav className="space-y-1">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => {
                onViewChange(item.id)
                onNavClick?.()
              }}
              className={`w-full flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors ${
                currentView === item.id
                  ? 'bg-primary text-primary-foreground'
                  : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
              }`}
            >
              {item.icon}
              {item.label}
            </button>
          ))}
        </nav>
      </ScrollArea>
      <Separator />
      <div className="p-4">
        <div className="flex items-center gap-3">
          <Avatar className="h-9 w-9">
            <AvatarFallback className="bg-primary/10 text-primary text-sm font-semibold">
              {getInitials(userName)}
            </AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium truncate">{userName}</p>
            <p className="text-xs text-muted-foreground truncate">{userEmail}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

interface AppLayoutProps {
  children: React.ReactNode
  currentView: ViewType
  onViewChange: (view: ViewType) => void
}

export function AppLayout({ children, currentView, onViewChange }: AppLayoutProps) {
  const { user, logout } = useAuthStore()
  const { theme, setTheme } = useTheme()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  if (!user) return null

  const navItems = getNavItems(user.role)

  return (
    <div className="min-h-screen flex flex-col bg-background">
      {/* Demo Mode Banner */}
      {isMockMode() && (
        <div className="bg-amber-500/90 text-amber-950 text-center py-1.5 px-4 text-sm font-medium flex items-center justify-center gap-2">
          <FlaskConical className="h-4 w-4" />
          Demo Mode — Backend not connected. Using sample data.
        </div>
      )}
      {/* Header */}
      <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="flex h-14 items-center px-4 gap-4">
          {/* Mobile menu trigger */}
          <Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon" className="md:hidden">
                <Menu className="h-5 w-5" />
                <span className="sr-only">Toggle menu</span>
              </Button>
            </SheetTrigger>
            <SheetContent side="left" className="p-0 w-72">
              <SheetTitle className="sr-only">Navigation Menu</SheetTitle>
              <SidebarContent
                navItems={navItems}
                currentView={currentView}
                onViewChange={onViewChange}
                onNavClick={() => setMobileMenuOpen(false)}
                userName={user.fullName}
                userEmail={user.logInEmail}
              />
            </SheetContent>
          </Sheet>

          {/* Logo for mobile */}
          <div className="flex items-center gap-2 md:hidden">
            <div className="rounded-md bg-primary p-1.5">
              <GraduationCap className="h-4 w-4 text-primary-foreground" />
            </div>
            <span className="font-bold">LMS</span>
          </div>

          {/* Spacer */}
          <div className="flex-1" />

          {/* Right side actions */}
          <div className="flex items-center gap-2">
            {/* Theme toggle */}
            <Button
              variant="ghost"
              size="icon"
              onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
              className="h-9 w-9"
            >
              <Sun className="h-4 w-4 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
              <Moon className="absolute h-4 w-4 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
              <span className="sr-only">Toggle theme</span>
            </Button>

            {/* User menu */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="flex items-center gap-2 h-9 px-2">
                  <Avatar className="h-7 w-7">
                    <AvatarFallback className="bg-primary/10 text-primary text-xs font-semibold">
                      {getInitials(user.fullName)}
                    </AvatarFallback>
                  </Avatar>
                  <span className="hidden sm:inline text-sm font-medium">{user.fullName}</span>
                  <Badge variant={getRoleBadgeVariant(user.role)} className="hidden sm:flex gap-1 text-[10px] px-1.5 py-0">
                    {getRoleIcon(user.role)}
                    {user.role}
                  </Badge>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <div className="flex items-center gap-2 p-2">
                  <Avatar className="h-8 w-8">
                    <AvatarFallback className="bg-primary/10 text-primary text-sm font-semibold">
                      {getInitials(user.fullName)}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex flex-col">
                    <p className="text-sm font-medium">{user.fullName}</p>
                    <p className="text-xs text-muted-foreground">{user.logInEmail}</p>
                  </div>
                </div>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={() => {
                    logout()
                  }}
                  className="text-destructive focus:text-destructive"
                >
                  <LogOut className="mr-2 h-4 w-4" />
                  Log out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </header>

      {/* Main content area */}
      <div className="flex flex-1">
        {/* Desktop sidebar */}
        <aside className="hidden md:flex w-64 border-r flex-col bg-background">
          <SidebarContent
            navItems={navItems}
            currentView={currentView}
            onViewChange={onViewChange}
            userName={user.fullName}
            userEmail={user.logInEmail}
          />
        </aside>

        {/* Page content */}
        <main className="flex-1 overflow-auto">
          {children}
        </main>
      </div>
    </div>
  )
}
