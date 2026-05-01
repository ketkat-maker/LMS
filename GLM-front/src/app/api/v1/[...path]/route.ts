import { NextRequest, NextResponse } from 'next/server'

const BACKEND_URL = 'http://localhost:8080'

async function proxyRequest(request: NextRequest, method: string) {
  try {
    const url = new URL(request.url)
    const pathSegments = url.pathname.replace('/api/v1/', '')
    const queryString = url.searchParams.toString()
    const targetUrl = `${BACKEND_URL}/api/v1/${pathSegments}${queryString ? `?${queryString}` : ''}`

    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    }

    // Forward Authorization header if present
    const authHeader = request.headers.get('Authorization')
    if (authHeader) {
      headers['Authorization'] = authHeader
    }

    const fetchOptions: RequestInit = {
      method,
      headers,
    }

    if (method !== 'GET' && method !== 'HEAD') {
      const body = await request.text()
      if (body) {
        fetchOptions.body = body
      }
    }

    const response = await fetch(targetUrl, fetchOptions)

    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      const data = await response.json()
      return NextResponse.json(data, { status: response.status })
    } else {
      const text = await response.text()
      return new NextResponse(text, {
        status: response.status,
        headers: { 'Content-Type': contentType || 'text/plain' },
      })
    }
  } catch (error) {
    console.error('Proxy error:', error)
    return NextResponse.json(
      { message: 'Failed to connect to backend server. Make sure Spring Boot is running on port 8080.' },
      { status: 502 }
    )
  }
}

export async function GET(request: NextRequest) {
  return proxyRequest(request, 'GET')
}

export async function POST(request: NextRequest) {
  return proxyRequest(request, 'POST')
}

export async function PUT(request: NextRequest) {
  return proxyRequest(request, 'PUT')
}

export async function DELETE(request: NextRequest) {
  return proxyRequest(request, 'DELETE')
}

export async function PATCH(request: NextRequest) {
  return proxyRequest(request, 'PATCH')
}

// Handle CORS preflight requests from the browser
export async function OPTIONS(request: NextRequest) {
  return new NextResponse(null, {
    status: 204,
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization',
      'Access-Control-Max-Age': '86400',
    },
  })
}
